package org.kumoricon.registration.utility;

import com.google.gson.*;

import org.kumoricon.registration.helpers.FieldCleaner;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.tillsession.TillSession;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
class AttendeeImporterService {
    private final TillSessionService sessionService;

    private final OrderRepository orderRepository;
    private final AttendeeRepository attendeeRepository;
    private final BadgeService badgeService;

    private final UserRepository userRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(AttendeeImporterService.class);

    AttendeeImporterService(TillSessionService sessionService, OrderRepository orderRepository, AttendeeRepository attendeeRepository, BadgeService badgeService, UserRepository userRepository) {
        this.sessionService = sessionService;
        this.orderRepository = orderRepository;
        this.attendeeRepository = attendeeRepository;
        this.badgeService = badgeService;
        this.userRepository = userRepository;
    }

    private HashMap<String, Badge> getBadgeHashMap() {
        HashMap<String, Badge> badges = new HashMap<>();
        for (Badge b : badgeService.findAll()) {
            badges.put(b.getName(), b);
        }
        return badges;
    }

    private HashMap<String, Order> getOrderHashMap() {
        HashMap<String, Order> orders = new HashMap<>();
        for (Order o : orderRepository.findAll()) {
            orders.put(o.getOrderId(), o);
        }
        return orders;
    }

    private String generateBadgeNumber(Integer badgeNumber) {
        return String.format("ONL%1$05d", badgeNumber);
    }

    private List<AttendeeRecord> loadFile(BufferedReader bufferedReader) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE)).create();
        AttendeeRecord[] output = gson.fromJson(bufferedReader, AttendeeRecord[].class);

        return Arrays.asList(output);
    }


    public String importFromJSON(InputStreamReader reader, User user) {
        log.info("{} starting data import", user);
        BufferedReader jsonFile = new BufferedReader(reader);
        try {
            List<AttendeeRecord> attendees = loadFile(jsonFile);
            List<Attendee> attendeesToAdd = new ArrayList<>();
            List<Order> ordersToAdd = new ArrayList<>();

            Map<String, Badge> badges = getBadgeHashMap();
            Map<String, Order> orders = getOrderHashMap();
            User currentUser = userRepository.findOneByUsernameIgnoreCase(user.getUsername());

            int count = 0;
            for (AttendeeRecord record : attendees) {
                count++;
                if (count % 1000 == 0) { log.info("Loading line " + count); }

                // Auto-generate order ID if it doesn't exist
                if (record.orderId == null || record.orderId.trim().isEmpty()) {
                    record.orderId = Order.generateOrderId();
                }

                Attendee attendee = new Attendee();
                attendee.setFirstName(record.firstName);
                attendee.setLastName(record.lastName);
                attendee.setNameIsLegalName(record.nameOnIdIsPreferredName);
                attendee.setLegalFirstName(record.firstNameOnId);
                attendee.setLegalLastName(record.lastNameOnId);
                attendee.setFanName(record.fanName);
                attendee.setBadgeNumber(generateBadgeNumber(currentUser.getNextBadgeNumber()));
                attendee.setZip(record.postal);
                attendee.setCountry(record.country);
                attendee.setPhoneNumber(FieldCleaner.cleanPhoneNumber(record.phone));
                attendee.setEmail(record.email);
                attendee.setBirthDate(LocalDate.parse(record.birthdate, formatter));
                attendee.setEmergencyContactFullName(record.emergencyName);
                attendee.setEmergencyContactPhone(FieldCleaner.cleanPhoneNumber(record.emergencyPhone));
                attendee.setParentIsEmergencyContact(record.emergencyContactSameAsParent);
                attendee.setParentFullName(record.parentName);
                attendee.setParentPhone(FieldCleaner.cleanPhoneNumber(record.parentPhone));
                attendee.setPaid(true);     // All are paid, there isn't a specific flag for it
                try {
                    attendee.setPaidAmount(new BigDecimal(record.amountPaidInCents / 100));
                } catch (NumberFormatException e) {
                    attendee.setPaidAmount(BigDecimal.ZERO);
                }

                if (badges.containsKey(record.membershipType)) {
                    attendee.setBadge(badges.get(record.membershipType));
                } else {
                    log.error("Badge type " + record.membershipType + " not found on line " + count);
                    throw new RuntimeException("Badge type " + record.membershipType + " not found on line " + count);
                }

                if (orders.containsKey(record.orderId)) {
                    Order currentOrder = orders.get(record.orderId);
                    attendee.setOrder(currentOrder);
                    currentOrder.addToTotalAmount(attendee.getPaidAmount());
                } else {
                    Order o = new Order();
                    o.setOrderTakenByUser(currentUser);
                    o.setOrderId(record.orderId);
                    orders.put(o.getOrderId(), o);
                    ordersToAdd.add(o);
                    attendee.setOrder(o);
                    o.addToTotalAmount(attendee.getPaidAmount());
                }
                if (!record.notes.isEmpty() && !record.notes.trim().isEmpty()) {
//                    attendee.addHistoryEntry(currentUser, record.notes);
                }
                if (!record.vipTShirtSize.trim().isEmpty()) {
//                    attendee.addHistoryEntry(currentUser, "VIP T-Shirt size: " + record.vipTShirtSize);
                }
                attendee.setPreRegistered(true);
                attendeesToAdd.add(attendee);
            }

            log.info("Read " + count + " lines");
            log.info("Setting paid/unpaid status in {} orders", ordersToAdd.size());

            if (sessionService.userHasOpenSession(currentUser)) {
                log.info("{} closed open session {} before import",
                        currentUser, sessionService.getCurrentSessionForUser(currentUser));
            }
            TillSession session = sessionService.getNewSessionForUser(currentUser);
            for (Order o : ordersToAdd) {
                if (o.getPaid()) {
                    Payment p = new Payment();
                    p.setAmount(o.getTotalAmount());
                    p.setPaymentType(Payment.PaymentType.PREREG);
                    p.setPaymentTakenAt(Instant.now());
                    p.setPaymentLocation("kumoricon.org");
                    p.setPaymentTakenBy(currentUser);
                    p.setTillSession(session);
                    p.setOrder(o);
                }
            }


            log.info("{} saving {} orders and {} attendees to database", user, ordersToAdd.size(), attendeesToAdd.size());
            orderRepository.saveAll(ordersToAdd);

            attendeeRepository.saveAll(attendeesToAdd);
            userRepository.save(currentUser);

            log.info("{} closing session used during import");
            sessionService.closeSessionForUser(currentUser);

            jsonFile.close();

            log.info("{} done importing data", user);
            return String.format("Imported %s attendees and %s orders", attendeesToAdd.size(), ordersToAdd.size());

        } catch (Exception ex) {
            log.error("Error parsing file: ", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } finally {
            try {
                jsonFile.close();
            } catch (IOException ex) {
                log.error("Error closing file", ex);
            }
        }

    }
}
