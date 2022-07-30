package org.kumoricon.registration.admin.export;

import org.kumoricon.registration.helpers.DateTimeService;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.model.tillsession.TillSessionRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
public class ExportFileController {
    private static final Logger log = LoggerFactory.getLogger(ExportFileController.class);
    private final StaffRepository staffRepository;
    private final OrderRepository orderRepository;
    private final TillSessionRepository tillSessionRepository;
    private final AttendeeRepository attendeeRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final BadgeService badgeService;
    private final DateTimeService dateTimeService;

    public ExportFileController(StaffRepository staffRepository,
                                AttendeeRepository attendeeRepository,
                                UserRepository userRepository,
                                PaymentRepository paymentRepository,
                                OrderRepository orderRepository,
                                TillSessionRepository tillSessionRepository,
                                BadgeService badgeService,
                                DateTimeService dateTimeService) {
        this.staffRepository = staffRepository;
        this.attendeeRepository = attendeeRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.tillSessionRepository = tillSessionRepository;
        this.badgeService = badgeService;
        this.dateTimeService = dateTimeService;
    }

    @RequestMapping(value = "/admin/export/attendees.csv", produces = "text/csv")
    @PreAuthorize("hasAuthority('manage_export')")
    @Transactional(readOnly = true)
    public String exportAttendees() {
        Map<Integer, Badge> badges = new HashMap<>();
        for (Badge b : badgeService.findAll()) {
            badges.put(b.getId(), b);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("First Name,Last Name,Legal First Name,Legal Last Name,Fan Name,Preferred Pronoun,Birthdate,Zip Code,");
        sb.append("Phone,Email,Emergency Contact,Parent Contact,");
        sb.append("Order ID,Badge Number,Badge Type,Paid?,Amount,Checked In,Check In Time,Membership Revoked\n");
        for (Attendee a : attendeeRepository.findAll()) {
            sb.append(a.getFirstName()).append(",")
                    .append(a.getLastName()).append(",")
                    .append(a.getLegalFirstName()).append(",")
                    .append(a.getLegalLastName()).append(",")
                    .append(blankOrString(a.getFanName())).append(",")
                    .append(blankOrString(a.getPreferredPronoun())).append(",")
                    .append(a.getBirthDate()).append(",")
                    .append(blankOrString(a.getZip())).append(",")
                    .append(blankOrString(a.getPhoneNumber())).append(",")
                    .append(blankOrString(a.getEmail())).append(",")
                    .append(blankOrString(a.getEmergencyContactFullName(), a.getEmergencyContactPhone())).append(",")
                    .append(blankOrString(a.getParentFullName(), a.getParentPhone())).append(",")
                    .append(a.getOrderId()).append(",")
                    .append(a.getBadgeNumber()).append(",")
                    .append(badges.get(a.getBadgeId()).getName()).append(",")
                    .append(a.getPaid()).append(",")
                    .append(a.getPaidAmount()).append(",")
                    .append(a.getCheckedIn()).append(",")
                    .append(dateTimeService.format(a.getCheckInTime())).append(",")
                    .append(a.isMembershipRevoked())
                    .append("\n");
        }

        return sb.toString();
    }

    @RequestMapping(value = "/admin/export/staff.csv", produces = "text/csv")
    @PreAuthorize("hasAuthority('manage_export')")
    public String exportStaff() {

        StringBuilder sb = new StringBuilder();
        sb.append("First Name,Last Name,Legal First Name,Legal Last Name,Department,Checked In,Check In Time\n");

        List<Staff> staff = staffRepository.findAll();

        for (Staff s : staff) {
            sb.append(s.getFirstName()).append(",")
                    .append(s.getLastName()).append(",")
                    .append(s.getLegalFirstName()).append(",")
                    .append(s.getLegalLastName()).append(",")
                    .append(s.getDepartment()).append(",")
                    .append(s.getCheckedIn()).append(",")
                    .append(dateTimeService.format(s.getCheckedInAt())).append("\n");
        }


        return sb.toString();
    }

    @RequestMapping(value = "/admin/export/orders.csv", produces = "text/csv")
    @PreAuthorize("hasAuthority('manage_export')")
    public String exportOrders() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID, Payments, Badges\n");
        List<Order> orders = orderRepository.findAll();

        for (Order o : orders) {
            StringBuilder badgeTypes = new StringBuilder();
            List<Attendee> attendees = attendeeRepository.findAllByOrderId(o.getId());
            for (Attendee a : attendees) {
                int badgeId = a.getBadgeId();
                String type = badgeService.findById(badgeId).getBadgeTypeText();
                badgeTypes.append(a.getFirstName() + " " + a.getLastName() + " - " + type + "  ");
            }

            StringBuilder paymentString = new StringBuilder();
            List<Payment> payments = paymentRepository.findByOrderId(o.getId());
            for (Payment p : payments) {
                User takenByUser = userRepository.findOneById(p.getPaymentTakenBy());
                String tillSession = String.valueOf(p.getTillSessionId());
                String takenBy = takenByUser.getFirstName() + ' ' + takenByUser.getLastName();
                String takenAt = p.getPaymentTakenAt().format(DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss"));
                String amount = p.getAmount().toString();
                Payment.PaymentType type = p.getPaymentType();
                String paymentType = type.name();
                String paymentSummary = paymentType + ": " + amount + " by " + takenBy + " at " + takenAt + " session " + tillSession + "  ";
                paymentString.append(paymentSummary);
                if (type == Payment.PaymentType.CREDIT)
                    paymentString.append("squareReceiptNumber " + p.getAuthNumber() + "  ");
                if (type == Payment.PaymentType.CHECK)
                    paymentString.append("checkNumber " + p.getAuthNumber() + "  ");
            }

            sb.append(o.getId().toString()).append(",")
            .append(paymentString).append(",")
            .append(badgeTypes)
            .append("\n");
        }

        return sb.toString();
    }

    static String blankOrString(String... input) {
        if (input == null) {
            return "";
        } else {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < input.length; i++) {
                if (input[i] != null && !input[i].isBlank()) {
                    if (i > 0) {
                        if (input[i - 1] != null && !input[i - 1].isBlank()) {
                            output.append(" - ");
                        }
                    }
                    output.append(input[i]);
                }
            }
            return output.toString();
        }
    }

}