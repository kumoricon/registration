package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class TillSessionService {
    private TillSessionRepository repository;
    private PaymentRepository paymentRepository;
    private static final ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a").withZone(zoneId);

    @Autowired
    public TillSessionService(TillSessionRepository repository, PaymentRepository paymentRepository) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
    }

    public TillSession getCurrentSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("getCurrentSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        if (session == null) {
            session = repository.save(new TillSession(user));
        }
        return session;
    }

    public TillSession getNewSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("getNewSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        if (session != null) {
            closeSession(session);
        }
        session = repository.save(new TillSession(user));
        return session;
    }

    public boolean userHasOpenSession(User user) {
        if (user == null) { throw new RuntimeException("userHasOpenSession called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        return session != null;
    }

    public TillSession closeSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("closeSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        return closeSession(session.getId());
    }

    public TillSession closeSession(TillSession session) {
        if (session != null) {
            if (session.isOpen()) {
                session.setEndTime(Instant.now());
                session.setOpen(false);
                repository.save(session);
            } else {
                throw new RuntimeException(String.format("Session %s is already closed", session));
            }
        }
        return session;
    }

    public TillSession closeSession(Integer id) {
        TillSession session = repository.findOneById(id);
        return closeSession(session);
    }

    public List<TillSession> getAllOpenSessions() {
        return repository.findAllOpenSessions();
    }
    public List<TillSession> getAllSessions() { return repository.findAllOrderByEnd(); }
    public List<TillSessionDTO> getAllTillSessionDTOs() { return repository.findAllTillSessionDTO(); }

    public BigDecimal getTotalForSession(TillSession s) {
        return paymentRepository.getTotalForSessionId(s.getId());
    }

    public String generateTextReportForSession(TillSession session) {
        if (session == null) { return ""; }
        final User user = session.getUser();
        if (user == null) { return "User not found for session " + session; }

        return "Till Report" + "\n" +
                buildTextReportUserLine(user) + "\n" +
                buildTextReportSessionLine(session) + "\n" +
                "\n\n" +
                buildTextTotalsForSession(session) + "\n" +
                "Credit Card and Check Transactions\n" +
                buildTextDetailsForSession(session, Payment.PaymentType.CREDIT) + "\n" +
                buildTextDetailsForSession(session, Payment.PaymentType.CHECK) + "\n" +
                buildTextReportFooter();
    }


    public String generateHTMLReportForSession(TillSession session) {
        if (session == null) { return ""; }
        final User user = session.getUser();
        if (user == null) { return "User not found for session " + session; }

        return "<h1>Till Report</h1>" +
                buildHTMLReportUserLine(user) +
                buildHTMLReportSessionLine(session) +
                "<hr>" +
                buildHTMLTotalsForSession(session) +
                "<h2>Credit Card and Check Transactions</h2>" +
                buildHTMLDetailsForSession(session, Payment.PaymentType.CREDIT) +
                buildHTMLDetailsForSession(session, Payment.PaymentType.CHECK) +
                buildHTMLReportFooter();
    }

    private String buildHTMLTotalsForSession(TillSession session) {
        StringBuilder output = new StringBuilder();
        output.append("<h2>Totals</h2>");
        output.append("<table border=\"1\" cellpadding=\"5\" style=\"border-collapse: collapse; border-spacing: 5px; border-style: 1px solid black;\">")
                .append("<tr><td>Type</td><td>Amount</td></tr>");
        // Total per payment type
        for (Payment.PaymentType pt : Payment.PaymentType.values()) {
            BigDecimal amount = paymentRepository.getTotalByPaymentTypeForSessionId(session.getId(), pt.getValue());
            if (amount != null) {
                output.append(String.format("<tr><td>%s</td><td align=\"right\">$%s</td></tr>",
                        pt.toString(), amount));
            }
        }
        output.append("</table>");
        return output.toString();
    }

    public String buildTextTotalsForSession(TillSession session) {
        StringBuilder output = new StringBuilder();
        // Total per payment type
        for (Payment.PaymentType pt : Payment.PaymentType.values()) {
            BigDecimal amount = paymentRepository.getTotalByPaymentTypeForSessionId(session.getId(), pt.getValue());
            if (amount != null) {
                output.append(String.format("%1$-10s    $%2$10s\n", pt.toString(), amount));
            }
        }
        return output.toString();
    }

    private String buildHTMLDetailsForSession(TillSession session, Payment.PaymentType paymentType) {
        StringBuilder output = new StringBuilder();

        List<Payment> payments = paymentRepository.findByTillSessionIdAndPaymentType(session.getId(), paymentType);

        if (payments.size() > 0) {
            output.append(String.format("<b>%s Transactions:</b><br>\n", paymentType));
            output.append("<table width=\"100%\" border=\"1\" cellpadding=\"5\" style=\"border-collapse: collapse; border-spacing: 5px; border-style: 1px solid black;\">")
                    .append("<tr>")
                    .append("<td>Payment Taken At</td>")
                    .append("<td>From Computer</td>")
                    .append("<td>Order</td>")
                    .append("<td>Notes/Auth Number</td>")
                    .append("<td>Amount</td>")
                    .append("</tr>");
            for (Payment payment : payments) {
                output.append("<tr><td>")
                        .append(payment.getPaymentTakenAt().atZone(zoneId).format(DATE_TIME_FORMATTER))
                        .append("</td>");
                output.append(String.format("<td>%s</td><td>%s</td><td>%s</td><td align=\"right\">$%s</td></tr>",
                        payment.getPaymentLocation(),
                        payment.getOrderId(),
                        payment.getAuthNumber(),
                        payment.getAmount()));
            }
            output.append("</table><br>");
        }
        return output.toString();
    }

    private String buildTextDetailsForSession(TillSession session, Payment.PaymentType paymentType) {
        StringBuilder output = new StringBuilder();

        List<Payment> payments = paymentRepository.findByTillSessionIdAndPaymentType(session.getId(), paymentType);

        if (payments.size() > 0) {
            output.append(String.format("%s Transactions:\n", paymentType));
            output.append("Payment Taken At\t")
                    .append("From Computer\t")
                    .append("Order\t")
                    .append("Notes/Auth Number\t")
                    .append("Amount")
                    .append("\n");
            for (Payment payment : payments) {
                output.append(payment.getPaymentTakenAt().atZone(zoneId).format(DATE_TIME_FORMATTER))
                        .append("\t");
                output.append(String.format("%s\t%s\t%s\t$%s\n",
                        payment.getPaymentLocation(),
                        payment.getOrderId(),
                        payment.getAuthNumber(),
                        payment.getAmount()));
            }
        }
        return output.toString();
    }

    private static String buildHTMLReportUserLine(User user) {
        return "User: <b>" +
                user.getFirstName() +
                " " +
                user.getLastName() +
                "</b> (id: " +
                user.getId() +
                ")<br>";
    }

    private static String buildTextReportUserLine(User user) {
        return "User: " +
                user.getFirstName() +
                " " +
                user.getLastName() +
                "(id: " +
                user.getId() +
                ")\n";
    }

    private static String buildTextReportSessionLine(TillSession session) {
        StringBuilder output = new StringBuilder();
        output.append("Session id ")
                .append(session.getId())
                .append(": ");
        // Date range
        output.append(session.getStartTime().atZone(zoneId).format(DATE_TIME_FORMATTER));
        output.append(" -\n");
        if (session.getEndTime() != null) {
            output.append(session.getEndTime().atZone(zoneId).format(DATE_TIME_FORMATTER));
        } else {
            output.append("now");
        }
        output.append("\n");
        return output.toString();
    }


    private static String buildHTMLReportSessionLine(TillSession session) {
        return buildTextReportSessionLine(session) + "<br>";
    }

    private String buildHTMLReportFooter() {
        return "<br><p>Report generated at " + Instant.now().atZone(zoneId).format(DATE_TIME_FORMATTER) + "</p>";
    }

    private String buildTextReportFooter() {
        return "Report generated at " + Instant.now().atZone(zoneId).format(DATE_TIME_FORMATTER) + "\n";
    }

}
