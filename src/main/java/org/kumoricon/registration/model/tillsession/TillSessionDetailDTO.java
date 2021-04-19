package org.kumoricon.registration.model.tillsession;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record TillSessionDetailDTO(
        Integer id,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        String username,
        Integer userId,
        BigDecimal total,
        String tillName,
        boolean open,
        List<TillSessionOrderDTO> orderDTOs,
        List<TillSessionPaymentTotalDTO> paymentTotals,
        List<TillSessionBadgeCountDTO> badgeCounts) {

    public record TillSessionPaymentTotalDTO(String type, BigDecimal total) {}

    public record TillSessionBadgeCountDTO(String badgeName, Integer count) {}

    public record TillSessionOrderDTO(Integer orderId, String badges, String payments) {}

    public static TillSessionDetailDTO fromTillSessionDTO(TillSessionDTO tillSessionDTO,
                                                          List<TillSessionOrderDTO> orderDTOs,
                                                          List<TillSessionPaymentTotalDTO> paymentTotals,
                                                          List<TillSessionBadgeCountDTO> badgeCounts) {
        return new TillSessionDetailDTO(
                tillSessionDTO.id(),
                tillSessionDTO.startTime(),
                tillSessionDTO.endTime(),
                tillSessionDTO.username(),
                tillSessionDTO.userId(),
                tillSessionDTO.total(),
                tillSessionDTO.tillName(),
                tillSessionDTO.open(),
                orderDTOs,
                paymentTotals,
                badgeCounts);
    }

}
