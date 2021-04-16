package org.kumoricon.registration.guest;

import org.kumoricon.registration.model.ImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;

@Component
public class GuestImportService extends ImportService {

    private final GuestRepository guestRepository;

    public GuestImportService(@Value("${registration.guestinputpath}") String onlineImportInputPath,
                              @Value("${registration.onlinedlqpath}") String onlineDLQPath,
                              GuestRepository guestRepository) {
        this.onlineImportInputPath = onlineImportInputPath;
        this.onlineDLQPath = onlineDLQPath;
        this.guestRepository = guestRepository;
    }

    protected void importFile(Path filepath) {
        try {
            GuestImportFile importFile = objectMapper.readValue(filepath.toFile(), GuestImportFile.class);
            log.info("{}: Actions: {}   Persons: {}", filepath, importFile.getActions().size(), importFile.getPersons().size());

            for (GuestImportFile.Person person : importFile.getPersons()) {
                try {
                    importPerson(person);
                } catch (Exception ex) {
                    log.error("Error importing {}", person, ex);
                }
            }

            for (GuestImportFile.Action action : importFile.getActions()) {
                log.info("  Action: {}: {}", action.getActionsVersion(), action.getDeleted());
            }
        } catch (IOException ex) {
            log.error("Error loading {}", filepath, ex);
            try {
                Path dest = Paths.get(dlqPath.toString(), filepath.getFileName().toString());
                Files.move(filepath, dest);
            } catch (IOException e) {
                log.error("Error moving {} to DLQ {}", filepath, dlqPath, e);
            }
        }
    }

    private void importPerson(GuestImportFile.Person person) {
        if (person.isCanceled()) {
            log.info("{} was canceled online, deleting from system", person);
            guestRepository.deleteByOnlineId(person.getId());
            return;
        }
        log.info("Importing {}", person);
        Guest existing;
        try {
            existing = guestRepository.findByOnlineId(person.getId());
        } catch (EmptyResultDataAccessException ex) {
            existing = new Guest();
        }

        boolean changed = updateGuestFromPerson(existing, person);
        if (changed) {
            guestRepository.save(existing);
        }
    }

    private boolean updateGuestFromPerson(Guest guest, GuestImportFile.Person person) {
        boolean changed = false;
        if (guest.getOnlineId() != null && !guest.getOnlineId().equals(person.getId())) {
            log.error("Tried to update guest {} that didn't match Person {}'s id", guest, person);
            throw new RuntimeException("Tried to update guest from the wrong person");
        }
        if (guest.getOnlineId() == null ||
                !Objects.equals(guest.getOnlineId(), person.getId()) ||
                !Objects.equals(guest.getFirstName(), person.getNamePreferredFirst()) ||
                !Objects.equals(guest.getLastName(), person.getNamePreferredLast()) ||
                !Objects.equals(guest.getLegalFirstName(), person.getNameOnIdFirst()) ||
                !Objects.equals(guest.getLegalLastName(), person.getNameOnIdLast()) ||
                !Objects.equals(guest.getPreferredPronoun(), person.getPreferredPronoun()) ||
                !Objects.equals(guest.getFanName(), person.getFanName()) ||
                !Objects.equals(guest.getBirthDate().toString(), person.getBirthdate()) ||
                !Objects.equals(guest.getAgeCategoryAtCon(), person.getAgeCategoryConCurrentTerm()) ||
                !Objects.equals(guest.getHasBadgeImage(), person.getHasBadgeImage()) ||
                !Objects.equals(guest.getBadgeImageFileType(), person.getBadgeImageFileType())
        ) {
            changed = true;
        }
        guest.setOnlineId(person.getId());
        guest.setFirstName(person.getNamePreferredFirst());
        guest.setLastName(person.getNamePreferredLast());
        guest.setLegalFirstName(person.getNameOnIdFirst());
        guest.setLegalLastName(person.getNameOnIdLast());
        guest.setPreferredPronoun(person.getPreferredPronoun());
        guest.setFanName(person.getFanName());
        guest.setBirthDate(LocalDate.parse(person.getBirthdate()));
        guest.setAgeCategoryAtCon(person.getAgeCategoryConCurrentTerm());
        guest.setHasBadgeImage(person.getHasBadgeImage());
        guest.setBadgeImageFileType(person.getBadgeImageFileType());

        return changed;
    }
}
