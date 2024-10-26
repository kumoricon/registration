package org.kumoricon.registration.voucher;

import org.kumoricon.registration.model.SearchSuggestion;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffAutoSuggestRepository;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class VoucherCheckInController {
    private static final Logger log = LoggerFactory.getLogger(VoucherCheckInController.class);
    private final StaffAutoSuggestRepository staffAutoSuggestRepository;
    private final StaffRepository staffRepository;
    private final VoucherRepository voucherRepository;
    private final ZoneId timezone;

    public VoucherCheckInController(StaffAutoSuggestRepository staffAutoSuggestRepository,
                                    StaffRepository staffRepository,
                                    VoucherRepository voucherRepository) {
        this.staffAutoSuggestRepository = staffAutoSuggestRepository;
        this.staffRepository = staffRepository;
        this.voucherRepository = voucherRepository;
        this.timezone = ZoneId.of("America/Los_Angeles");
    }

    @RequestMapping(value = "/voucher/search", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String voucherSearch(Model model,
                          @RequestParam(required = false, name = "q", defaultValue = "") String search) {
        if (search == null || search.isBlank()) {
            log.info("searching staff list for {}", search);
        }

        final List<Staff> staff = staffRepository.findByNameLike(search);
        final Map<Integer, Voucher> staffVouchers = voucherRepository.findByAllStaffOnDate(staff, LocalDate.now(timezone))
                .stream()
                .collect(Collectors.toMap(Voucher::getStaffId, Function.identity()));
        model.addAttribute("staff", staff);
        model.addAttribute("totalCount", staffRepository.count());
        model.addAttribute("search", search);
        model.addAttribute("dailyVoucherCount", voucherRepository.countOnDate(LocalDate.now(timezone)));
        model.addAttribute("staffVouchers", staffVouchers);

        return "voucher/search";
    }

    @RequestMapping(value = "/voucher/suggest", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    @ResponseBody
    public SearchSuggestion suggest(@RequestParam(name = "query") String query) {
        return new SearchSuggestion(query, staffAutoSuggestRepository.findNamesLike(query));
    }

    @RequestMapping(value = "/voucher/checkin/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String checkInVoucher(Model model,
                          @RequestParam(required = false, name = "q", defaultValue = "") String search,
                          @PathVariable(name = "uuid") String uuid,
                          @AuthenticationPrincipal User principal) {
        final Staff staff = staffRepository.findByUuid(uuid);
        final Voucher voucher = new Voucher();
        voucher.setStaffId(staff.getId());
        voucher.setVoucherDate(LocalDate.now(timezone));
        voucher.setVoucherBy(principal.getUsername());
        voucher.setVoucherAt(OffsetDateTime.now());
        voucherRepository.save(voucher);

        model.addAttribute("search", search);

        return "redirect:/voucher/search?msg=Voucher+marked+received+for+" + staff.getFirstName() + "+" + staff.getLastName() + "&q=" + search;
    }

    @RequestMapping(value = "/voucher/revoke/{uuid}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String revokeVoucher(Model model,
                         @PathVariable(name = "uuid") String uuid,
                         @AuthenticationPrincipal User principal) {
        final Staff staff = staffRepository.findByUuid(uuid);
        final Voucher voucher = voucherRepository.findByStaffIdOnDate(staff.getId(), LocalDate.now(timezone));

        model.addAttribute("staff", staff);
        model.addAttribute("voucher", voucher);

        return "voucher/revoke";
    }

    @RequestMapping(value = "/voucher/revoke/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('voucher_check_in')")
    public String revokeVoucherPost(Model model,
                         @PathVariable(name = "uuid") String uuid,
                         @AuthenticationPrincipal User principal) {
        final Staff staff = staffRepository.findByUuid(uuid);
        final Voucher voucher = voucherRepository.findByStaffIdOnDate(staff.getId(), LocalDate.now(timezone));
        voucherRepository.delete(voucher);
        log.info("Deleted voucher for {}", staff);

        model.addAttribute("staff", staff);
        model.addAttribute("voucher", voucher);

        return "redirect:/voucher/search?err=Voucher%20revoked%20%20for%20" + staff.getFirstName() + "%20" + staff.getLastName();
    }
}
