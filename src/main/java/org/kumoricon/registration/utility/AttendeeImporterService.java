package org.kumoricon.registration.utility;

import com.google.gson.*;

import org.kumoricon.registration.helpers.FieldCleaner;
import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.tillsession.TillSession;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public static final String IMPORT_TILL_NAME = "Attendee Import";
    private final TillSessionService sessionService;

    private final OrderRepository orderRepository;
    private final AttendeeRepository attendeeRepository;
    private final BadgeService badgeService;

    private final UserRepository userRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final PaymentRepository paymentRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(AttendeeImporterService.class);

    private HashMap<String, Badge> badgeMap;        // badge name -> badge
    private HashMap<String, Integer> orderIdMap;    // order number -> id in database
    private HashMap<String, Integer> attendeeIdMap; // firstname+lastname+orderid  -> id in database

    AttendeeImporterService(TillSessionService sessionService, OrderRepository orderRepository,
                            AttendeeRepository attendeeRepository, BadgeService badgeService,
                            UserRepository userRepository, AttendeeHistoryRepository attendeeHistoryRepository,
                            PaymentRepository paymentRepository) {
        this.sessionService = sessionService;
        this.orderRepository = orderRepository;
        this.attendeeRepository = attendeeRepository;
        this.badgeService = badgeService;
        this.userRepository = userRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.paymentRepository = paymentRepository;
    }

    private HashMap<String, Badge> getBadgeMap() {
        HashMap<String, Badge> badges = new HashMap<>();
        for (Badge b : badgeService.findAll()) {
            badges.put(b.getName(), b);
        }
        return badges;
    }

    private HashMap<String, Integer> getOrderIdMap() {
        HashMap<String, Integer> orderIds = new HashMap<>();
        for (Order o : orderRepository.findAll()) {
            orderIds.put(o.getOrderId(), o.getId());
        }
        return orderIds;
    }

    private HashMap<String, Integer> getAttendeeMap() {
        HashMap<String, Integer> attendeeIds = new HashMap<>();
        for (AttendeeOrderDTO a : attendeeRepository.findAllAttendeeOrderDTO()) {
            attendeeIds.put(a.getFirstName() + a.getLastName() + a.getOrderNumber(), a.getAttendeeId());
        }
        return attendeeIds;
    }


    private String generateBadgeNumber(Integer badgeNumber) {
        return String.format("VN%1$05d", badgeNumber);
    }

    private List<AttendeeRecord> loadFile(BufferedReader bufferedReader) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE)).create();
        AttendeeRecord[] output = gson.fromJson(bufferedReader, AttendeeRecord[].class);

        return Arrays.asList(output);
    }

    protected Integer createOrders(List<AttendeeRecord> attendeeRecords, User user) {
        long start = System.currentTimeMillis();
        log.info("Creating orders...");
        Set<String> orderNumbers = new HashSet<>();
        int count = 0;
        for (AttendeeRecord a : attendeeRecords) {
            count++;
            if (a.orderId == null || a.orderId.trim().isEmpty()) {
                log.error("During import record {} had no order id: {} {}", count, a.firstName, a.lastName);
                throw new RuntimeException("Import record missing order id");
            }
            orderNumbers.add(a.orderId);
        }

        List<Order> orders = new ArrayList<>();

        count = 0;
        for (String orderNumber : orderNumbers) {
            Order o = new Order();
            o.setOrderId(orderNumber);
            o.setOrderTakenByUser(user);
            o.setPaid(true);
            orders.add(o);
            count++;
        }
        orderRepository.saveAll(orders);
        log.info("Saved {} orders in {} ms", count, System.currentTimeMillis()-start);
        return count;
    }

    protected Integer createAttendees(List<AttendeeRecord> attendeeRecords, User user) {
        long start = System.currentTimeMillis();
        log.info("Creating attendees...");
        List<Attendee> attendeesToAdd = new ArrayList<>();
        int count = 0;
        for (AttendeeRecord record : attendeeRecords) {
            count++;
            Attendee attendee = new Attendee();
            attendee.setOrderId(orderIdMap.get(record.orderId));
            attendee.setFirstName(record.firstName);
            attendee.setLastName(record.lastName);
            attendee.setNameIsLegalName(record.nameOnIdIsPreferredName);
            attendee.setLegalFirstName(record.firstNameOnId);
            attendee.setLegalLastName(record.lastNameOnId);
            attendee.setFanName(record.fanName);
            attendee.setBadgeNumber(generateBadgeNumber(user.getNextBadgeNumber()));
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
            attendee.setPaidAmount(new BigDecimal(record.amountPaidInCents / 100));

            if (badgeMap.containsKey(record.membershipType)) {
                attendee.setBadge(badgeMap.get(record.membershipType));
            } else {
                log.error("Badge type " + record.membershipType + " not found on line " + count);
                throw new RuntimeException("Badge type " + record.membershipType + " not found on line " + count);
            }

            attendee.setPreRegistered(true);
            attendeesToAdd.add(attendee);
        }

        attendeeRepository.saveAll(attendeesToAdd);
        log.info("Saved {} attendees in {} ms", count, System.currentTimeMillis()-start);
        return count;
    }

    protected Integer createNotes(List<AttendeeRecord> attendeeRecords, User user) {
        long start = System.currentTimeMillis();
        List<AttendeeHistory> notes = new ArrayList<>();
        int count = 0;
        for (AttendeeRecord record : attendeeRecords) {
            Integer attendeeId = attendeeIdMap.get(record.firstName + record.lastName + record.orderId);

            if (!record.notes.isEmpty() && !record.notes.trim().isEmpty()) {
                count++;
                AttendeeHistory note = new AttendeeHistory(user, attendeeId, record.notes);
                notes.add(note);
            }
            if (!record.vipTShirtSize.trim().isEmpty()) {
                count++;
                AttendeeHistory note = new AttendeeHistory(user, attendeeId, "VIP T-Shirt size: " + record.vipTShirtSize);
                notes.add(note);
            }
        }
        attendeeHistoryRepository.saveAll(notes);

        log.info("Saved {} notes in {} ms", count, System.currentTimeMillis() - start);
        return count;
    }

    private Integer createPayments(List<AttendeeRecord> attendeeRecords, User user) {
        long start = System.currentTimeMillis();
        List<Payment> payments = new ArrayList<>();
        if (sessionService.userHasOpenSession(user)) {
            log.info("{} closed open session {} before import",
                    user, sessionService.getCurrentOrNewTillSession(user, IMPORT_TILL_NAME));
            sessionService.closeSessionForUser(user);
        }
        TillSession session = sessionService.getNewSessionForUser(user, IMPORT_TILL_NAME);

        int count = 0;
        for (String orderNumber : orderIdMap.keySet()) {
            count++;
            BigDecimal total = orderRepository.getTotalByOrderNumber(orderNumber);

            Payment p = new Payment();
            p.setAmount(total);
            p.setPaymentType(Payment.PaymentType.PREREG);
            p.setPaymentTakenAt(Instant.now());
            p.setPaymentLocation("kumoricon.org");
            p.setPaymentTakenBy(user);
            p.setTillSession(session);
            p.setOrderId(orderIdMap.get(orderNumber));
            payments.add(p);
        }

        userRepository.save(user);
        paymentRepository.saveAll(payments);

        log.info("Closing till session used during import");
        sessionService.closeSessionForUser(user);
        log.info("Saved {} payments in {} ms", count, System.currentTimeMillis()-start);
        return count;
    }

    @Transactional
    public String importFromJSON(InputStreamReader reader, User user) {
        long start = System.currentTimeMillis();
        log.info("{} starting data import", user);
        BufferedReader jsonFile = new BufferedReader(reader);
        Integer orderCount, attendeeCount, noteCount,paymentCount;
        try {
            List<AttendeeRecord> attendees = loadFile(jsonFile);

            badgeMap = getBadgeMap();
            User currentUser = userRepository.findOneByUsernameIgnoreCase(user.getUsername());

            orderCount = createOrders(attendees, user);
            orderIdMap = getOrderIdMap();
            attendeeCount = createAttendees(attendees, user);

            attendeeIdMap = getAttendeeMap();
            noteCount = createNotes(attendees, user);

            paymentCount = createPayments(attendees, user);

            jsonFile.close();

            log.info("{} done importing data", user);

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
        return String.format("Saved %s orders, %s attendees, %s notes, %s payments in %s ms",
                orderCount, attendeeCount, noteCount, paymentCount, System.currentTimeMillis() - start);
    }
}
