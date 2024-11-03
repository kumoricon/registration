package org.kumoricon.registration.voucher;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Voucher {
    private Integer id;
    private Integer staffId;
    private VoucherType voucherType;
    private LocalDate voucherDate;
    private String voucherBy;
    private OffsetDateTime voucherAt;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(final Integer staffId) {
        this.staffId = staffId;
    }

    public VoucherType getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(final VoucherType voucherType) {
        this.voucherType = voucherType;
    }

    public LocalDate getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(final LocalDate voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getVoucherBy() {
        return voucherBy;
    }

    public void setVoucherBy(final String voucherBy) {
        this.voucherBy = voucherBy;
    }

    public OffsetDateTime getVoucherAt() {
        return voucherAt;
    }

    public void setVoucherAt(final OffsetDateTime voucherAt) {
        this.voucherAt = voucherAt;
    }

    @Override
    public String toString() {
        return String.format("Voucher={id=%s, staffId=%s, voucherDate=%s, voucherBy=%s, voucherAt=%s}", id, staffId, voucherDate, voucherBy, voucherAt);
    }
}
