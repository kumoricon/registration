package org.kumoricon.registration.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;

public abstract class ImportService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected String onlineImportInputPath;
    protected String onlineImportGlob;
    protected String onlineDLQPath;
    protected Path inputPath;
    protected Path dlqPath;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 10000)
    public void doWork() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputPath, this.onlineImportGlob)) {
            for (Path entry : stream) {
                long start = System.currentTimeMillis();
                try {
                    importFile(entry);
                    Files.delete(entry);
                } catch (Exception ex) {
                    log.error("Error processing file {}", entry, ex);
                    try {
                        Path dest = Paths.get(dlqPath.toString(), entry.getFileName().toString());
                        Files.move(entry, dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        log.error("Error moving {} to DLQ {}", entry, dlqPath, e);
                    }
                    throw ex;
                }
                long finish = System.currentTimeMillis();
                log.info("Imported {} in {} ms", entry, finish-start);
            }
        } catch (IOException ex) {
            log.error("Error reading files", ex);
        }
    }

    protected void importFile(Path entry) throws IOException {
        throw new RuntimeException("Not implemented, this class must be extended");
    }

    @PostConstruct
    public void createDirectories() {
        try {
            inputPath = Files.createDirectories(Paths.get(onlineImportInputPath));
            dlqPath = Files.createDirectories(Paths.get(onlineDLQPath));
            log.info("{} monitoring input path: {}", this.getClass().getSimpleName(), inputPath.toAbsolutePath());
            log.info("{} DLQ path: {}", this.getClass().getSimpleName(), dlqPath.toAbsolutePath());
        } catch (IOException ex) {
            log.error("Error creating directory", ex);
        }
    }
}
