package org.kumoricon.registration.staff.staffimport.voucher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kumoricon.registration.model.ImportService;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.voucher.Voucher;
import org.kumoricon.registration.voucher.VoucherRepository;
import org.kumoricon.registration.voucher.VoucherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.time.*;
import java.util.List;

@Component
public class VoucherImportService extends ImportService {
    private static final Logger log = LoggerFactory.getLogger(VoucherImportService.class);
    private final StaffRepository staffRepository;
    private final VoucherRepository voucherRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final LocalDate FRIDAY = LocalDate.of(2024, 11, 8);
    private static final LocalTime TIME = LocalTime.of(11, 59, 59);
    private static final LocalDateTime FRIDAY_LOCAL_DATE = LocalDateTime.of(FRIDAY, TIME);

    public VoucherImportService(@Value("${staff.onlineImportPath}") String importInputPath,
                                @Value("${staff.voucherImportGlob}") String importGlob,
                                @Value("${staff.onlineDLQPath}") String importDLQPath,
                                StaffRepository staffRepository,
                                VoucherRepository voucherRepository) {
        this.staffRepository = staffRepository;
        this.voucherRepository = voucherRepository;
        this.onlineImportInputPath = importInputPath;
        this.onlineImportGlob = importGlob;
        this.onlineDLQPath = importDLQPath;
    }

    protected void importFile(Path filepath) throws IOException {
        final VoucherImportFile importFile = objectMapper.readValue(filepath.toFile(), VoucherImportFile.class);
        log.info("{}: Vouchers: {}", filepath, importFile.getVouchers().size());

        for (VoucherImportFile.VoucherImport voucherImport : importFile.getVouchers()) {
            importVouchers(voucherImport);
        }
    }

    private void importVouchers(VoucherImportFile.VoucherImport voucherImport) {
        final String[] names = voucherImport.getName().split("\\s+");
        final List<Staff> staffList = staffRepository.findByNameLike(voucherImport.getName());
        if (staffList.isEmpty()) {
            log.warn("STAFF MEMBER NOT FOUND: {} {}", names[0], names[1]);
            return;
        }

        final Staff staff = staffList.get(0);

        VoucherType voucherType = VoucherType.valueOf(voucherImport.getVoucherType().toUpperCase());

        // save current and set revoked to true for next to save
        if (!voucherImport.getTradedFor().isBlank()) {
            log.info("Saving revoked voucher for {} {}", staff.getFirstName(), staff.getLastName());
            final Voucher voucher = new Voucher();
            voucher.setStaffId(staff.getId());
            voucher.setVoucherType(voucherType);
            voucher.setVoucherDate(FRIDAY);
            voucher.setVoucherBy("admin");
            voucher.setVoucherAt(OffsetDateTime.of(FRIDAY_LOCAL_DATE, ZoneOffset.UTC));
            voucher.setIsRevoked(true);
            voucherRepository.save(voucher);

            voucherType = VoucherType.valueOf(voucherImport.getTradedFor().toUpperCase());
        }

        log.info("Saving voucher for {} {}", staff.getFirstName(), staff.getLegalFirstName());
        final Voucher voucher = new Voucher();
        voucher.setStaffId(staff.getId());
        voucher.setVoucherType(voucherType);
        voucher.setVoucherDate(FRIDAY);
        voucher.setVoucherBy("admin");
        voucher.setVoucherAt(OffsetDateTime.of(FRIDAY_LOCAL_DATE, ZoneOffset.UTC));
        voucher.setIsRevoked(false);
        voucherRepository.save(voucher);

    }
}
