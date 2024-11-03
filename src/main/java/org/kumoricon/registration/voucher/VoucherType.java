package org.kumoricon.registration.voucher;

public enum VoucherType {
    HYATT,
    OCC;

    public static VoucherType of(final int voucherType) {
        if (voucherType == 0) {
            return HYATT;
        }
        if (voucherType == 1) {
            return OCC;
        }

        return HYATT;
    }
}
