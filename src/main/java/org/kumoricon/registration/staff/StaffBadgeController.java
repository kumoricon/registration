package org.kumoricon.registration.staff;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.Sides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.PrintException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
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
                             @RequestParam(name = "sides", defaultValue = "both") String sides,
                             HttpServletRequest request,
                             @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        String previousLink = request.getHeader("referer");
        previousLink = previousPage(previousLink);
        Sides printSides = Sides.from(sides);

        Staff s = staffRepository.findByUuid(uuid);
        log.info("printing badge for {} on {}", s, printerSettings.getPrinterName());
        try {
            String result = badgePrintService.printBadgesForStaff(List.of(s), printSides, printerSettings);
            if (!printSides.equals(Sides.FRONT)) {
                s.setBadgePrintCount(s.getBadgePrintCount() + 1);
                s.setBadgePrinted(true);
                staffRepository.save(s);
            }
            return "redirect:" + previousLink + "?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:" + previousLink + "?err=" + ex.getMessage();
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

        InputStream pdfStream = badgePrintService.generateStaffPDF(List.of(s), Sides.BOTH, printerSettings);
        byte[] media = pdfStream.readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/staff/badges-{page}.pdf")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public ResponseEntity<String> getAllStaffBadgePdf(@PathVariable Integer page,
                                                   @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {

        int start = page;
        log.info("export all staff badge PDF page {}+", page);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        boolean keepRunning = true;
        while (keepRunning) {
            long startTime = System.currentTimeMillis();
            File file = Paths.get("/tmp", "staff-badges-" + start + ".pdf").toFile();
            List<Staff> staffList = staffRepository.findAllWithPositions((start-1)*50);
            InputStream pdfStream = badgePrintService.generateStaffPDF(staffList, Sides.BOTH, printerSettings);
            byte[] media = pdfStream.readAllBytes();
            try(OutputStream os = new FileOutputStream(file)) {
                os.write(media);
            }
            long endTime = System.currentTimeMillis();
            log.info("Wrote {} bytes to {} in {} ms", media.length, file, endTime-startTime);
            if (staffList.size() < 50) {
                keepRunning = false;
            }
            start += 1;
        }
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>("Saved", headers, HttpStatus.OK);
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

    private String previousPage(String referrer) {
        try {
            URL url = new URL(referrer);
            return url.getPath();
        } catch (MalformedURLException e) {
            log.warn("Parsed bad referrer: {}", referrer, e);
            return referrer;
        }
    }

}