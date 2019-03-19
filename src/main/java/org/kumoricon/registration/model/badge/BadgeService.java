package org.kumoricon.registration.model.badge;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        ageRangeRepository.save(badge.getAgeRanges(), savedId);
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
}
