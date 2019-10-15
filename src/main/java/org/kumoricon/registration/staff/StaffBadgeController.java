package org.kumoricon.registration.staff;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.print.BadgePrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.print.PrintException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class StaffBadgeController {
    private final Logger log = LoggerFactory.getLogger(StaffBadgeController.class);
    private final StaffRepository staffRepository;
    private final BadgePrintService badgePrintService;

    public StaffBadgeController(StaffRepository staffRepository,
                                BadgePrintService badgePrintService) {
        this.staffRepository = staffRepository;
        this.badgePrintService = badgePrintService;
    }

    @RequestMapping(value = "/staff/{uuid}/print", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('staff_check_in')")
    public String printBadge(@PathVariable(name = "uuid") String uuid,
                             @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        Staff s = staffRepository.findByUuid(uuid);
        try {
            String result = badgePrintService.printBadgesForStaff(List.of(s), printerSettings);
            s.setBadgePrintCount(s.getBadgePrintCount() + 1);
            s.setBadgePrinted(true);
            staffRepository.save(s);
            return "redirect:/staff?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/staff/" + s.getUuid() + "/printBadge/" + "?err=" + ex.getMessage();
        }
    }

    @RequestMapping(value = "/staff/{uuid}/badge.pdf")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public ResponseEntity<byte[]> getStaffBadgePdf(@PathVariable String uuid,
                                              @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {

        Staff s = staffRepository.findByUuid(uuid);
        if (s == null) throw new RuntimeException("Staff " + uuid + " not found");
        log.info("downloaded badge PDF for {}", s);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        InputStream pdfStream = badgePrintService.generateStaffPDF(List.of(s), printerSettings);
        byte[] media = pdfStream.readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/staff/{uuid}/badge.png")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public ResponseEntity<byte[]> getBadgeImage(@PathVariable String uuid) {
        Staff s = staffRepository.findByUuid(uuid);
        if (s == null) throw new RuntimeException("Staff " + uuid + " not found");

        log.info("viewed badge image for {}", s);

        byte[] media = badgePrintService.generateStaffImage(List.of(s));
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }


    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setExpires(3);
        return headers;
    }

}