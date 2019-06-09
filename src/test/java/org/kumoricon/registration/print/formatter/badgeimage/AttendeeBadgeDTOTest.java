package org.kumoricon.registration.print.formatter.badgeimage;

import org.junit.Test;

import static org.junit.Assert.*;

public class AttendeeBadgeDTOTest {

    @Test
    public void setNameFirstOnly() {
        AttendeeBadgeDTO a = new AttendeeBadgeDTO();
        a.setName("First", null);
        assertEquals("First", a.getName());
    }

    @Test
    public void setNameLastOnly() {
        AttendeeBadgeDTO a = new AttendeeBadgeDTO();
        a.setName(null, "Last");
        assertEquals("Last", a.getName());
    }

    @Test
    public void setNameSpacesGetTrimmed() {
        AttendeeBadgeDTO a = new AttendeeBadgeDTO();
        a.setName(" First ", " Last ");
        assertEquals("First Last", a.getName());
    }

    @Test
    public void setNameNullsChangedToEmptyString() {
        AttendeeBadgeDTO a = new AttendeeBadgeDTO();
        a.setName(null, null);
        assertEquals("", a.getName());
    }
}