package org.kumoricon.registration.model.tillsession;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TillSessionDTO (
        Integer id,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        String username,
        Integer userId,
        BigDecimal total,
        String tillName,
        boolean open){
}
