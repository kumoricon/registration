package org.kumoricon.registration.model.staff;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
    private Font badgeFont;
    private Font nameFont;

    public BadgeResourceService(@Value("${staffbadge.badgeresourcepath}") String badgeResourcePathString,
                             @Value("${staffbadge.fontfilename}") String fontFilename) {
        this.badgeResourcePathString = badgeResourcePathString;
        this.fontFilename = fontFilename;
    }

    public BadgeResource getStaffBadgeResources() {
        return new BadgeResource(cloneDocument(staffBadgeBackground), badgeFont, nameFont);
    }

    public BadgeResource getGuestBadgeResource() {
        return new BadgeResource(cloneDocument(guestBadgeBackground), badgeFont, nameFont);
    }

    public BadgeResource getAttendeeBadgeResources() {
        return new BadgeResource(cloneDocument(attendeeBadgeBackground), badgeFont, nameFont);
    }

    public Image getAdultSeal() { return adultSeal; }
    public Image getYouthSeal() {
        return youthSeal;
    }
    public Image getChildSeal() {
        return childSeal;
    }
    public Font getBadgeFont() { return badgeFont; }
    public Font getNameFont() { return nameFont; }

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
            log.info("Badge Resource path: " + badgeResourcePath.toAbsolutePath().toString());
            adultSeal = loadImage("staffadult.png");
            youthSeal = loadImage("staffyouth.png");
            childSeal = loadImage("staffchild.png");
            staffBadgeBackground = loadStaffBackground("Print - Kumoricon-2019-Badge-Staff.pdf");
            guestBadgeBackground = loadStaffBackground("Print - Kumoricon-2019-Badge-Guest.pdf");
            attendeeBadgeBackground = loadBackground("Print - Kumoricon-2019-Badge-Attendee.pdf");
            nameFont = loadNameFont();
            badgeFont = loadBadgeFont();
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
     * @return
     */
    private PDDocument loadBackground(String filename) {
        Path filePath = Paths.get(badgeResourcePath.toAbsolutePath().toString(), filename);
        PDDocument background;
        try (InputStream stream = new FileInputStream(filePath.toFile())) {
            background = PDDocument.load(stream);
            log.info("Loaded PDF background {}", filename);
            return background;
        } catch (IOException ex) {
            background = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(612f, 396f));
            background.addPage(page);

            log.warn("Couldn't load PDF background {}, using blank document", filename);
        }
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
            background = new PDDocument();
            background.addPage(new PDPage(new PDRectangle(396f, 612f)));
            background.addPage(new PDPage(new PDRectangle(396f, 612f)));
            log.warn("Couldn't load PDF background {}, using blank document", filename);
        }
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
            badgeFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            log.warn("Couldn't load badge font {}, using default", fontPath);
            badgeFont = loadNameFont();
        }
        return badgeFont;
    }
    /**
     * Fallback font and font for fan name. Returns a font family to include as
     * many foreign/weird characters as possible
     */
    private Font loadNameFont() {
        return new Font("Dialog", Font.BOLD, 36);
    }
}


