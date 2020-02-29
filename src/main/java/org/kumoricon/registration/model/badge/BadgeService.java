package org.kumoricon.registration.model.badge;

import org.kumoricon.registration.model.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final AgeRangeRepository ageRangeRepository;

    public BadgeService(BadgeRepository badgeRepository, AgeRangeRepository ageRangeRepository) {
        this.badgeRepository = badgeRepository;
        this.ageRangeRepository = ageRangeRepository;
    }

    @Transactional(readOnly = true)
    public List<Badge> findByVisibleTrue() {
        List<Badge> badges = badgeRepository.findByVisibleTrue();
        for (Badge badge : badges) {
            badge.setAgeRanges(ageRangeRepository.findAgeRangesForBadgeId(badge.getId()));
        }
        return badges;
    }

    @Transactional(readOnly = true)
    public List<Badge> findByVisibleAndUserRight(User user) {
        List<Badge> badges = badgeRepository.findByVisibleTrue();

        List<Badge> userCanSee = new ArrayList<>();
        for (Badge badge : badges) {
            if (badge.getRequiredRight() == null || user.hasRight(badge.getRequiredRight())) {
                userCanSee.add(badge);
            }
        }

        for (Badge badge : userCanSee) {
            badge.setAgeRanges(ageRangeRepository.findAgeRangesForBadgeId(badge.getId()));
        }
        return userCanSee;
    }

    @Transactional(readOnly = true)
    public List<Badge> findAll() {
        List<Badge> badges = badgeRepository.findAll();
        for (Badge badge : badges) {
            badge.setAgeRanges(ageRangeRepository.findAgeRangesForBadgeId(badge.getId()));
        }
        return badges;
    }


    @Transactional
    public void save(Badge badge) {
        Integer savedId = badgeRepository.save(badge);
        if (badge.getId() == null) {                    // Badge hasn't been saved in the database, so set its new ID
            for (AgeRange r : badge.getAgeRanges()) {   // on the AgeRange objects before saving them
                r.setBadgeId(savedId);
            }
        }
        ageRangeRepository.save(badge.getAgeRanges());
    }

    @Transactional(readOnly = true)
    public int count() {
        return badgeRepository.count();
    }

    @Transactional(readOnly = true)
    public BigDecimal getCostForBadgeType(Integer badgeId, Long age) {
        AgeRange ageRange = ageRangeRepository.findAgeRangeForBadgeIdAndAge(badgeId, age);
        return ageRange.getCost();
    }

    @Transactional(readOnly = true)
    public Badge findById(Integer badgeId) {
        Badge badge = badgeRepository.findById(badgeId);
        badge.setAgeRanges(ageRangeRepository.findAgeRangesForBadgeId(badgeId));
        return badge;
    }

    @Transactional
    public void setBadgeVisibility(Integer badgeId, Boolean visible) {
        /* TODO: a round trip to the database could be saved by adding a function in BadgeRepository to set
           the badges visibility by ID */
        Badge b = badgeRepository.findById(badgeId);
        b.setVisible(visible);
        badgeRepository.save(b);
    }
}
