package org.kumoricon.registration.staff.staffimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StaffImportService {
    private static final Logger log = LoggerFactory.getLogger(StaffImportService.class);

    @Value("${staff.onlineinputpath}")
    private String onlineImportInputPath;

    @Value("${staff.onlinedlqpath}")
    private String onlineDLQPath;

    private Path inputPath;
    private Path dlqPath;

    private final StaffRepository staffRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StaffImportService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void doWork() {
//        log.info("Reading files from " + onlineImportInputPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputPath, "*.{json}")) {
            for (Path entry : stream) {
                long start = System.currentTimeMillis();
                importFile(entry);
                Files.delete(entry);
                long finish = System.currentTimeMillis();
                log.info("Imported {} in {} ms", entry, finish-start);
            }
        } catch (IOException ex) {
            log.error("Error reading files", ex);
        }
    }


    private void importFile(Path filepath) {
        try {
            StaffImportFile importFile = objectMapper.readValue(filepath.toFile(), StaffImportFile.class);
            log.info("{}: Actions: {}   Persons: {}", filepath, importFile.getActions().size(), importFile.getPersons().size());

            for (StaffImportFile.Person person : importFile.getPersons()) {
                try {
                    importPerson(person);
                } catch (Exception ex) {
                    log.error("Error importing {}", person, ex);
                }
            }

            for (StaffImportFile.Action action : importFile.getActions()) {
                log.info("  Action: {}: {}", action.getActionsVersion(), action.getDeleted());
            }
        } catch (IOException ex) {
            log.error("Error loading {}", filepath.toString(), ex);
            try {
                Path dest = Paths.get(dlqPath.toString(), filepath.getFileName().toString());
                Files.move(filepath, dest);
            } catch (IOException e) {
                log.error("Error moving {} to DLQ {}", filepath, dlqPath, e);
            }
        }
    }

    private void importPerson(StaffImportFile.Person person) {

        log.info("Importing {}", person);
        Staff existing = staffRepository.findByUuid(person.getId());
        if (existing == null) {
            existing = new Staff();
            existing.setCheckedIn(false);
            existing.setDeleted(false);
            existing.setBadgePrinted(false);
            existing.setBadgePrintCount(0);
            existing.setInformationVerified(false);
            existing.setPictureSaved(false);
            existing.setSignatureSaved(false);
        }

        updateStaffFromPerson(existing, person);
        staffRepository.save(existing);
    }

    private void updateStaffFromPerson(Staff staff, StaffImportFile.Person person) {
        // TOOD: Corner cases here!
        List<String> positions = new ArrayList<>();
        for (StaffImportFile.Position p : person.getPositions()) {
            positions.add(p.title);
        }

        if (
                !Objects.equals(staff.getUuid(), person.getId()) ||
                !Objects.equals(staff.getFirstName(), person.getNamePreferredFirst()) ||
                !Objects.equals(staff.getLastName(), person.getNamePreferredLast()) ||
                !Objects.equals(staff.getLegalFirstName(), person.getNameOnIdFirst()) ||
                !Objects.equals(staff.getLegalLastName(), person.getNameOnIdLast()) ||
                !Objects.equals(staff.getPreferredPronoun(), person.getPreferredPronoun()) ||
                !Objects.equals(staff.getBirthDate().toString(), person.getBirthdate()) ||
                !Objects.equals(staff.getShirtSize(), person.gettShirtSize()) ||
                !staff.getPositions().equals(positions) ||
                !Objects.equals(staff.getAgeCategoryAtCon(), person.getAgeCategoryConCurrentTerm()) ||
                !Objects.equals(staff.getHasBadgeImage(), person.getHasBadgeImage()) ||
                !Objects.equals(staff.getBadgeImageFileType(), person.getBadgeImageFileType())
        ) {
            staff.setLastModifiedMS(Instant.now().toEpochMilli());
        }
        staff.setUuid(person.getId());
        staff.setFirstName(person.getNamePreferredFirst());
        staff.setLastName(person.getNamePreferredLast());
        staff.setLegalFirstName(person.getNameOnIdFirst());
        staff.setLegalLastName(person.getNameOnIdLast());
        staff.setPreferredPronoun(person.getPreferredPronoun());
        staff.setBirthDate(LocalDate.parse(person.getBirthdate()));
        staff.setShirtSize(person.gettShirtSize());
        staff.setPositions(positions);
        staff.setAgeCategoryAtCon(person.getAgeCategoryConCurrentTerm());
        staff.setPositions(positions);
        staff.setHasBadgeImage(person.getHasBadgeImage());
        staff.setBadgeImageFileType(person.getBadgeImageFileType());

        if (person.getPositions().size() >0) {
            staff.setDepartment(person.getPositions().get(0).department);
            staff.setSuppressPrintingDepartment(person.getPositions().get(0).departmentSuppressed);
            staff.setDepartmentColorCode(findDepartmentColorCode(staff.getDepartment()));
        } else {
            log.warn("{} has no positions!", staff);
        }
    }

    /**
     * For a department name, lookup and return the HTML color code for their background
     * @param department Department name
     * @return String color code (ex: #FF00EC)
     */
    private static String findDepartmentColorCode(String department) {
        String dept;
        if (department == null) {
            log.warn("Null department found");
            return "#FFFFFF";
        } else {
            dept = department.toLowerCase();
        }
        switch (dept) {
            case "treasury":
                return "#0a8141";
            case "department of the treasurer":
                return "#0a8141";
            case "secretarial":
                return "#3a53a5";
            case "department of the secretary":
                return "#3953a4"; // Not sure which color code is correct, this is from 2016

//            return "#3a53a5";
            case "relations":
                return "#f282b4";
            case "publicity":
                return "#e0e0e0";
            case "programming":
                return "#6b52a2";
            case "operations":
                return "#ec2426";
            case "membership":
                return "#f57f20";
            case "infrastructure":
                return "#414242";
            case "chair":
                return "#f99f1d";
            case "department of the chair":
                return "#f99f1d";
            default:
                log.warn("Warning, couldn't find color code for " + department);
                return "#FFFFFF";
        }
    }

    @PostConstruct
    public void createDirectories() {
        try {
            inputPath = Files.createDirectories(Paths.get(onlineImportInputPath));
            dlqPath = Files.createDirectories(Paths.get(onlineDLQPath));
            log.info("Monitoring input path: " + inputPath.toAbsolutePath().toString());
            log.info("DLQ path: " + dlqPath.toAbsolutePath().toString());
        } catch (IOException ex) {
            log.error("Error creating directory", ex);
        }
    }
}
