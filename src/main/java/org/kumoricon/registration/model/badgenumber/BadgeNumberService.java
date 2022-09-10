package org.kumoricon.registration.model.badgenumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class BadgeNumberService {
    private static final Logger log = LoggerFactory.getLogger(BadgeNumberService.class);
    private static final String[] PREFIXES = {"G"};
    private final Integer startingBadgeNumber;
    private final BadgeNumberRepository badgeNumberRepository;
    private final Random random = new Random();

    public BadgeNumberService(@Value("${badge.startingBadgeNumber}") Integer startingBadgeNumber,
                              BadgeNumberRepository badgeNumberRepository) {
        this.startingBadgeNumber = startingBadgeNumber;
        this.badgeNumberRepository = badgeNumberRepository;
        initialize();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getNextBadgeNumber() {
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        return prefix + badgeNumberRepository.increment(prefix);
    }

    protected void initialize() {
        log.info("Initializing badge numbers");
        Integer created = 0;
        for (String prefix : PREFIXES) {
            if (!badgeNumberRepository.exists(prefix)) {
                badgeNumberRepository.insert(prefix, startingBadgeNumber);
                created++;
            }
        }
        log.info("Created {} badge number prefix records for {} configured prefixes", created, PREFIXES.length);
    }

}
