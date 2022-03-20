package org.kumoricon.registration.utility.badgeexport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BadgeExport {

    private String type;
    private boolean withAttendeeBackground;
    private boolean markPreprinted;
    private String path = "/tmp";

    public BadgeExport(@Value("${badge.printAttendeeBackgrounds}") boolean withAttendeeBackground) {
        this.withAttendeeBackground = withAttendeeBackground;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWithAttendeeBackground() {
        return withAttendeeBackground;
    }

    public void setWithAttendeeBackground(boolean withAttendeeBackground) {
        this.withAttendeeBackground = withAttendeeBackground;
    }

    public boolean isMarkPreprinted() {
        return markPreprinted;
    }

    public void setMarkPreprinted(boolean markPreprinted) {
        this.markPreprinted = markPreprinted;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
