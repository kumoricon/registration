package org.kumoricon.registration.model.order;

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
    private String squareReceiptNumber;
    private String checkNumber;
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

    public String getSquareReceiptNumber() {
        return squareReceiptNumber;
    }

    public void setSquareReceiptNumber(String squareReceiptNumber) {
        if (squareReceiptNumber != null && squareReceiptNumber.length() < 4) {
            throw new RuntimeException("Invalid square receipt number: " + squareReceiptNumber + " (cannot be less than 4 characters)");
        }

        this.squareReceiptNumber = squareReceiptNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        // keeping 10-char length check for legacy reasons
        // if we don't want to validate against this length we can remove
        if (checkNumber != null && checkNumber.length() >= 10) {
            throw new RuntimeException("Invalid check number: " + checkNumber + " (must be less than 10 characters)");
        }
        this.checkNumber = checkNumber;
    }

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