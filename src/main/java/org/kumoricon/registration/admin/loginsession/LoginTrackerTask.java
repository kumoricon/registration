package org.kumoricon.registration.admin.loginsession;

import org.kumoricon.registration.model.loginsession.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Every minute, runs a SQL query that adds active login sessions to the loginsessions table
 */
@Component
public class LoginTrackerTask {

    private static final Logger log = LoggerFactory.getLogger(LoginTrackerTask.class);
    private final LoginRepository loginRepository;

    public LoginTrackerTask(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void saveCurrentLoginSessions() {
        loginRepository.updateLoginRecords();
    }
}