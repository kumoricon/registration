package org.kumoricon.registration.utility;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttendeeImporterServiceTest {

    @Test
    public void normalizePronoun() {
        // Valid pronouns can be found in Attendee.PRONOUNS
        String[] data = {null, "", " ", "she/her", "He/Him", "She/Her", "he/they", "She/they", "They/Them", "Ask Me My Pronouns"};
        String[] expected = {null, null, null, "She/Her", "He/Him", "She/Her", "He/They", "She/They", "They/Them", "Ask Me My Pronouns"};

        for (int i = 0; i < data.length; i++) {
            assertEquals(expected[i], AttendeeImporterService.normalizePronoun(data[i]));
        }
    }
}