package org.kumoricon.registration.healthcheck;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class HealthcheckController {
    @RequestMapping(value="/healthcheck", method=GET, produces="text/plain")
    public String getHealthcheck() {
        return "ok\n";
    }

}