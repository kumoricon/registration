package org.kumoricon.registration.model.staff;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.kumoricon.registration.model.badge.BadgeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class BadgeResourceService {
    private static final Logger log = LoggerFactory.getLogger(BadgeResourceService.class);
    private final String badgeResourcePathString;
    private final String fontFilename;
    private Path badgeResourcePath;

    private Image adultSeal;
    private Image youthSeal;
    private Image childSeal;
    private PDDocument staffBadgeBackground;
    private PDDocument guestBadgeBackground;
    private PDDocument attendeeBadgeBackground;
    private PDDocument specialtyBadgeBackground;
    private PDDocument vipBadgeBackground;
    private Font boldFont;
    private Font plainFont;

    public BadgeResourceService(@Value("${staffbadge.badgeresourcepath}") String badgeResourcePathString,
                                @Value("${staffbadge.fontfilename}") String fontFilename) {
        this.badgeResourcePathString = badgeResourcePathString;
        this.fontFilename = fontFilename;
    }

    public BadgeResource getBadgeResourceFor(BadgeType badgeType) {
        Boolean withAttendeeBackground = true;
        return getBadgeResourceForWithBackground(badgeType, withAttendeeBackground);
    }

    public BadgeResource getBadgeResourceForWithBackground(BadgeType badgeType, Boolean withAttendeeBackground) {
        switch (badgeType) {
            case ATTENDEE:
                return withAttendeeBackground ? getAttendeeBadgeResources() : getBlankAttendeeBadgeResources();
            case VIP:
                return withAttendeeBackground ? getVipBadgeResources() : getBlankAttendeeBadgeResources();
            case SPECIALTY :
                return withAttendeeBackground ? getSpecialtyBadgeResources() : getBlankAttendeeBadgeResources();
            case STAFF:
                return getStaffBadgeResources();
            case GUEST:
                return getGuestBadgeResource();
            default:
                log.warn("Tried to get badge resources for type {} that is not supported in BadgeResourceService", badgeType);
                return getAttendeeBadgeResources();
        }
    }

    public BadgeResource getStaffBadgeResources() {
        return new BadgeResource(cloneDocument(staffBadgeBackground), boldFont, plainFont);
    }

    public BadgeResource getGuestBadgeResource() {
        return new BadgeResource(cloneDocument(guestBadgeBackground), boldFont, plainFont);
    }

    public BadgeResource getAttendeeBadgeResources() {
        return new BadgeResource(cloneDocument(attendeeBadgeBackground), boldFont, plainFont);
    }

    private BadgeResource getSpecialtyBadgeResources() {
        return new BadgeResource(cloneDocument(specialtyBadgeBackground), boldFont, plainFont);
    }

    private BadgeResource getVipBadgeResources() {
        return new BadgeResource(cloneDocument(vipBadgeBackground), boldFont, plainFont);
    }

    private BadgeResource getBlankAttendeeBadgeResources() {
        return new BadgeResource(cloneDocument(buildBlankAttendeeBadge()), boldFont, plainFont);
    }

    public Image getAdultSeal() { return adultSeal; }
    public Image getYouthSeal() {
        return youthSeal;
    }
    public Image getChildSeal() {
        return childSeal;
    }
    public Font getBoldFont() { return boldFont; }
    public Font getPlainFont() { return plainFont; }

    private PDDocument cloneDocument(PDDocument document) {
        try {
            PDDocument output = new PDDocument();
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage backgroundPage = document.getPage(i);
                COSBase pageBase = new PDFCloneUtility(output).cloneForNewDocument(backgroundPage.getCOSObject());
                PDPage imported = new PDPage(new COSDictionary((COSDictionary) pageBase));
                output.importPage(imported);
            }
            return output;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @PostConstruct
    public void createDirectoriesAndLoadData() {
        try {
            badgeResourcePath = Files.createDirectories(Paths.get(badgeResourcePathString));
            log.info("Badge Resource path: " + badgeResourcePath.toAbsolutePath());
            adultSeal = loadImage("staffadult.png");
            youthSeal = loadImage("staffyouth.png");
            childSeal = loadImage("staffchild.png");
            staffBadgeBackground = loadStaffBackground("2022 Staff Badge.pdf");
            guestBadgeBackground = loadStaffBackground("2022 Guest of Honor Badge.pdf");
            attendeeBadgeBackground = loadBackground("2022 Attendee Badges.pdf");
            specialtyBadgeBackground = loadBackground("2022 Specialty Badges.pdf");
            vipBadgeBackground = loadBackground("2022 VIP Badges.pdf");
            plainFont = loadPlainFont();
            boldFont = loadBadgeFont();
        } catch (IOException ex) {
            log.error("Error creating resource directory", ex);
        }
    }

    @PreDestroy
    public void destroy() {
        closeDocument(staffBadgeBackground);
        closeDocument(guestBadgeBackground);
    }

    private Image loadImage(String filename) {
        Path filePath = Paths.get(badgeResourcePath.toAbsolutePath().toString(), filename);
        try {
            return ImageIO.read(filePath.toFile());
        } catch (IOException e) {
            log.error("Error reading file {}, using blank image", filePath, e);
            return new BufferedImage(100, 100,BufferedImage.TYPE_INT_ARGB);
        }
    }

    /**
     * Load a PDF for background of Attendee badges. If it can't be loaded, return a document
     * with a single page, rotated to landscape
     * @param filename Filename to load, without path
     * @return PDF document
     */
    private PDDocument loadBackground(String filename) {
        Path filePath = Paths.get(badgeResourcePath.toAbsolutePath().toString(), filename);
        PDDocument background;
        try (InputStream stream = new FileInputStream(filePath.toFile())) {
            background = PDDocument.load(stream);
            log.info("Loaded PDF background {}", filename);
            return background;
        } catch (IOException ex) {
            log.warn("Couldn't load PDF background {}, using blank document", filename);
            background = buildBlankAttendeeBadge();
        }
        return background;
    }

    private PDDocument buildBlankAttendeeBadge() {
        PDDocument background = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(612f, 396f));
        background.addPage(page);
        return background;
    }

    /**
     * Open an existing PDF from the resource path. If it can't be loaded, return a document with two
     * blank pages (Staff badges are double sided)
     * @param filename Filename to load, without path
     * @return PDDocument
     */
    private PDDocument loadStaffBackground(String filename) {
        Path filePath = Paths.get(badgeResourcePath.toAbsolutePath().toString(), filename);
        PDDocument background;
        try (InputStream stream = new FileInputStream(filePath.toFile())) {
            background = PDDocument.load(stream);
            log.info("Loaded PDF background {}", filename);
            return background;
        } catch (IOException ex) {
            log.warn("Couldn't load PDF background {}, using blank document", filename);
            background = buildBlankStaffBadge();
        }
        return background;
    }

    private PDDocument buildBlankStaffBadge() {
        PDDocument background = new PDDocument();
        background.addPage(new PDPage(new PDRectangle(396f, 612f)));
        background.addPage(new PDPage(new PDRectangle(396f, 612f)));
        return background;
    }


    private void closeDocument(PDDocument pdDocument) {
        if (pdDocument != null) {
            try {
                pdDocument.close();
            } catch (IOException ex) {
                log.error("Error closing pdDocument {}", pdDocument, ex);
            }
        }
    }

    private Font loadBadgeFont() {
        Path fontPath = Paths.get(badgeResourcePath.toAbsolutePath().toString(),fontFilename);
        try (InputStream stream = new FileInputStream(fontPath.toFile())) {
            boldFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            log.warn("Couldn't load badge font {}, using default", fontPath);
            boldFont = loadBoldFont();
        }
        return boldFont;
    }

    private Font loadBoldFont() {
        return new Font("Dialog", Font.BOLD, 36);
    }

    /**
     * Fallback font and font for fan name. Returns a font family to include as
     * many foreign/weird characters as possible
     */
    private Font loadPlainFont() {
        return new Font("Dialog", Font.PLAIN, 36);
    }
}


