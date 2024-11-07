package org.kumoricon.registration.staff.staffimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kumoricon.registration.model.ImportService;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class StaffYearsOfServiceImportService extends ImportService {

    private final StaffRepository staffRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public StaffYearsOfServiceImportService(@Value("${staff.onlineImportPath}") String importInputPath,
                                            @Value("${staff.yearsImportGlob}") String importGlob,
                                            @Value("${staff.onlineDLQPath}") String importDLQPath,
                                            StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
        this.onlineImportInputPath = importInputPath;
        this.onlineImportGlob = importGlob;
        this.onlineDLQPath = importDLQPath;
    }

    protected void importFile(Path filepath) throws IOException {
        final StaffYearsOfServiceImportFile importFile = objectMapper.readValue(filepath.toFile(), StaffYearsOfServiceImportFile.class);
        log.info("{}: Staff Members Years of Service: {}", filepath, importFile.getStaffYearsList().size());

        for (StaffYearsOfServiceImportFile.StaffYearsOfService staffYearsOfService : importFile.getStaffYearsList()) {
            importYearsOfService(staffYearsOfService);
        }
    }

    private void importYearsOfService(StaffYearsOfServiceImportFile.StaffYearsOfService staffYearsOfService) {
        try {
            final Staff staff = staffRepository.findByUuid(staffYearsOfService.getUuid());
            log.info("Setting {} {}'s years of service to {}", staff.getFirstName(), staff.getLastName(), staffYearsOfService.getYearsOfService());
            staff.setYearsOfService(staffYearsOfService.getYearsOfService());
            staffRepository.save(staff);
        } catch (Exception ex) {
            log.error("Error importing {}", staffYearsOfService, ex);
        }
    }
}
