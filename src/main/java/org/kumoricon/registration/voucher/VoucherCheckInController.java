package org.kumoricon.registration.voucher;

import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class VoucherCheckInController {
    private static final Logger log = LoggerFactory.getLogger(VoucherCheckInController.class);
    private final StaffRepository staffRepository;
    private final VoucherRepository voucherRepository;

    public VoucherCheckInController(StaffRepository staffRepository, VoucherRepository voucherRepository) {
        this.staffRepository = staffRepository;
        this.voucherRepository = voucherRepository;
    }

    @RequestMapping(value = "/voucher/search", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String voucher(Model model,
                          @RequestParam(required = false, name = "q", defaultValue = "") String search) {
        if (search == null || search.isBlank()) {
            log.info("searching staff list for {}", search);
        }

        final List<Staff> staff = staffRepository.findByNameLike(search);
        final Map<Integer, Voucher> voucherStaff = voucherRepository.findByAllStaff(staff)
                .stream()
                .collect(Collectors.toMap(Voucher::getStaffId, Function.identity()));
        log.info("VOUCHER MAP {}", voucherStaff);
        model.addAttribute("staff", staff);
        model.addAttribute("totalCount", staffRepository.count());
        model.addAttribute("search", search);
        model.addAttribute("checkedInCount", staffRepository.countByCheckedIn(true));
        model.addAttribute("voucherStaff", voucherStaff);

        return "voucher/search";
    }
/*
    @RequestMapping(value = "/voucher/checkin/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String voucher1(Model model,
                           @PathVariable(name = "uuid") String uuid) {
        final Staff staff = staffRepository.findByUuid(uuid);
        final Voucher voucher = voucherRepository.findByStaffId(uuid);
        if (voucher != null) {
            log.warn("")
        }
    }*/

    @RequestMapping(value = "/voucher/checkin/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String voucherPost(Model model,
                              @RequestParam(required = false, name = "q", defaultValue = "") String search,
                              @PathVariable(name = "uuid") String uuid,
                              @AuthenticationPrincipal User principal) {
        final Staff staff = staffRepository.findByUuid(uuid);
        final Voucher voucher = new Voucher();
        voucher.setStaffId(staff.getId());
        voucher.setVoucherDate(LocalDate.now());
        voucher.setVoucherBy(principal.getUsername());
        voucher.setVoucherAt(OffsetDateTime.now());
        voucherRepository.save(voucher);

        model.addAttribute("search", search);

        return "voucher/search";
    }
}
