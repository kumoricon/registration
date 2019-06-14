package org.kumoricon.registration.model.order;

import java.math.BigDecimal;

public class OrderDTO {
    private Integer id;
    private String orderId;
    private Boolean paid;
    private BigDecimal totalDue;
    private BigDecimal totalPaid;

    private String orderTakenByUsername;
    private String notes;

    public OrderDTO(Integer id, String orderId, Boolean paid, BigDecimal totalDue, BigDecimal totalPaid, String orderTakenByUsername, String notes) {
        this.id = id;
        this.orderId = orderId;
        this.paid = paid;
        this.totalDue = totalDue;
        this.totalPaid = totalPaid;
        this.orderTakenByUsername = orderTakenByUsername;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Boolean getPaid() {
        return paid;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public String getOrderTakenByUsername() {
        return orderTakenByUsername;
    }

    public String getNotes() {
        return notes;
    }
}
