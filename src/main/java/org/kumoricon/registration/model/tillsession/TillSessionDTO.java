package org.kumoricon.registration.model.tillsession;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TillSessionDTO {
    private Integer id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String username;
    private Integer userId;
    private BigDecimal total;
    private String tillName;
    private boolean open;

    public TillSessionDTO() {
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
}
