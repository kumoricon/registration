package org.kumoricon.registration.export;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.kumoricon.registration.model.attendee.AttendeeDetailDTO;
import org.kumoricon.registration.model.attendee.AttendeeDetailRepository;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventReservationExportService {
    private static final Logger log = LoggerFactory.getLogger(EventReservationExportService.class);

    private final AttendeeDetailRepository attendeeDetailRepository;
    private final StaffRepository staffRepository;
    private final String exportPathString;
    private Path exportPath;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private OffsetDateTime highWaterMark;

    public EventReservationExportService(@Value("${eventReservation.exportPath}") String exportPathString,
                                         AttendeeDetailRepository attendeeDetailRepository,
                                         StaffRepository staffRepository) {
        this.attendeeDetailRepository = attendeeDetailRepository;
        this.staffRepository = staffRepository;
        this.exportPathString = exportPathString;
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        objectMapper.registerModule(new JavaTimeModule());
        highWaterMark = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    @Scheduled(fixedDelay = 30000)
    public void doExport() {
        if (exportPath != null) {
            List<EventRegistration> eventRegistrations = new ArrayList<>();
            for (AttendeeDetailDTO attendee : attendeeDetailRepository.findCheckedIn(highWaterMark)) {
                eventRegistrations.add(new EventRegistration(attendee.getFirstName(), attendee.getLastName(),
                        attendee.getBadgeNumber(), attendee.getPhoneNumber(), attendee.getBirthDate(),
                        attendee.getBadgeType(), attendee.getAccessibilitySticker(), attendee.getLastModified()));
            }

            for (Staff staff : staffRepository.findCheckedIn(highWaterMark)) {
                eventRegistrations.add(new EventRegistration(staff.getFirstName(), staff.getLastName(),
                        staff.getBadgeNumber(), staff.getPhoneNumber(), staff.getBirthDate(),
                        "Staff", staff.getAccessibilitySticker(), staff.getLastModified()));
            }

            if (eventRegistrations.size() > 0) {
                try {
                    Path output = exportPath.resolve(Path.of("export-" + System.currentTimeMillis() + ".json"));
                    Files.write(output, objectMapper.writeValueAsString(eventRegistrations).getBytes());
                    log.info("Exported {} record(s) to {}", eventRegistrations.size(), output.toAbsolutePath());

                    highWaterMark = OffsetDateTime.now();
                } catch (JsonProcessingException e) {
                    log.error("Error creating Event Reservation export JSON", e);
                } catch (IOException e) {
                    log.error("Error writing Event Reservation export JSON", e);
                }
            }
        }
    }

    @PostConstruct
    public void createDirectory() {
        try {
            if (exportPath != null) {
                exportPath = Files.createDirectories(Paths.get(exportPathString));
                log.info("{} export path: {}", this.getClass().getSimpleName(), exportPath.toAbsolutePath());
            } else {
                log.warn("eventReservation.exportPath not set, event reservation exports disabled");
            }

        } catch (IOException ex) {
            log.error("Error creating directory {}, event reservation exports disabled",
                    exportPath.toAbsolutePath(), ex);
        }
    }

    // TODO: workaround to serialize records, shouldn't be necessary after upgrading to Jackson 2.12.3
    // https://dev.to/brunooliveira/practical-java-16-using-jackson-to-serialize-records-4og4
    // Holding off on that because mass upgrading libraries 1.5 weeks before the event seems like a bad idea
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonSerialize
    record EventRegistration(String preferredFirstname, String preferredLastname, String badgeNumber,
                             String phone, LocalDate birthdate, String membershipType,
                             boolean accessibilitySticker, OffsetDateTime lastModified) {}
}
