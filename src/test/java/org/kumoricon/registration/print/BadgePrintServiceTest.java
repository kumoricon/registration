package org.kumoricon.registration.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.staff.BadgeImageService;
import org.kumoricon.registration.model.staff.BadgeResourceService;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffBadgeDTO;
import org.kumoricon.registration.settings.SettingsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BadgePrintServiceTest {
    BadgePrintService badgePrintService;
    PrinterInfoService printerInfoService = mock(PrinterInfoService.class);
    BadgeService badgeService = mock(BadgeService.class);
    BadgeImageService badgeImageService = mock(BadgeImageService.class);
    BadgeResourceService badgeResourceService = mock(BadgeResourceService.class);
    SettingsService settingsService = mock(SettingsService.class);
    Boolean withAttendeeBackground = mock(Boolean.class);

    @BeforeEach
    void setUp() {
        badgePrintService = new BadgePrintService(printerInfoService, badgeService, badgeImageService, badgeResourceService, settingsService, withAttendeeBackground);
    }

    @Test
    void staffBadgeDTOsFromStaff() {
        // Staff badges should use the privacy name.
        // StaffBadgeDTO represents data as it's printed on a badge, where Staff represents
        // a person. Make sure the Privacy name is used for badges
        Staff s = new Staff();
        s.setFirstName("First");
        s.setLastName("Last");
        s.setPrivacyNameFirst("PrivacyFirst");
        s.setPrivacyNameLast("PrivacyLast");
        s.setBadgeNumber("1234");
        s.setPositions(List.of());

        List<StaffBadgeDTO> badgeDTOs = badgePrintService.staffBadgeDTOsFromStaff(List.of(s));
        assertEquals("PrivacyFirst", badgeDTOs.get(0).getFirstName());
        assertEquals("PrivacyLast", badgeDTOs.get(0).getLastName());
    }
}