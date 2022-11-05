package org.kumoricon.registration.guest;

import org.kumoricon.registration.model.ImportService;
import org.kumoricon.registration.model.badgenumber.BadgeNumberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Handles importing non-staff members (attendees and guests). Attendees are handed off
 * to the AttendeeImporterService
 */
@Component
public class AttendeeImportService extends ImportService {

    private final GuestRepository guestRepository;
    private final AttendeeImporterService attendeeImporterService;
    private final BadgeNumberService badgeNumberService;

    public AttendeeImportService(@Value("${registration.attendeeImportPath}") String onlineImportInputPath,
                                 @Value("${registration.attendeeImportGlob}") String importGlob,
                                 @Value("${registration.onlineDLQPath}") String onlineDLQPath,
                                 GuestRepository guestRepository,
                                 AttendeeImporterService attendeeImporterService,
                                 BadgeNumberService badgeNumberService) {
        this.onlineImportInputPath = onlineImportInputPath;
        this.onlineDLQPath = onlineDLQPath;
        this.onlineImportGlob = importGlob;
        this.guestRepository = guestRepository;
        this.attendeeImporterService = attendeeImporterService;
        this.badgeNumberService = badgeNumberService;
    }

    protected void importFile(Path filepath) {
        AttendeeImportFile importFile;
        try {
            importFile = objectMapper.readValue(filepath.toFile(), AttendeeImportFile.class);
        } catch (IOException ex) {
            log.error("Error reading {}", filepath, ex);
            return;
        }
        log.info("{}: Actions: {}   Persons: {}", filepath, importFile.getActions().size(), importFile.getPersons().size());

        // Guests are imported in to both the guests table (for printing) and the
        // attendees table. One side effect of this is that the badge numbers are different
        // between the two tables. To fix this, run the SQL:
        //      UPDATE attendees SET badge_number = guests.badge_number
        //      FROM guests WHERE guests.online_id = attendees.website_id;
        // guestBadgeNumbers is a map of the badge numbers in the guests table,
        // and it's used later to set the same badge number in the attendees table.
        // This is pretty hacky.
        // TODO: Refactor all this in to a single "members" table
        Map<String, String> guestBadgeNumbers = new HashMap<>();
        List<AttendeeImportFile.Person> attendees = new ArrayList<>();
        for (AttendeeImportFile.Person person : importFile.getPersons()) {
            try {
                if ("guest".equalsIgnoreCase(person.membershipType())) {
                    Guest guest = importPerson(person);
                    if (guest != null) {
                        guestBadgeNumbers.put(guest.getOnlineId(), guest.getBadgeNumber());
                    }
                }
                attendees.add(person);
            } catch (Exception ex) {
                log.error("Error importing {}", person, ex);
                throw new RuntimeException(ex);
            }
        }

        attendeeImporterService.importFromObjects(attendees, guestBadgeNumbers);
    }

    private Guest importPerson(AttendeeImportFile.Person person) {
        if (person.isCanceled()) {
            log.info("{} was canceled online, deleting from system", person);
            guestRepository.deleteByOnlineId(person.id());
            return null;
        }
        log.info("Importing {}", person);
        Guest existing;
        try {
            existing = guestRepository.findByOnlineId(person.id());
        } catch (EmptyResultDataAccessException ex) {
            existing = new Guest();
            existing.setBadgeNumber(badgeNumberService.getNextBadgeNumber());
        }

        boolean changed = updateGuestFromPerson(existing, person);
        if (changed) {
            guestRepository.save(existing);
        }
        return existing;
    }

    private boolean updateGuestFromPerson(Guest guest, AttendeeImportFile.Person person) {
        boolean changed = false;
        if (guest.getOnlineId() != null && !guest.getOnlineId().equals(person.id())) {
            log.error("Tried to update guest {} that didn't match Person {}'s id", guest, person);
            throw new RuntimeException("Tried to update guest from the wrong person");
        }
        if (guest.getOnlineId() == null ||
                !Objects.equals(guest.getOnlineId(), person.id()) ||
                !Objects.equals(guest.getFirstName(), person.namePreferredFirst()) ||
                !Objects.equals(guest.getLastName(), person.namePreferredLast()) ||
                !Objects.equals(guest.getLegalFirstName(), person.nameOnIdFirst()) ||
                !Objects.equals(guest.getLegalLastName(), person.nameOnIdLast()) ||
                !Objects.equals(guest.getPreferredPronoun(), person.preferredPronoun()) ||
                !Objects.equals(guest.getFanName(), person.fanName()) ||
                !Objects.equals(guest.getBirthDate().toString(), person.birthdate()) ||
                !Objects.equals(guest.getAgeCategoryAtCon(), person.ageCategoryConCurrentTerm()) ||
                !Objects.equals(guest.getHasBadgeImage(), person.hasBadgeImage()) ||
                !Objects.equals(guest.getBadgeImageFileType(), person.badgeImageFileType())
        ) {
            changed = true;
        }
        guest.setOnlineId(person.id());
        guest.setFirstName(person.namePreferredFirst());
        guest.setLastName(person.namePreferredLast());
        guest.setLegalFirstName(person.nameOnIdFirst());
        guest.setLegalLastName(person.nameOnIdLast());
        guest.setPreferredPronoun(person.preferredPronoun());
        guest.setFanName(person.fanName());
        guest.setBirthDate(LocalDate.parse(person.birthdate()));
        guest.setAgeCategoryAtCon(person.ageCategoryConCurrentTerm());
        guest.setHasBadgeImage(person.hasBadgeImage());
        guest.setBadgeImageFileType(person.badgeImageFileType());

        return changed;
    }
}
