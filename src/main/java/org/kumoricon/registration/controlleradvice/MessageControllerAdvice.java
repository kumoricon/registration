package org.kumoricon.registration.controlleradvice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * This adds the "msg" and "err" attributes to every model, which are used to
 * display messages and errors on the page. For example, after a new item
 * is saved successfully, you can add the query parameter ?msg=Saved%20Successfully
 * to the redirect URL, and the message "Saved Successfully" will be displayed at the
 * top of the page.
 *
 * "msg" indicates a normal or success message and will be displayed in a green box
 * "err" indicates an error or failure message and will be displayed in a red box
 */
@ControllerAdvice
public class MessageControllerAdvice {

    @ModelAttribute("msg")
    public String getMessage(@RequestParam(required=false) String msg) {
        return msg;
    }

    @ModelAttribute("err")
    public String getError(@RequestParam(required = false) String err) {
        return err;
    }
}

