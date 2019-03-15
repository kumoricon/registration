package org.kumoricon.registration.utility;


import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class PrinterController {


    @Autowired
    public PrinterController() {

    }

    @RequestMapping(value = "/utility/printer")
    public String printer(Model model,
                          @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerName) {
        model.addAttribute("printer", printerName);
        return "utility/printer";
    }

    @RequestMapping(value = "/utility/printer", method = RequestMethod.POST)
    public String setPrinter(Model model, @RequestParam String printer, HttpServletResponse response) {
        String newPrinter = printer.trim();
        try {
            model.addAttribute("printer", newPrinter);
            model.addAttribute("msg", "Selected printer \"" + newPrinter + "\"");
            Cookie cookie = new Cookie(CookieControllerAdvice.PRINTER_COOKIE_NAME, newPrinter);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } catch (Exception e) {
            model.addAttribute("err", e.getMessage());

        }

        return "redirect:/utility/printer?msg=Printer+Saved";
    }
}