package org.kumoricon.registration.utility;

import org.kumoricon.registration.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Controller
public class AttendeeImportController {
    private final AttendeeImporterService attendeeImporterService;

    public AttendeeImportController(AttendeeImporterService attendeeImporterService) {
        this.attendeeImporterService = attendeeImporterService;
    }

    @RequestMapping(value = "/utility/attendeeimport")
    public String attendeeimport(Model model) {
        return "utility/attendeeimport";
    }

    @RequestMapping(value = "/utility/attendeeimport/upload", method = RequestMethod.POST)
    public String uploadFile(MultipartFile file, Model model, Authentication auth) {
        model.addAttribute("filename", file.getOriginalFilename());
        model.addAttribute("filesize", file.getSize());

        try (InputStream input = file.getInputStream()) {
            String result = attendeeImporterService.importFromJSON(input, (User) auth.getPrincipal());
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("err", e.getMessage());
            return "utility/attendeeimport-upload";
        }

        return "utility/attendeeimport-upload";
    }
}
