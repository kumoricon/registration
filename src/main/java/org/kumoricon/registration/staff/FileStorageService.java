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

        BufferedImage image = decodeImageFromString(imageData);
        Path targetLocation = this.uploadPath.resolve(Instant.now().toEpochMilli() + "-" + fileName);
        File outputFile = targetLocation.toFile();
        ImageIO.write(image, "png", outputFile);
    }

    BufferedImage decodeImageFromString(String imageData) {
        if (imageData.startsWith("data:image/png;base64,")) {
            imageData = imageData.substring(22);
        } else if (imageData.startsWith("data:image/jpeg;base64,")) {
            imageData = imageData.substring(23);
        }
        byte[] imageByte = decoder.decode(imageData);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
            BufferedImage image = ImageIO.read(bis);
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't extract image", e);
        }
    }

    @PostConstruct
    public void createDirectories() {
        try {
            uploadPath = Files.createDirectories(Paths.get(uploadPathString));
            log.info("Impage upload path: " + uploadPath.toAbsolutePath().toString());
        } catch (IOException ex) {
            log.error("Error creating directory", ex);
        }
    }
}
