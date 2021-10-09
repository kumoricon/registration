package org.kumoricon.registration.model.badgenumber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BadgeNumberServiceTest {
    BadgeNumberService service;
    BadgeNumberRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(BadgeNumberRepository.class);
        service = new BadgeNumberService(1000, repository);
    }

    @Test
    void getNextBadgeNumber() {
        when(repository.exists(any())).thenReturn(true);
        when(repository.increment(any())).thenReturn(1001, 1002, 1003);

        assertTrue(service.getNextBadgeNumber().contains("1001"));
        assertTrue(service.getNextBadgeNumber().contains("1002"));
        assertTrue(service.getNextBadgeNumber().contains("1003"));
    }
}