package org.kumoricon.registration.utility;

import org.kumoricon.registration.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Controller
public class AttendeeImportController {
    private final AttendeeImporterService attendeeImporterService;

    @Autowired
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

        try {
            String result = attendeeImporterService.importFromJSON(new InputStreamReader(file.getInputStream()), (User) auth.getPrincipal());
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("err", e.getMessage());
            return "utility/attendeeimport-upload";
        }

        return "utility/attendeeimport-upload";
    }
}
