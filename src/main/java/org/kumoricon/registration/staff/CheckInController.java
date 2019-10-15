package org.kumoricon.registration.staff;

import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Controller
public class CheckInController {
    private final StaffRepository staffRepository;
    private final FileStorageService fileStorageService;
    private final Logger log = LoggerFactory.getLogger(CheckInController.class);

    public CheckInController(StaffRepository staffRepository, FileStorageService fileStorageService) {
        this.staffRepository = staffRepository;
        this.fileStorageService = fileStorageService;
    }

    @RequestMapping(value="/staff")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String staffList(Model model) {
        List<Staff> staff = staffRepository.findAll();
        int checkedIn = 0;          // Just count them here to save another database call, since
        for (Staff s : staff) {     // We need the whole list anyway
            if (s.getCheckedIn()) checkedIn++;
        }
        model.addAttribute("staff", staff);
        model.addAttribute("checkedIn", checkedIn);

        return "staff/stafflist";
    }

    @RequestMapping(value = "/staff/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String viewStaff(Model model,
                            @PathVariable String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        model.addAttribute("staff", staff);
        return "staff/staff-id";
    }


    @RequestMapping(value = "/staff/checkin/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn1(Model model, @PathVariable(name = "uuid") String uuid) {
        model.addAttribute("staff", staffRepository.findByUuid(uuid));
        return "staff/step1";
    }

    @RequestMapping(value = "/staff/checkin/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn1Post(Model model, @PathVariable(name = "uuid") String uuid) {
        Staff s = staffRepository.findByUuid(uuid);
        s.setInformationVerified(true);
        staffRepository.save(s);
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
        return "staff/step2";
    }

    @RequestMapping(value = "/staff/checkin2/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn2Post(Model model,
                               @PathVariable(name = "uuid") String uuid,
                               @RequestParam("imageData") String imageData) {
        Staff s = staffRepository.findByUuid(uuid);
        try {
            fileStorageService.storeFile(s.getFirstName() + "_" + s.getLastName() + "_" + s.getUuid() + ".png", imageData);
        } catch (IOException ex) {
            log.error("Error saving image", ex);
            return "staff/step2?err=Error+saving+image";
        }
        s.setPictureSaved(true);
        staffRepository.save(s);
        return "redirect:/staff/checkin3/" + uuid;
    }

    @RequestMapping(value = "/staff/checkin3/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn3(Model model, @PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        if (!staff.getPictureSaved()) {
            return "redirect:/staff/checkin2/" + uuid + "?err=Picture+not+saved";
        }
        model.addAttribute("staff", staff);
        return "staff/step3";
    }

    @RequestMapping(value = "/staff/checkin3/{uuid}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkIn3Post(Model model,
                               @PathVariable(name = "uuid") String uuid,
                               @RequestParam("imageData") String imageData) {

        Staff s = staffRepository.findByUuid(uuid);
        try {
            fileStorageService.storeFile(s.getFirstName() + "_" + s.getLastName() + "_" + s.getUuid() + "-signature.png", imageData);
        } catch (IOException ex) {
            log.error("Error saving image", ex);
            return "staff/step3?err=Error+saving+image";
        }

        s.setSignatureSaved(true);
        s.setCheckedIn(true);
        s.setCheckedInAt(Instant.now());
        staffRepository.save(s);
        return "redirect:/staff/checkin4/" + uuid;
    }

    @RequestMapping(value = "/staff/checkin4/{uuid}")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String checkInStep4(Model model, @PathVariable(name = "uuid") String uuid) {
        Staff staff = staffRepository.findByUuid(uuid);
        if (!staff.getInformationVerified()) {
            return "redirect:/staff/checkin/" + uuid + "?err=Information+not+verified";
        }
        if (!staff.getPictureSaved()) {
            return "redirect:/staff/checkin2/" + uuid + "?err=Picture+not+saved";
        }
        if (!staff.getSignatureSaved()) {
            return "redirect:/staff/checkin3/" + uuid + "?err=Signature+not+saved";
        }
        model.addAttribute("staff", staff);
        return "staff/step4";
    }
}
