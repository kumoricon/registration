package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.tillsession.TillSession;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentDTO {
    private Integer id;
    private BigDecimal amount;
    private Integer orderId;
    private Integer paymentTakenBy;
    private String paymentTakenByUsername;
    private String tillName;
    private Payment.PaymentType paymentType;
    private OffsetDateTime paymentTakenAt;
    private String paymentLocation;
    private String authNumber;
    private Integer tillSessionId;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public void setOrder(Order order) {
        this.orderId = order.getId();
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPaymentTakenBy() { return paymentTakenBy; }
    public void setPaymentTakenBy(Integer paymentTakenBy) { this.paymentTakenBy = paymentTakenBy; }

    public String getPaymentTakenByUsername() { return paymentTakenByUsername; }
    public void setPaymentTakenByUsername(String paymentTakenByUsername) { this.paymentTakenByUsername = paymentTakenByUsername; }

    public String getTillName() { return tillName; }
    public void setTillName(String tillName) { this.tillName = tillName; }

    public String getPaymentLocation() { return paymentLocation; }

    public void setPaymentLocation(String paymentLocation) { this.paymentLocation = paymentLocation; }

    public Payment.PaymentType getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(Payment.PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public OffsetDateTime getPaymentTakenAt() {
        return paymentTakenAt;
    }
    public void setPaymentTakenAt(OffsetDateTime paymentTakenAt) {
        this.paymentTakenAt = paymentTakenAt;
    }

    public String getAuthNumber() { return authNumber; }
    public void setAuthNumber(String authNumber) { this.authNumber = authNumber; }

    public Integer getTillSessionId() { return tillSessionId; }
    public void setTillSessionId(Integer tillSessionId) { this.tillSessionId = tillSessionId; }

    public String toString() {
        if (id != null) {
            return String.format("[Payment %s: $%s]", id, getAmount());
        } else {
            return String.format("[Payment: $%s]", getAmount());
        }
    }
}