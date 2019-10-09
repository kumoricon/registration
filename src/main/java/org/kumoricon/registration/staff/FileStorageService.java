package org.kumoricon.registration.staff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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

        String[] parts = imageData.split(",");
        String imageString = parts[1];

        BufferedImage image = null;
        byte[] imageByte;

        imageByte = decoder.decode(imageString);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
            image = ImageIO.read(bis);
            Path targetLocation = this.uploadPath.resolve(Instant.now().toEpochMilli() + "-" + fileName);

            File outputFile = targetLocation.toFile();

            ImageIO.write(image, "png", outputFile);
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
