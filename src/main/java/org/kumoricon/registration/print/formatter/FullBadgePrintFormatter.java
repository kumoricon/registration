package org.kumoricon.registration.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.kumoricon.registration.model.staff.BadgeResource;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreatorAttendee;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreatorAttendeeFull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class FullBadgePrintFormatter implements BadgePrintFormatter {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    private final BadgeCreatorAttendee badgeCreator;
    private final BadgeResource badgeResource;
    private static final Logger LOGGER = LoggerFactory.getLogger(FullBadgePrintFormatter.class);
    private Integer xOffset = 0;
    private Integer yOffset = 0;

    /**
     * Generates a PDF containing badges ready to be printed.
     * @param attendees Attendees to print badges for
     */
    public FullBadgePrintFormatter(List<AttendeeBadgeDTO> attendees, BadgeResource badgeResource) {
        this(attendees, 0, 0, badgeResource);
    }

    /**
     * Generates a PDF containing badges ready to be printed.
     * @param attendees Attendees to generate badges for
     * @param xOffset Horizontal offset in points (1/72 inch)
     * @param yOffset Vertical offset in points (1/72 inch)
     */
    public FullBadgePrintFormatter(List<AttendeeBadgeDTO> attendees, Integer xOffset, Integer yOffset, BadgeResource badgeResource) {
        PDDocument document;
        this.xOffset = (xOffset == null) ? 0 : xOffset;
        this.yOffset = (yOffset == null) ? 0 : yOffset;
        this.badgeResource = badgeResource;
        this.badgeCreator = new BadgeCreatorAttendeeFull(badgeResource.getBadgeFont(), badgeResource.getNameFont());
        try {
            document = new PDDocument();
            for (AttendeeBadgeDTO attendee : attendees) {
                generatePage(attendee, document);
            }

            document.save(os);
            document.close();
            badgeResource.getBackground().close();

        } catch (IOException e) {
            LOGGER.error("Error creating badge", e);
            throw new RuntimeException(e);
        }

    }


    private PDPage generatePage(AttendeeBadgeDTO attendee, PDDocument document) throws IOException {
        PDPage page = document.importPage(badgeResource.getBackground().getPage(0));

        byte[] badgeImage = badgeCreator.createBadge(attendee);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        PDImageXObject pdi = PDImageXObject.createFromByteArray(document, badgeImage, attendee.getId() + ".png");
        contentStream.drawImage(pdi,126+xOffset,54+yOffset, 360, 288);
        contentStream.close();

        return page;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(os.toByteArray());
    }
}
