package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.tillsession.TillSession;
import org.kumoricon.registration.model.user.User;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Payment {
    private Integer id;
    private BigDecimal amount;
    private Integer orderId;

    private Integer paymentTakenBy;
    private PaymentType paymentType;
    private OffsetDateTime paymentTakenAt;
    private String paymentLocation;
    private String squareReceiptNumber;
    private String checkNumber;
    private Integer tillSessionId;

    public enum PaymentType {
        CASH(0) {
            public String toString() {
                return "Cash";
            }
        },
        CHECK(1) {
            public String toString() {
                return "Check/Money Order";
            }
        },
        CREDIT(2) {
            public String toString() {
                return "Credit Card";
            }
        },
        PREREG(3) {
            public String toString() {
                return "Pre Reg";
            }
        };

        private final int value;
        PaymentType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PaymentType fromInteger(Integer typeId) {
            PaymentType[] paymentTypes = PaymentType.values();
            return paymentTypes[typeId];
        }
    }

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

    public Integer getPaymentTakenBy() {
        return paymentTakenBy;
    }
    public void setPaymentTakenBy(User paymentTakenBy) {
        this.paymentTakenBy = paymentTakenBy.getId();
    }
    public void setPaymentTakenBy(Integer userId) {
        this.paymentTakenBy = userId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public OffsetDateTime getPaymentTakenAt() {
        return paymentTakenAt;
    }
    public void setPaymentTakenAt(OffsetDateTime paymentTakenAt) {
        this.paymentTakenAt = paymentTakenAt;
    }

    public String getPaymentLocation() {
        return paymentLocation;
    }
    public void setPaymentLocation(String paymentLocation) {
        this.paymentLocation = paymentLocation;
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
    public void setTillSession(TillSession tillSession) { this.tillSessionId = tillSession.getId(); }

    public String toString() {
        if (id != null) {
            return String.format("[Payment %s: $%s]", id, getAmount());
        } else {
            return String.format("[Payment: $%s]", getAmount());
        }
    }
}