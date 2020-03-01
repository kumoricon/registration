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
    private static final String[] PERIOD_NAMES = new String[] {"12:00 AM", "12:15 AM", "12:30 AM", "12:45 AM", "01:00 AM", "01:15 AM", "01:30 AM", "01:45 AM", "02:00 AM", "02:15 AM", "02:30 AM", "02:45 AM", "03:00 AM", "03:15 AM", "03:30 AM", "03:45 AM", "04:00 AM", "04:15 AM", "04:30 AM", "04:45 AM", "05:00 AM", "05:15 AM", "05:30 AM", "05:45 AM", "06:00 AM", "06:15 AM", "06:30 AM", "06:45 AM", "07:00 AM", "07:15 AM", "07:30 AM", "07:45 AM", "08:00 AM", "08:15 AM", "08:30 AM", "08:45 AM", "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM", "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM", "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM", "12:00 PM", "12:15 PM", "12:30 PM", "12:45 PM", "01:00 PM", "01:15 PM", "01:30 PM", "01:45 PM", "02:00 PM", "02:15 PM", "02:30 PM", "02:45 PM", "03:00 PM", "03:15 PM", "03:30 PM", "03:45 PM", "04:00 PM", "04:15 PM", "04:30 PM", "04:45 PM", "05:00 PM", "05:15 PM", "05:30 PM", "05:45 PM", "06:00 PM", "06:15 PM", "06:30 PM", "06:45 PM", "07:00 PM", "07:15 PM", "07:30 PM", "07:45 PM", "08:00 PM", "08:15 PM", "08:30 PM", "08:45 PM", "09:00 PM", "09:15 PM", "09:30 PM", "09:45 PM", "10:00 PM", "10:15 PM", "10:30 PM", "10:45 PM", "11:00 PM", "11:15 PM", "11:30 PM", "11:45 PM"};

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

        List<ZonedDateTime> periods = new ArrayList<>();
        List<List<String>> history = new ArrayList<>();

        if (reportDate != null) {
            OffsetDateTime start = reportDate.atStartOfDay(TIMEZONE).toOffsetDateTime();
            OffsetDateTime end = reportDate.atStartOfDay(TIMEZONE).plus(24, ChronoUnit.HOURS).toOffsetDateTime();

            for (int i = 0; i < 96; i++) {
                periods.add(start.plus(i*15, ChronoUnit.MINUTES).atZoneSameInstant(TIMEZONE));
            }

            List<LoginTimePeriod> loginTimePeriods = loginRepository.getLoginTimePeriods(start, end);

            Map<String, Set<ZonedDateTime>> userIdToPeriodMap = new LinkedHashMap<>();

            for (LoginTimePeriod p : loginTimePeriods) {
                if (!userIdToPeriodMap.containsKey(p.getUser())) {
                    userIdToPeriodMap.put(p.getUser(), new HashSet<>());
                }
                userIdToPeriodMap.get(p.getUser()).add(p.getStartTime().atZoneSameInstant(TIMEZONE));
            }

            for (String userFullName : userIdToPeriodMap.keySet()) {
                List<String> row = new ArrayList<>();
                row.add(userFullName);
                int minutesPresent = 0;
                for (ZonedDateTime p : periods) {
                    if (userIdToPeriodMap.get(userFullName).contains(p)) {
                        row.add("background-color: green;");
                        minutesPresent += 15; // Each period is 15 minutes
                    } else {
                        row.add("");
                    }
                }
                row.add(minToHHMM(minutesPresent));
                history.add(row);
            }
        }

        model.addAttribute("availableDays", availableDays);
        model.addAttribute("selectedDate", reportDate);
        model.addAttribute("periods", periods);
        model.addAttribute("periodNames", PERIOD_NAMES);
        model.addAttribute("timezone", TIMEZONE);
        model.addAttribute("history", history);

        return "reports/loginhistory";
    }


    private String minToHHMM(int minutes) {
        if (minutes <= 0) { return "0:00"; }
        int hours = minutes/60;
        int remainder = minutes % 60;

        return String.format("%d:%02d", hours, remainder);
    }

}