package org.kumoricon.registration.staff;

import org.kumoricon.registration.model.SearchSuggestion;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffAutoSuggestRepository;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.OffsetDateTime;

@Controller
public class CheckInController {
    private final StaffRepository staffRepository;
    private final StaffAutoSuggestRepository staffAutoSuggestRepository;
    private final FileStorageService fileStorageService;
    private final SettingsService settingsService;
    private final Logger log = LoggerFactory.getLogger(CheckInController.class);

    public CheckInController(StaffRepository staffRepository,
                             StaffAutoSuggestRepository staffAutoSuggestRepository,
                             FileStorageService fileStorageService,
                             SettingsService settingsService) {
        this.staffRepository = staffRepository;
        this.staffAutoSuggestRepository = staffAutoSuggestRepository;
        this.fileStorageService = fileStorageService;
        this.settingsService = settingsService;
    }

    @RequestMapping(value = "/staff")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String staffList(Model model,
                            @RequestParam(required = false, name = "q", defaultValue = "") String search) {
        if (!search.isBlank()) {
            log.info("searching staff list for {}", search);
        }

        model.addAttribute("staff", staffRepository.findByNameLike(search));
        model.addAttribute("totalCount", staffRepository.count());
        model.addAttribute("search", search);
        model.addAttribute("checkedInCount", staffRepository.countByCheckedIn(true));

        return "staff/stafflist";
    }

    @RequestMapping(value = "/staff/suggest", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('staff_check_in')")
    @ResponseBody
    public SearchSuggestion suggest(@RequestParam(name = "query") String query) {
        return new SearchSuggestion(query, staffAutoSuggestRepository.findNamesLike(query));
    }

    @RequestMapping(value = "/staff/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String viewStaff(Model model,
                            @PathVariable String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        model.addAttribute("staff", staff);
        return "staff/staff-id";
    }

    @RequestMapping(value = "/staff/{uuid}/accessibilitysticker", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String accessibilityStickerToggle(@PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        Boolean newValue = !staff.getAccessibilitySticker();
        log.info("set accessibility sticker to {} for {}", newValue, staff);
        staff.setAccessibilitySticker(newValue);
        staffRepository.save(staff);
        return "redirect:/staff/" + uuid;
    }

    @RequestMapping(value = "/staff/checkin/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn1(Model model, @PathVariable(name = "uuid") String uuid) {
        model.addAttribute("staff", staffRepository.findByUuid(uuid));
        return "staff/step1";
    }

    @RequestMapping(value = "/staff/checkin/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn1Post(@PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        if (staff.getCheckedIn()) return "redirect:/staff/checkin/" + uuid + "?err=Already+checked+in";
        staff.setInformationVerified(true);
        staffRepository.save(staff);
        log.info("verified information for {}", staff);
        return "redirect:/staff/checkin2/" + uuid;
    }

    @RequestMapping(value = "/staff/checkin2/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn2(Model model, @PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        if (!staff.getInformationVerified()) {
            return "redirect:/staff/checkin/" + uuid + "?err=Information+not+verified";
        }
        model.addAttribute("staff", staff);
        model.addAttribute("requirePhoto", settingsService.getCurrentSettings().getRequireStaffPhoto());
        return "staff/step2";
    }

    @RequestMapping(value = "/staff/checkin2/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn2Post(@PathVariable(name = "uuid") String uuid,
                               @RequestParam("imageData") String imageData) {
        Staff staff = staffRepository.findByUuid(uuid);
        if (staff.getCheckedIn()) return "redirect:/staff/checkin/" + uuid + "?err=Already+checked+in";

        if (imageData.isEmpty() && !settingsService.getCurrentSettings().getRequireStaffPhoto()) {
            imageData = NO_DATA_SAVED_IMAGE;    // A blank image will be saved if the incoming image is blank AND
        }                                       // the staff photo isn't required

        log.info("saved staff photo for {}", staff);

        try {
            fileStorageService.storeFile(staff.getFirstName() + "_" + staff.getLastName() + "_" + staff.getUuid() + "-photo", imageData);
        } catch (IOException ex) {
            log.error("Error saving image", ex);
            return "staff/step2?err=Error+saving+image";
        }
        staff.setPictureSaved(true);
        staff.setCheckedIn(true);
        staff.setCheckedInAt(OffsetDateTime.now());
        staffRepository.save(staff);
        return "redirect:/staff/checkin3/" + uuid;
    }

    @RequestMapping(value = "/staff/checkin3/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkInStep4(Model model, @PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        if (!staff.getInformationVerified()) {
            return "redirect:/staff/checkin/" + uuid + "?err=Information+not+verified";
        }
        if (!staff.getPictureSaved()) {
            return "redirect:/staff/checkin2/" + uuid + "?err=Picture+not+saved";
        }
        model.addAttribute("staff", staff);
        return "staff/step3";
    }

    @RequestMapping(value = "/staff/checkin3/{uuid}/accessibilitysticker", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String accessibilityStickerToggleCheckIn(@PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        Boolean newValue = !staff.getAccessibilitySticker();
        log.info("set accessibility sticker to {} for {}", newValue, staff);
        staff.setAccessibilitySticker(newValue);
        staffRepository.save(staff);
        return "redirect:/staff/checkin3/" + uuid;
    }

    private static final String NO_DATA_SAVED_IMAGE = """
            data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAAAUCAAAAABeq8dJAAABxUlEQVQ4y+2UTUsC
            URSG5w/4D1r3AwQhEKFNRLhwVbuoVStrUQgWWmaWi4wiCAL7LiICicykj0EUbKwmKKJFQVMGEwRBNnfKatR7mrkzji1CiD4g8Kwu7znveT
            j33BkK/iCoCuS/QJ5dhR9pn2QIRDKG5DOty4WIzbYmfeoQ+/BnchmHDml03H+EcB0PrwcXX4GUceiQ1rOgDME71trVvCzw3px6XThsaZ9j
            UPeMcTCbNxpse3iUoibvRzEkGOQMWjLJJtPUm+4gJUAnAaL7QFJ4Q2mgQXKBdJLmnJmnkSNFOV9ajL7IkMs+QfQyyJqWFlgyBlIm0SB1ab
            jxC1IkrjvUEsFVkBxZNcW5BeQpQvL8UIJObQGwYW1IdlaGpHYB4gzqx8DGnsdrDFW3RYisejCkKDmWSw5SAtNXpyEtpTYoQmDeRXM9j+ok
            XEyUomNkEiQOMMirQE4mXjNOPtshgdgmiD6iXgeQthPiOCYlwE0P32kprheJ/SXIg5nGWw2WFWUnObrF6L1TdrJutgcPVQhyV48EeDxTPQ
            mbps55AoG9ZgPF6g61BApd/mJKXqpd20m5Vy64+V/+4rHPUL9d+XdVIN+Id/zXQJnVUxn4AAAAAElFTkSuQmCC
            """.replace("\n", "");
}
