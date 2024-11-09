package org.kumoricon.registration.voucher;

import org.kumoricon.registration.model.staff.Staff;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public void saveNewVoucher(final Staff staff, final VoucherType voucherType, final LocalDate date, final String username) {
        final Voucher voucher = new Voucher();

        voucher.setStaffId(staff.getId());
        voucher.setVoucherType(voucherType);
        voucher.setVoucherDate(date);
        voucher.setVoucherBy(username);
        voucher.setVoucherAt(OffsetDateTime.now());
        voucher.setIsRevoked(false);

        voucherRepository.save(voucher);
    }
}
