package org.kumoricon.registration.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.kumoricon.registration.model.staff.BadgeResource;
import org.kumoricon.registration.model.staff.StaffBadgeDTO;
import org.kumoricon.registration.print.Sides;
import org.kumoricon.registration.print.formatter.badgeimage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class StaffBadgePrintFormatter implements BadgePrintFormatter {
    private final BadgeCreatorStaffFront badgeCreator;
    private final BadgeCreatorStaffBack badgeCreatorBack;
    private final List<StaffBadgeDTO> staffList;
    private final Sides sides;
    private final PDDocument background;
    private static final Logger LOGGER = LoggerFactory.getLogger(StaffBadgePrintFormatter.class);
    private Integer xOffset;
    private Integer yOffset;

    /**
     * Generates a PDF containing badges ready to be printed.
     * @param staffList Staff to generate badges for
     * @param xOffset Horizontal offset in points (1/72 inch)
     * @param yOffset Vertical offset in points (1/72 inch)
     */
    public StaffBadgePrintFormatter(List<StaffBadgeDTO> staffList, Sides sides, Integer xOffset, Integer yOffset, BadgeResource badgeResource) {
        this.xOffset = (xOffset == null) ? 0 : xOffset;
        this.yOffset = (yOffset == null) ? 0 : yOffset;
        this.staffList = staffList;
        this.sides = sides;
        this.background = badgeResource.getBackground();
        badgeCreator = new BadgeCreatorStaffFront(badgeResource.getBadgeFont(), badgeResource.getNameFont());
        badgeCreatorBack = new BadgeCreatorStaffBack(badgeResource.getBadgeFont(), badgeResource.getNameFont());
    }


    private PDPage generatePageFront(StaffBadgeDTO staffBadgeDTO, PDDocument document) throws IOException {
        PDPage page = linkBackgroundInto(background, 0, document);

        byte[] badgeImage = badgeCreator.createBadge(staffBadgeDTO);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        PDImageXObject pdi = PDImageXObject.createFromByteArray(document, badgeImage, staffBadgeDTO.getFirstName() + ".png");
        contentStream.drawImage(pdi,45+xOffset, 81+yOffset, 306, 450);
        contentStream.close();

        return page;
    }


    private PDPage generatePageBack(StaffBadgeDTO staffBadgeDTO, PDDocument document) throws IOException {
        PDPage page = linkBackgroundInto(background, 1, document);
        byte[] badgeImage = badgeCreatorBack.createBadge(staffBadgeDTO);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        PDImageXObject pdi = PDImageXObject.createFromByteArray(document, badgeImage, staffBadgeDTO.getLastName() + ".png");
        contentStream.drawImage(pdi,45+xOffset, 81+yOffset, 306, 450);
        contentStream.close();

        return page;
    }

    private static PDPage linkBackgroundInto(PDDocument source, int pageNumber, PDDocument dest) throws IOException{
        if (source.getNumberOfPages() < pageNumber) {
            throw new RuntimeException("Tried to clone page that doesn't exist. " + source.getNumberOfPages() + " exist");
        }
        PDPage backgroundPage = source.getPage(pageNumber);
        return dest.importPage(backgroundPage);
    }

    @Override
    public InputStream getStream() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PDDocument document;
        try {
            document = new PDDocument();
            for (StaffBadgeDTO staffBadgeDTO : staffList) {
                if (sides.equals(Sides.FRONT) || sides.equals(Sides.BOTH)) {
                    generatePageFront(staffBadgeDTO, document);
                }
                if (sides.equals(Sides.BACK) || sides.equals(Sides.BOTH)) {
                    generatePageBack(staffBadgeDTO, document);
                }
            }

            document.save(os);
            document.close();
            background.close();
        } catch (IOException e) {
            LOGGER.error("Error creating badge", e);
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(os.toByteArray());
    }
}
