package org.kumoricon.registration.reg;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentFormDTO {
    private Integer id;
    @NotNull
    private String paymentType;
    @NotNull
    private BigDecimal amount;
    private String authNumber;

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

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }
}
