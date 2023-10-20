package org.kumoricon.registration.guest;

import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.badgenumber.BadgeNumberService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendeeImporterService {
    public static final String IMPORT_TILL_NAME = "Attendee Import";
    private static final String REPLACE_REGEX = "[^A-Za-z0-9]";
    private final TillSessionService sessionService;

    private final OrderRepository orderRepository;
    private final AttendeeRepository attendeeRepository;
    private final BadgeService badgeService;

    private final UserRepository userRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final BadgeNumberService badgeNumberService;

    private static final Logger log = LoggerFactory.getLogger(AttendeeImporterService.class);

    private HashMap<String, Badge> badgeMap;        // badge name -> badge
    private HashMap<String, Integer> orderIdMap;    // order number -> id in database
    private HashMap<String, Integer> attendeeIdMap; // firstname+lastname+orderid  -> id in database

    public AttendeeImporterService(TillSessionService sessionService, OrderRepository orderRepository,
                            AttendeeRepository attendeeRepository, BadgeService badgeService,
                            UserRepository userRepository, AttendeeHistoryRepository attendeeHistoryRepository,
                            PaymentRepository paymentRepository,
                            BadgeNumberService badgeNumberService) {
        this.sessionService = sessionService;
        this.orderRepository = orderRepository;
        this.attendeeRepository = attendeeRepository;
        this.badgeService = badgeService;
        this.userRepository = userRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.paymentRepository = paymentRepository;
        this.badgeNumberService = badgeNumberService;
    }

    /**
     * Returns a map of lowercase badge names with non-alphanumeric characters removed. This is to
     * make it easier to match badge names from the website, we have cases like  "smallPress" -> "small press",
     * "standardPress" -> "standard press".
     * @return Badge sanitized name to badge map
     */
    private HashMap<String, Badge> getBadgeMap() {
        HashMap<String, Badge> badges = new HashMap<>();
        for (Badge b : badgeService.findAll()) {
            badges.put(b.getName().replaceAll(REPLACE_REGEX, "").toLowerCase(), b);
        }
        // Add special cases - you can't buy day badges online, but they can be created
        badges.put("regularday1", badges.get("friday"));
        badges.put("regularday2", badges.get("saturday"));
        badges.put("regularday3", badges.get("sunday"));
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

    protected List<String> createOrders(List<AttendeeImportFile.Person> attendeeRecords, User user) {
        long start = System.currentTimeMillis();
        log.info("Creating orders...");
        Set<String> orderNumbers = new HashSet<>();
        int count = 0;
        for (AttendeeImportFile.Person a : attendeeRecords) {
            count++;
            if (a.getOrderId() == null || a.getOrderId().trim().isEmpty()) {
                log.error("During import record {} had no order id: {} {}", count, a.namePreferredFirst(), a.namePreferredLast());
                throw new RuntimeException("Import record missing order id");
            }
            orderNumbers.add(a.getOrderId());
        }

        List<Order> orders = new ArrayList<>();

        count = 0;
        int existingOrderCount = 0;
        Set<String> existingOrderIds = orderRepository.findAll().stream()
                .map(Order::getOrderId)
                .collect(Collectors.toSet());
        for (String orderNumber : orderNumbers) {
            if (!existingOrderIds.contains(orderNumber)) {
                Order o = new Order();
                o.setOrderId(orderNumber);
                o.setOrderTakenByUser(user);
                o.setPaid(true);
                orders.add(o);
                count++;
            } else {
                existingOrderCount += 1;
            }
        }
        orderRepository.saveAll(orders);
        log.info("Saved {} orders in {} ms. {} already exist", count, System.currentTimeMillis()-start, existingOrderCount);
        return orders.stream().map(Order::getOrderId).collect(Collectors.toList());
    }

    protected static String normalizePronoun(String pronoun) {
        if (pronoun == null || pronoun.isBlank()) { return null; }
        String lowerStr = pronoun.toLowerCase().trim();
        for (String p : Attendee.PRONOUNS) {
            if (lowerStr.equalsIgnoreCase(p)) {
                return p;
            }
        }
        log.warn("Couldn't find match for pronoun {} in {}, setting to null", pronoun, Attendee.PRONOUNS);
        return null;
    }


    protected void createNotes(List<AttendeeImportFile.Person> attendeeRecords, User user) {
        long start = System.currentTimeMillis();
        int noteCount = 0, foundExistingNote = 0;
        List<AttendeeHistory> notes = new ArrayList<>();
        for (AttendeeImportFile.Person record : attendeeRecords) {
            Integer attendeeId = attendeeIdMap.get(record.namePreferredFirst() + record.namePreferredLast() + record.getOrderId());

            List<AttendeeHistoryDTO> existing = attendeeHistoryRepository.findAllDTObyAttendeeId(attendeeId);

            if (record.notes() != null && !record.notes().trim().isEmpty()) {
                noteCount += 1;
                AttendeeHistory note = new AttendeeHistory(user, attendeeId, record.notes());
                if (noteDoesNotExist(existing, note)) {
                    notes.add(note);
                } else {
                    foundExistingNote += 1;
                }
            }
            if (record.tShirtSize() != null && !record.tShirtSize().trim().isEmpty()) {
                noteCount += 1;
                AttendeeHistory note = new AttendeeHistory(user, attendeeId, "VIP T-Shirt size: " + record.tShirtSize());
                if (noteDoesNotExist(existing, note)) {
                    notes.add(note);
                } else {
                    foundExistingNote += 1;
                }
            }
            if (record.membershipType() != null && record.membershipType().equalsIgnoreCase("guest")) {
                noteCount += 1;
                AttendeeHistory note = new AttendeeHistory(user, attendeeId, "Guest marked checked in during import");
                if (noteDoesNotExist(existing, note)) {
                    notes.add(note);
                } else {
                    foundExistingNote += 1;
                }
            }
        }
        attendeeHistoryRepository.saveAll(notes);

        log.info("Importing {} notes, skipping {} existing, created {} notes in {} ms",
                noteCount, foundExistingNote, notes.size(), System.currentTimeMillis() - start);
    }

    private static boolean noteDoesNotExist(List<AttendeeHistoryDTO> existing, AttendeeHistory newNote) {
        for (AttendeeHistoryDTO old : existing) {
            if (old.getMessage().equalsIgnoreCase(newNote.getMessage())) {
                return false;
            }
        }
        return true;
    }

    private void createPayments(List<String> createdOrderIds, User user) {
        if (createdOrderIds.size() == 0) {      // Don't bother messing with the session if there are no new orders
            return;
        }
        long start = System.currentTimeMillis();
        List<Payment> payments = new ArrayList<>();
        if (sessionService.userHasOpenSession(user)) {
            log.info("{} closed open session {} before import",
                    user, sessionService.getCurrentOrNewTillSession(user, IMPORT_TILL_NAME));
            sessionService.closeSessionForUser(user);
        }
        TillSession session = sessionService.getNewSessionForUser(user, IMPORT_TILL_NAME);

        int count = 0;
        for (String orderNumber : createdOrderIds) {
            count++;
            BigDecimal total = orderRepository.getTotalByOrderNumber(orderNumber);

            Payment p = new Payment();
            p.setAmount(total);
            p.setPaymentType(Payment.PaymentType.PREREG);
            p.setPaymentTakenAt(OffsetDateTime.now());
            p.setPaymentLocation("kumoricon.org");
            p.setPaymentTakenBy(user);
            p.setTillSession(session);
            p.setOrderId(orderIdMap.get(orderNumber));
            payments.add(p);
        }

        paymentRepository.saveAll(payments);

        log.info("Closing till session used during import");
        sessionService.closeSessionForUser(user);
        log.info("Saved {} payments in {} ms", count, System.currentTimeMillis()-start);
    }

    @Transactional
    public void importFromObjects(List<AttendeeImportFile.Person> persons, Map<String, String> guestBadgeNumbers) {
        long start = System.currentTimeMillis();

        badgeMap = getBadgeMap();

        User user = userRepository.findOneByUsernameIgnoreCase("admin");

        Integer attendeeCount = 0, newAttendee = 0, updatedAttendee = 0;
        List<String> createdOrderIds = createOrders(persons, user);
        orderIdMap = getOrderIdMap();

        for (AttendeeImportFile.Person person : persons) {
            attendeeCount++;
            Attendee attendee;
            boolean isNew = false;
            if (attendeeRepository.countByWebsiteId(person.id()) > 0) {
                attendee = attendeeRepository.findByWebsiteId(person.id());
            } else {
                isNew = true;
                newAttendee++;
                attendee = new Attendee();
                attendee.setWebsiteId(person.id());
                attendee.setBadgeNumber(guestBadgeNumbers.getOrDefault(person.id(), null));
            }
            boolean updated = updateAttendeeFromPerson(person, attendee);

            if (isNew || updated) {
                if (!isNew) {updatedAttendee++;}
                attendeeRepository.save(attendee);
            }
        }


        attendeeIdMap = getAttendeeMap();
        createPayments(createdOrderIds, user);      // We're assuming that orders are not changed once they're
                                                    // created, AND orders are complete by the time they get to us.
                                                    // That _should_ be as safe assumption, but it's a corner case

        createNotes(persons, user);

        log.info("Found {} attendees, {} new {} updated in {} ms",
                attendeeCount, newAttendee, updatedAttendee, System.currentTimeMillis()-start);
    }

    private boolean updateAttendeeFromPerson(AttendeeImportFile.Person person, Attendee attendee) {
        boolean dirty = false;

        // Guests are always counted as checked in because they don't pick up their badges at Registration
        if ("guest".equalsIgnoreCase(person.membershipType())) {
            attendee.setCheckedIn(true);
        }

        if (!Objects.equals(attendee.getFirstName(), person.namePreferredFirst())) {
            dirty = true;
            attendee.setFirstName(person.namePreferredFirst());
        }
        if (!Objects.equals(attendee.getLastName(), person.namePreferredLast())) {
            dirty = true;
            attendee.setLastName(person.namePreferredLast());
        }
        if (!Objects.equals(attendee.getLegalFirstName(), person.nameOnIdFirst())) {
            dirty = true;
            attendee.setLegalFirstName(person.nameOnIdFirst());
        }
        if (!Objects.equals(attendee.getLegalLastName(), person.nameOnIdLast())) {
            dirty = true;
            attendee.setLegalLastName(person.nameOnIdLast());
        }
        if (!Objects.equals(attendee.getNameIsLegalName(), person.nameOnIdSame())) {
            dirty = true;
            attendee.setNameIsLegalName(person.nameOnIdSame());
        }

        if (!Objects.equals(attendee.getPreferredPronoun(), person.preferredPronoun())) {
            dirty = true;
            attendee.setPreferredPronoun(person.preferredPronoun());
        }
        if (!Objects.equals(attendee.getFanName(), person.fanName())) {
            dirty = true;
            attendee.setFanName(person.fanName());
        }

        if (!Objects.equals(attendee.getZip(), person.postal())) {
            dirty = true;
            attendee.setZip(person.postal());
        }
        if (!Objects.equals(attendee.getCountry(), person.country())) {
            dirty = true;
            attendee.setCountry(person.country());
        }

        if (!Objects.equals(attendee.getPhoneNumber(), person.phone())) {
            dirty = true;
            attendee.setPhoneNumber(person.phone());
        }
        if (!Objects.equals(attendee.getEmail(), person.email())) {
            dirty = true;
            attendee.setEmail(person.email());
        }
        LocalDate birthDate = LocalDate.parse(person.birthdate());  // Should be in YYYY-MM-DD format
        if (!Objects.equals(attendee.getBirthDate(), birthDate)) {
            dirty = true;
            attendee.setBirthDate(birthDate);
        }

        if (!Objects.equals(attendee.getEmergencyContactFullName(), person.emergencyName())) {
            dirty = true;
            attendee.setEmergencyContactFullName(person.emergencyName());
        }
        if (!Objects.equals(attendee.getEmergencyContactPhone(), person.emergencyPhone())) {
            dirty = true;
            attendee.setEmergencyContactPhone(person.emergencyPhone());
        }

        if (!Objects.equals(attendee.getParentIsEmergencyContact(), !person.parentContactSeparate())) {
            dirty = true;
            attendee.setParentIsEmergencyContact(!person.parentContactSeparate());
        }
        if (!Objects.equals(attendee.getParentFullName(), person.parentName())) {
            dirty = true;
            attendee.setParentFullName(person.parentName());
        }
        if (!Objects.equals(attendee.getParentPhone(), person.parentPhone())) {
            dirty = true;
            attendee.setParentPhone(person.parentPhone());
        }


        if (attendee.getPaidAmount() == null) {
            dirty = true;
            if (person.amountPaidCents() != null) {
                attendee.setPaidAmount(new BigDecimal(person.amountPaidCents()/100));
            } else {
                attendee.setPaidAmount(BigDecimal.ZERO);
            }
            attendee.setPaid(true);

        } else if (attendee.getPaidAmount().compareTo(new BigDecimal(person.amountPaidCents()/100)) != 0) {
            dirty = true;
            attendee.setPaidAmount(new BigDecimal(person.amountPaidCents()/100));
            attendee.setPaid(true);
        }

        // The website can cancel memberships, but we're not going to overwrite that if reg canceled it.
        if (person.isCanceled() && !Objects.equals(attendee.isMembershipRevoked(), person.isCanceled())) {
            dirty = true;
            attendee.setMembershipRevoked(person.isCanceled());
        }

        attendee.setPreRegistered(true);        // If you're imported from the website, you're prereg

        if (attendee.getBadgeNumber() == null) {
            dirty = true;
            attendee.setBadgeNumber(badgeNumberService.getNextBadgeNumber());
        }

        String lowerCaseMembershipType;
        if (person.vipLevel().isEmpty()) {
            lowerCaseMembershipType = person.membershipType().replaceAll(REPLACE_REGEX, "").toLowerCase();
        } else {
            lowerCaseMembershipType = "vip" + person.vipLevel().replaceAll(REPLACE_REGEX, "").toLowerCase();
        }
        if (badgeMap.containsKey(lowerCaseMembershipType)) {
            attendee.setBadgeId(badgeMap.get(lowerCaseMembershipType).getId());
        } else {
            throw new RuntimeException("Unknown badge type %s".formatted(person.membershipType()));
        }

        if (orderIdMap.containsKey(person.getOrderId())) {
            attendee.setOrderId(orderIdMap.get(person.getOrderId()));
        } else {
            throw new RuntimeException("Unknown order ID %s".formatted(person.getOrderId()));
        }

        return dirty;
    }
}
