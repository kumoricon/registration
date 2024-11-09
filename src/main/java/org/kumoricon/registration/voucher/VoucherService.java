package org.kumoricon.registration.voucher;

import org.kumoricon.registration.model.staff.Staff;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final ZoneId timezone;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
        this.timezone = ZoneId.of("America/Los_Angeles");
    }

    public void saveNewVoucher(final Staff staff, final VoucherType voucherType, final String username) {
        final Voucher voucher = new Voucher();

        voucher.setStaffId(staff.getId());
        voucher.setVoucherType(voucherType);
        voucher.setVoucherDate(LocalDate.now(timezone));
        voucher.setVoucherBy(username);
        voucher.setVoucherAt(OffsetDateTime.now());
        voucher.setIsRevoked(false);

        voucherRepository.save(voucher);
    }
}
