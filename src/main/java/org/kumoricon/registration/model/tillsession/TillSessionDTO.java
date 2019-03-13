package org.kumoricon.registration.model.tillsession;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TillSessionDTO {
    private Integer id;
    private Instant startTime;
    private Instant endTime;
    private String username;
    private Integer userId;
    private BigDecimal total;
    private boolean open;
    private Map<String, BigDecimal> paymentTotals;

    public TillSessionDTO() {
        this.paymentTotals = new HashMap<>();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime == null ? null : startTime.toInstant();
    }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime == null ? null : endTime.toInstant();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public BigDecimal getTotal() { return total; }

    public void setTotal(BigDecimal total) { this.total = total; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    public Map<String, BigDecimal> getPaymentTotals() { return paymentTotals; }
    public void setPaymentTotals(Map<String, BigDecimal> paymentTotals) { this.paymentTotals = paymentTotals; }
}
