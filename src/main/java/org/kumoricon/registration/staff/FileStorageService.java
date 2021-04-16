package org.kumoricon.registration.staff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;

@Service
public class FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    @Value("${staff.file.uploaddir}")
    private String uploadPathString;
    private final Base64.Decoder decoder = Base64.getDecoder();

    private Path uploadPath;

    public void storeFile(String fileName, String imageData) throws IOException {
        if(fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        String fileFormat = findImageFormat(imageData);
        BufferedImage image = decodeImageFromString(imageData);
        Path targetLocation = this.uploadPath.resolve(fileName + "_" + Instant.now().toEpochMilli() + "." + fileFormat);
        File outputFile = targetLocation.toFile();

        ImageIO.write(image, fileFormat, outputFile);
    }

    private String findImageFormat(String imageData) {
        if (imageData.startsWith("data:image/png;base64,")) {
            return "png";
        } else if (imageData.startsWith("data:image/jpeg;base64,")) {
            return "jpg";
        } else {
            throw new RuntimeException("Could not find file type, must be jpg or png");
        }
    }

    BufferedImage decodeImageFromString(String imageData) {
        int start = imageData.indexOf(";base64,") + 8;
        byte[] imageByte = decoder.decode(imageData.substring(start));
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
            return ImageIO.read(bis);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't extract image", e);
        }
    }

    @PostConstruct
    public void createDirectories() {
        try {
            uploadPath = Files.createDirectories(Paths.get(uploadPathString));
            log.info("Impage upload path: " + uploadPath.toAbsolutePath());
        } catch (IOException ex) {
            log.error("Error creating directory", ex);
        }
    }
}
