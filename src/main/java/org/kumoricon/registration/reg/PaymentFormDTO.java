package org.kumoricon.registration.reg;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentFormDTO {
    private Integer id;
    @NotNull
    private String paymentType;
    @NotNull
    private BigDecimal amount;
    private String squareReceiptNumber;
    private String checkNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}
