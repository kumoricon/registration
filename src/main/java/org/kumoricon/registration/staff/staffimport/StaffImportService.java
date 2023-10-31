package org.kumoricon.registration.staff.staffimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kumoricon.registration.exceptions.NotFoundException;
import org.kumoricon.registration.model.ImportService;
import org.kumoricon.registration.model.badgenumber.BadgeNumberService;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StaffImportService extends ImportService {

    private final StaffRepository staffRepository;
    private final StaffImportUserCreateService staffUserCreate;
    private final BadgeNumberService badgeNumberService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public StaffImportService(@Value("${staff.onlineImportPath}") String importInputPath,
                              @Value("${staff.onlineImportGlob}") String importGlob,
                              @Value("${staff.onlineDLQPath}") String importDLQPath,
                              StaffRepository staffRepository,
                              StaffImportUserCreateService staffUserCreate,
                              BadgeNumberService badgeNumberService) {
        this.staffRepository = staffRepository;
        this.staffUserCreate = staffUserCreate;
        this.badgeNumberService = badgeNumberService;
        this.onlineImportInputPath = importInputPath;
        this.onlineImportGlob = importGlob;
        this.onlineDLQPath = importDLQPath;
    }

    protected void importFile(Path filepath) throws IOException {
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
    }

    private void importPerson(StaffImportFile.Person person) {
        log.info("Importing {}", person);
        Staff existing;
        try {
            existing = staffRepository.findByUuid(person.getId());
        } catch (NotFoundException ex) {
            existing = new Staff();
            existing.setCheckedIn(false);
            existing.setDeleted(false);
            existing.setBadgePrinted(false);
            existing.setBadgePrintCount(0);
            existing.setInformationVerified(false);
            existing.setPictureSaved(false);
            existing.setBadgeNumber(badgeNumberService.getNextBadgeNumber());
            existing.setAccessibilitySticker(false);
        }

        List<String> positions = new ArrayList<>();
        for (StaffImportFile.Position p : person.getPositions()) {
            positions.add(p.title);
        }
        if (staffRecordIsDifferent(existing, person, positions)) {
            updateStaffFromPerson(existing, person, positions);
            staffRepository.save(existing);
        }
    }

    private void updateStaffFromPerson(Staff staff, StaffImportFile.Person person, List<String> positions) {
        // TODO: Corner cases here!

        staff.setUuid(person.getId());
        staff.setFirstName(person.getNamePreferredFirst());
        staff.setLastName(person.getNamePreferredLast());
        staff.setLegalFirstName(person.getNameOnIdFirst());
        staff.setLegalLastName(person.getNameOnIdLast());
        staff.setPrivacyNameFirst(person.getNamePrivacyFirst());
        staff.setPrivacyNameLast(person.getNamePrivacyLast());
        staff.setPhoneNumber(person.getPhoneNumber());
        staff.setPreferredPronoun(person.getPreferredPronoun());
        staff.setBirthDate(LocalDate.parse(person.getBirthdate()));
        staff.setShirtSize(person.gettShirtSize());
        staff.setPositions(positions);
        staff.setAgeCategoryAtCon(person.getAgeCategoryConCurrentTerm());
        staff.setHasBadgeImage(person.getHasBadgeImage());
        staff.setBadgeImageFileType(person.getBadgeImageFileType());

        staffUserCreate.createUserFromStaff(person);

        if (person.getPositions().size() >0) {
            staff.setDepartment(person.getPositions().get(0).department);
            staff.setSuppressPrintingDepartment(person.getPositions().get(0).departmentSuppressed);
            staff.setDepartmentColorCode(findDepartmentColorCode(staff.getDepartment()));
        } else {
            log.warn("{} has no positions!", staff);
        }
    }

    private boolean staffRecordIsDifferent(Staff staff, StaffImportFile.Person person, List<String> positions) {
        return !Objects.equals(staff.getUuid(), person.getId()) ||
                !Objects.equals(staff.getFirstName(), person.getNamePreferredFirst()) ||
                !Objects.equals(staff.getLastName(), person.getNamePreferredLast()) ||
                !Objects.equals(staff.getLegalFirstName(), person.getNameOnIdFirst()) ||
                !Objects.equals(staff.getLegalLastName(), person.getNameOnIdLast()) ||
                !Objects.equals(staff.getPrivacyNameFirst(), person.getNamePrivacyFirst()) ||
                !Objects.equals(staff.getPrivacyNameLast(), person.getNamePrivacyLast()) ||
                !Objects.equals(staff.getPhoneNumber(), person.getPhoneNumber()) ||
                !Objects.equals(staff.getPreferredPronoun(), person.getPreferredPronoun()) ||
                !Objects.equals(staff.getBirthDate().toString(), person.getBirthdate()) ||
                !Objects.equals(staff.getShirtSize(), person.gettShirtSize()) ||
                !staff.getPositions().equals(positions) ||
                !Objects.equals(staff.getAgeCategoryAtCon(), person.getAgeCategoryConCurrentTerm()) ||
                !Objects.equals(staff.getHasBadgeImage(), person.getHasBadgeImage()) ||
                !Objects.equals(staff.getBadgeImageFileType(), person.getBadgeImageFileType());
    }

    /**
     * For a department name, lookup and return the HTML color code for their background
     * @param department Department name
     * @return String color code (ex: #FF00EC)
     */
    private String findDepartmentColorCode(String department) {
        String dept;
        if (department == null) {
            log.warn("Null department found");
            return "#FFFFFF";
        } else {
            dept = department.toLowerCase();
        }
        switch (dept) {
            case "treasury":
            case "department of the treasurer":
                return "#743029";
            case "secretarial":
            case "department of the secretary":
                return "#8EA8D7";
            case "relations":
                return "#F48DA5";
            case "publicity":
                return "#DDE0EB";
            case "programming":
                return "#854B76";
            case "operations":
                return "#447F65";
            case "membership":
                return "#F5994B";
            case "infrastructure":
                return "#2D233E";
            case "chair":
            case "department of the chair":
                return "#DDAD14";
            default:
                log.warn("Warning, couldn't find color code for " + department);
                return "#FFFFFF";
        }
    }
}
