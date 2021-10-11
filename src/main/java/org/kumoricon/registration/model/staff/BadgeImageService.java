package org.kumoricon.registration.model.staff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BadgeImageService {
    private static final Logger log = LoggerFactory.getLogger(BadgeImageService.class);
    private final String badgeImagePathString;
    private final String badgeResourcePathString;
    private final String mascotFilename;
    private Path badgeImagePath;
    private Path badgeResourcePath;

    public BadgeImageService(@Value("${staffbadge.badgeimagepath}") String badgeImagePathString,
                             @Value("${staffbadge.badgeresourcepath}") String badgeResourcePath,
                             @Value("${staffbadge.mascotfilename}") String mascotFilename) {
        this.badgeImagePathString = badgeImagePathString;
        this.badgeResourcePathString = badgeResourcePath;
        this.mascotFilename = mascotFilename;
    }

    public Image getBadgeForUuid(String uuid, String fileType) {
        if (fileType != null) {
            Path filePath = Paths.get(badgeImagePath.toAbsolutePath().toString(), uuid + "." + fileType);
            try {
                return ImageIO.read(filePath.toFile());
            } catch (IOException e) {
                return getMascotImage();
            }
        } else {
            return getMascotImage();
        }
    }

    /**
     * Get the mascot image or fall back to a blank PNG if it can't be loaded
     * @return Mascot image
     */
    public Image getMascotImage() {
        Path filePath = Paths.get(badgeResourcePath.toAbsolutePath().toString(), mascotFilename);
        try {
            return ImageIO.read(filePath.toFile());
        } catch (IOException ex) {
            return new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }

    }

    @PostConstruct
    public void createDirectories() {
        try {
            badgeImagePath = Files.createDirectories(Paths.get(badgeImagePathString));
            badgeResourcePath = Files.createDirectories(Paths.get(badgeResourcePathString));
            log.info("Badge Image path: " + badgeImagePath.toAbsolutePath());
            log.info("Badge Resource path: " + badgeImagePath.toAbsolutePath());
            log.info("Mascot Image: " + mascotFilename);
        } catch (IOException ex) {
            log.error("Error creating directory", ex);
        }
    }
}
