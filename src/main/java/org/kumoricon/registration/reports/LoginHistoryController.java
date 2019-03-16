package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.loginsession.LoginRepository;
import org.kumoricon.registration.model.loginsession.LoginTimePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class LoginHistoryController {
    private final LoginRepository loginRepository;

    private final ZoneId TIMEZONE = ZoneId.of("America/Los_Angeles");

    @Autowired
    public LoginHistoryController(LoginRepository  loginRepository) {
        this.loginRepository = loginRepository;
    }

    @RequestMapping(value = "/reports/loginhistory")
    @PreAuthorize("hasAuthority('view_login_history_report')")
    public String loginHistoryReport(Model model,
                                     @RequestParam(value = "date", required = false) String date) {
        List<LocalDate> availableDays = loginRepository.getAvailableDays();
        LocalDate reportDate = null;

        if (date != null) {
            reportDate = LocalDate.parse(date);
        } else if (availableDays.size() > 0) {
            reportDate = availableDays.get(0);
        }

        List<Instant> periods = new ArrayList<>();
        List<List<String>> history = new ArrayList<>();

        if (reportDate != null) {
            OffsetDateTime start = reportDate.atStartOfDay(TIMEZONE).toOffsetDateTime();
            OffsetDateTime end = reportDate.atStartOfDay(TIMEZONE).plus(24, ChronoUnit.HOURS).toOffsetDateTime();

            for (int i = 0; i < 96; i++) {
                periods.add(start.plusMinutes(i*15).toInstant());
            }

            List<LoginTimePeriod> loginTimePeriods = loginRepository.getLoginTimePeriods(start, end);

            Map<String, Set<Instant>> userIdToPeriodMap = new LinkedHashMap<>();

            for (LoginTimePeriod p : loginTimePeriods) {
                if (!userIdToPeriodMap.containsKey(p.getUser())) {
                    userIdToPeriodMap.put(p.getUser(), new HashSet<>());
                }
                userIdToPeriodMap.get(p.getUser()).add(p.getStartTime());
            }

            for (String userFullName : userIdToPeriodMap.keySet()) {
                List<String> row = new ArrayList<>();
                row.add(userFullName);
                for (Instant p : periods) {
                    if (userIdToPeriodMap.get(userFullName).contains(p)) {
                        row.add("background-color: green;");
                    } else {
                        row.add("");
                    }
                }
                history.add(row);
            }
        }

        model.addAttribute("availableDays", availableDays);
        model.addAttribute("selectedDate", reportDate);
        model.addAttribute("periods", periods);
        model.addAttribute("timezone", TIMEZONE);
        model.addAttribute("history", history);

        return "reports/loginhistory";
    }
}