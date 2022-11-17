package org.kumoricon.registration.controlleradvice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
public class VersionControllerAdvice {
    private final String version;

    public VersionControllerAdvice(@Value("${registration.version}") String version) {
        this.version = version;
    }
    /**
     * Returns the current service version set in pom.xml when the service is packaged. Set in the property
     * "registration.version" by maven-resources-plugin
     * @return version
     */
    @ModelAttribute("appVersion")
    public String getVersion() {
        return version;
    }

}

