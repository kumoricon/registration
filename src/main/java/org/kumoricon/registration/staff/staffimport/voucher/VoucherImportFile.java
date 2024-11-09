package org.kumoricon.registration.staff.staffimport.voucher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class VoucherImportFile {
    private List<VoucherImport> vouchers;

    @JsonCreator
    public VoucherImportFile(@JsonProperty("vouchers") final List<VoucherImport> vouchers) {
        this.vouchers = vouchers;
    }

    public List<VoucherImport> getVouchers() {
        return vouchers;
    }

    static class VoucherImport {
        private final String name;
        private final String voucherType;
        private final String tradedFor;
        private final String pickedUpBy;

        @JsonCreator
        public VoucherImport(@JsonProperty("name") String name,
                             @JsonProperty("voucher_type") String voucherType,
                             @JsonProperty("traded_for") String tradedFor,
                             @JsonProperty("picked_up_by") String pickedUpBy) {
            this.name = name;
            this.voucherType = voucherType;
            this.tradedFor = tradedFor;
            this.pickedUpBy = pickedUpBy;
        }

        public String getName() {
            return name;
        }

        public String getVoucherType() {
            return voucherType;
        }

        public String getTradedFor() {
            return tradedFor;
        }

        public String getPickedUpBy() {
            return pickedUpBy;
        }
    }
}
