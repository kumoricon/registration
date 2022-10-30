package org.kumoricon.registration.print.formatter.badgeimage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BadgeCreatorStaffBaseTest {

    BadgeCreatorStaffBase bc;
    @BeforeEach
    void setUp() {
        bc = new BadgeCreatorStaffBase(null, null);
    }

    @Test
    void buildNameListNoFanName() {
        StaffBadgeDTO sb = new StaffBadgeDTO.Builder().withFirstName("First").withLastName("Last").build();
        assertEquals("[First, Last]", Arrays.toString(bc.buildNameList(sb)));
    }

    @Test
    void buildNameListWithFanName() {
        StaffBadgeDTO sb = new StaffBadgeDTO.Builder().withFanName("Fan Name").withFirstName("First").withLastName("Last").build();
        assertEquals("[Fan Name, First Last]", Arrays.toString(bc.buildNameList(sb)));
    }

    @Test
    void buildNameListNoLastName() {
        StaffBadgeDTO sb = new StaffBadgeDTO.Builder().withFirstName("First").build();
        assertEquals("[First]", Arrays.toString(bc.buildNameList(sb)));
    }
}
