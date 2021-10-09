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
        Rectangle ageBackground = new Rectangle(112, 900, 1260, 207);
        b.fillRect(ageBackground, bgColor);
        Rectangle ageTextBoundingBox;
        ageTextBoundingBox = new Rectangle(130, 870, 1160, 200);
        b.drawStretchedCenteredString(BadgeImage.buildTitleString(attendee.getAgeStripeText()), ageTextBoundingBox, boldFont, fgColor);
    }

    private void drawLargeName(BadgeImage b, AttendeeBadgeDTO attendee) {
        // If fan name exists, draw it here. Otherwise draw the full name here
        String name;
        Rectangle largeNameBg;
        if (attendee.getFanName() != null && !attendee.getFanName().trim().equals("")) {
            largeNameBg = new Rectangle(490, 450, 640, 110);
            name = attendee.getFanName();
        } else {
            // Draw regular name in the combined name and fan name spaces
            largeNameBg = new Rectangle(490, 450, 640, 200);
            name = attendee.getName();
        }
//         b.fillRect(largeNameBg, Color.ORANGE);
        b.drawStretchedLeftAlignedString(name, largeNameBg, boldFont, Color.BLACK, 0);
    }

    private void drawSmallName(BadgeImage b, AttendeeBadgeDTO attendee) {
        // If Fan Name exists, draw the full name here (on the second line)
        String fanName = attendee.getFanName();
        if (fanName != null && !attendee.getFanName().trim().isEmpty()) {
            if (attendee.getName() != null && !attendee.getName().isEmpty()) {
                Rectangle nameBg = new Rectangle(500, 535, 630, 65);
//                 b.fillRect(nameBg, Color.ORANGE);
                b.drawStretchedCenteredString(attendee.getName(), nameBg, plainFont, Color.BLACK, 0);
            }
        }
    }

    private void drawBadgeTypeStripe(BadgeImage b, AttendeeBadgeDTO attendee) {
        if (attendee != null) {
            Color bgColor = Color.decode(attendee.getBadgeTypeBackgroundColor());
            Color fgColor = BadgeImage.getInverseColor(bgColor);
            Rectangle badgeType = new Rectangle(1200, 115, 180, 785);
            b.fillRect(badgeType, bgColor);

            Rectangle textBoundingBox = new Rectangle(1200, 160, 160, 680);
            b.drawVerticalCenteredString(attendee.getBadgeTypeText(), textBoundingBox, boldFont, fgColor, 0);
        }
    }

    private void drawBadgeNumber(BadgeImage b, AttendeeBadgeDTO attendee) {
        String badgeNumber = attendee.getBadgeNumber();
        Color bgColor = Color.decode(attendee.getAgeStripeBackgroundColor());
        Color fgColor = BadgeImage.getInverseColor(bgColor);
        Rectangle badgeNumberBounds = new Rectangle(1160, 900, 170, 160);

        String[] lines;
        if (badgeNumber.length() == 8) {
            lines = new String[] {badgeNumber.substring(0, 3), badgeNumber.substring(3)};
            b.drawCenteredStrings(lines, badgeNumberBounds, boldFont, fgColor);
        } else {
            b.drawStretchedCenteredString(badgeNumber, badgeNumberBounds, plainFont, fgColor);
        }
    }

    private void drawPronoun(BadgeImage b, AttendeeBadgeDTO attendee) {
        String pronoun = attendee.getPronoun();

        if (pronoun != null) {
            Color fgColor = Color.BLACK;
            Rectangle textBoundingBox = new Rectangle(510, 570, 580, 145);
//            b.fillRect(textBoundingBox, Color.ORANGE);
            Font scaledFont = b.scaleFontByHeight(16f, textBoundingBox, plainFont);
            b.drawRightAlignedString(pronoun, textBoundingBox, scaledFont, fgColor, 0);
        }
    }
}
