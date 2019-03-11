package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.tillsession.TillSession;
import org.kumoricon.registration.model.user.User;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    private Integer id;
    private BigDecimal amount;
    private Integer orderId;

    private Integer paymentTakenBy;
    private PaymentType paymentType;
    private Instant paymentTakenAt;
    private String paymentLocation;
    private String authNumber;
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

    public Instant getPaymentTakenAt() {
        return paymentTakenAt;
    }
    public void setPaymentTakenAt(Instant paymentTakenAt) {
        this.paymentTakenAt = paymentTakenAt;
    }

    public String getPaymentLocation() {
        return paymentLocation;
    }
    public void setPaymentLocation(String paymentLocation) {
        this.paymentLocation = paymentLocation;
    }

    public String getAuthNumber() { return authNumber; }
    public void setAuthNumber(String authNumber) { this.authNumber = authNumber; }

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