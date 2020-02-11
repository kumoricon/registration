package org.kumoricon.registration.admin.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class ExportController {
    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    @RequestMapping(value = "/admin/export")
    @PreAuthorize("hasAuthority('manage_export')")
    public String exportPage() {
        return "admin/export";
    }

}
