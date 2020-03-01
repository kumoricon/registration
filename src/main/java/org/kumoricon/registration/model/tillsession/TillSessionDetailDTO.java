package org.kumoricon.registration.model.tillsession;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class TillSessionDetailDTO {
    private Integer id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String username;
    private Integer userId;
    private BigDecimal total;
    private String tillName;
    private boolean open;
    private List<TillSessionOrderDTO> orderDTOs;
    private List<TillSessionPaymentTotalDTO> paymentTotals;
    private List<TillSessionBadgeCountDTO> badgeCounts;

    public TillSessionDetailDTO() {
        this.paymentTotals = new ArrayList<>();
        this.badgeCounts = new ArrayList<>();
        this.orderDTOs = new ArrayList<>();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }

    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getTillName() { return tillName; }
    public void setTillName(String tillName) { this.tillName = tillName; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    public List<TillSessionPaymentTotalDTO> getPaymentTotals() {
        return paymentTotals;
    }

    public void setPaymentTotals(List<TillSessionPaymentTotalDTO> paymentTotals) {
        this.paymentTotals = paymentTotals;
    }

    public List<TillSessionOrderDTO> getOrderDTOs() {
        return orderDTOs;
    }

    public void setOrderDTOs(List<TillSessionOrderDTO> orderDTOs) {
        this.orderDTOs = orderDTOs;
    }

    public List<TillSessionBadgeCountDTO> getBadgeCounts() {
        return badgeCounts;
    }

    public void setBadgeCounts(List<TillSessionBadgeCountDTO> badgeCounts) {
        this.badgeCounts = badgeCounts;
    }

    public static TillSessionDetailDTO fromTillSessionDTO(TillSessionDTO tillSessionDTO) {
        TillSessionDetailDTO t = new TillSessionDetailDTO();
        t.setId(tillSessionDTO.getId());
        t.setOpen(tillSessionDTO.isOpen());
        t.setUserId(tillSessionDTO.getUserId());
        t.setUsername(tillSessionDTO.getUsername());
        t.setTotal(tillSessionDTO.getTotal());
        t.setTillName(tillSessionDTO.getTillName());
        t.setStartTime(tillSessionDTO.getStartTime());
        t.setEndTime(tillSessionDTO.getEndTime());
        return t;
    }


    public static class TillSessionPaymentTotalDTO {
        private final String type;
        private final BigDecimal total;

        public TillSessionPaymentTotalDTO(String type, BigDecimal total) {
            this.type = type;
            this.total = total;
        }

        public String getType() {
            return type;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    public static class TillSessionBadgeCountDTO {
        private final String badgeName;
        private final Integer count;

        public TillSessionBadgeCountDTO(String badgeName, Integer count) {
            this.badgeName = badgeName;
            this.count = count;
        }

        public String getBadgeName() {
            return badgeName;
        }

        public Integer getCount() {
            return count;
        }
    }

    public static class TillSessionOrderDTO {
        private final Integer orderId;
        private final String badges;
        private final String payments;

        public TillSessionOrderDTO(Integer orderId, String badges, String payments) {
            this.orderId = orderId;
            this.badges = badges;
            this.payments = payments;
        }

        public Integer getOrderId() {
            return orderId;
        }

        public String getBadges() {
            return badges;
        }

        public String getPayments() {
            return payments;
        }
    }
}
