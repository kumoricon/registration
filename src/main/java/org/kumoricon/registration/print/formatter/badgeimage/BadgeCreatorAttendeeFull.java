package org.kumoricon.registration.print.formatter.badgeimage;

import java.awt.*;

public class BadgeCreatorAttendeeFull implements BadgeCreatorAttendee {
    private static final int DPI = 300;
    private static final int BADGE_WIDTH = 5*DPI;
    private static final int BADGE_HEIGHT = 4*DPI;
    private final Font boldFont;
    private final Font plainFont;

    public BadgeCreatorAttendeeFull(Font boldFont, Font plainFont) {
        this.boldFont = boldFont;
        this.plainFont = plainFont;
    }

    @Override
    public byte[] createBadge(AttendeeBadgeDTO attendee) {
        BadgeImage b = new BadgeImage(BADGE_WIDTH, BADGE_HEIGHT, DPI);

        drawBadgeTypeStripe(b, attendee);
        drawAgeColorStripe(b, attendee);
        drawLargeName(b, attendee);
        drawSmallName(b, attendee);
        drawBadgeNumber(b, attendee);
        drawPronoun(b, attendee);

        return b.writePNGToByteArray();
    }

    private void drawAgeColorStripe(BadgeImage b, AttendeeBadgeDTO attendee) {
        Color bgColor = Color.decode(attendee.getAgeStripeBackgroundColor());
        Color fgColor = BadgeImage.getInverseColor(bgColor);
        Rectangle ageBackground = new Rectangle(1096, 0, 190, 976);
        Rectangle textBoundingBox = new Rectangle(1070, 150, 160, 680);
        b.fillRect(ageBackground, bgColor);
        b.drawVerticalCenteredString(attendee.getAgeStripeText(), textBoundingBox, boldFont, fgColor, 2);
    }

    private void drawLargeName(BadgeImage b, AttendeeBadgeDTO attendee) {
        // If fan name exists, draw it here. Otherwise, draw the full name here
        String name;
        Rectangle largeNameBg;
        if (attendee.getFanName() != null && !attendee.getFanName().trim().equals("")) {
            largeNameBg = new Rectangle(260, 390, 760, 150);
            name = attendee.getFanName();
        } else {
            // Draw regular name in the combined name and fan name spaces
            largeNameBg = new Rectangle(260, 450, 760, 175);
            name = attendee.getName();
        }
        // b.fillRect(largeNameBg, Color.ORANGE);
        b.drawStretchedCenteredString(name, largeNameBg, plainFont, Color.BLACK, 0);
    }

    private void drawSmallName(BadgeImage b, AttendeeBadgeDTO attendee) {
        // If Fan Name exists, draw the full name here (on the second line)
        String fanName = attendee.getFanName();
        if (fanName != null && !attendee.getFanName().trim().isEmpty()) {
            if (attendee.getName() != null && !attendee.getName().isEmpty()) {
                Rectangle nameBg = new Rectangle(260, 550, 760, 105);
//                 b.fillRect(nameBg, Color.ORANGE);
                b.drawStretchedCenteredString(attendee.getName(), nameBg, plainFont, Color.BLACK, 0);
            }
        }
    }

    private void drawBadgeTypeStripe(BadgeImage b, AttendeeBadgeDTO attendee) {
        if (attendee != null) {
            Color bgColor = Color.decode(attendee.getBadgeTypeBackgroundColor());
            Color fgColor = BadgeImage.getInverseColor(bgColor);
            Rectangle background = new Rectangle(12, 0, 187, 975);
            Rectangle textBoundingBox = new Rectangle(40, 80, 160, 830);
            b.fillRect(background, bgColor);
            b.drawVerticalCenteredString(attendee.getBadgeTypeText(), textBoundingBox, boldFont, fgColor, 2);
        }
    }

    private void drawBadgeNumber(BadgeImage b, AttendeeBadgeDTO attendee) {
        String badgeNumber = attendee.getBadgeNumber();

        // Color bgColor = Color.decode(attendee.getAgeStripeBackgroundColor());
        // Color fgColor = BadgeImage.getInverseColor(bgColor);
        Rectangle badgeNumberBounds = new Rectangle(192, 210, 160, 110);
//        b.fillRect(badgeNumberBounds, Color.GREEN);
        b.drawStretchedCenteredString(badgeNumber, badgeNumberBounds, plainFont, Color.BLACK, 0);
    }

    private void drawPronoun(BadgeImage b, AttendeeBadgeDTO attendee) {
        String pronoun = attendee.getPronoun();

        if (pronoun != null) {
            Color fgColor = Color.BLACK;
            Rectangle textBoundingBox = new Rectangle(780, 695, 280, 100);
//            b.fillRect(textBoundingBox, Color.ORANGE);
            Font scaledFont = b.scaleFontByHeight(18f, textBoundingBox, plainFont);
            b.drawStretchedCenteredString(pronoun, textBoundingBox, scaledFont, fgColor, 0);
        }
    }
}
