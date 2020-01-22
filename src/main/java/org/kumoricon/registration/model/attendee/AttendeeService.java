package org.kumoricon.registration.model.attendee;

import org.kumoricon.registration.model.blacklist.BlacklistName;
import org.kumoricon.registration.model.blacklist.BlacklistRepository;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final BlacklistRepository blacklistRepository;

    private static final Logger log = LoggerFactory.getLogger(AttendeeService.class);

    public AttendeeService(AttendeeRepository attendeeRepository,
                           AttendeeHistoryRepository attendeeHistoryRepository,
                           BlacklistRepository blacklistRepository) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.blacklistRepository = blacklistRepository;
    }

    @Transactional
    public Attendee checkInAttendee(Integer attendeeId, User user, Boolean parentFormReceived) {
        Attendee attendee = attendeeRepository.findById(attendeeId);

        if (attendee != null) {
            log.info("checked in {}", attendee);
        } else {
            log.error("tried to check in attendee id {} but it wasn't found", attendeeId);
            throw new RuntimeException("Attendee " + attendeeId + " not found");
        }

        if (attendee.isMembershipRevoked()) {
            log.error("tried to check in {} but membership was revoked", attendee);
            throw new RuntimeException("membership was revoked for " + attendee.getName());
        }

        if (attendee.isMinor() && !parentFormReceived) {
            throw new RuntimeException("Parental consent form not received");
        }

        attendee.setParentFormReceived(parentFormReceived);
        attendee.setCheckedIn(true);
        attendeeRepository.save(attendee);

        AttendeeHistory ah = new AttendeeHistory(user, attendee.getId(),"Attendee Checked In");
        attendeeHistoryRepository.save(ah);

        return attendee;
    }

    @Transactional
    public void checkInAttendeesInOrder(Integer orderId, User user) {
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        for (Attendee attendee : attendees) {
            if (attendee.getCheckedIn()) {
                throw new RuntimeException("Attendee " + attendee + " is already checked in!");
            }
            checkInAttendee(attendee.getId(), user, attendee.getParentFormReceived());
        }
    }

    @Transactional
    public void revokeMembership(Integer attendeeId, Integer orderId, User user) {
        Attendee a = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        if (a == null) throw new RuntimeException("Attendee id " + attendeeId + " not found");
        revokeAttendeeMembership(a, user);
    }

    @Transactional
    void revokeAttendeeMembership(Attendee attendee, User user) {
        log.info("revoked membership of {}", attendee);
        attendee.setMembershipRevoked(true);
        attendeeRepository.save(attendee);
        AttendeeHistory ah = new AttendeeHistory(user, attendee.getId(),"Membership revoked");
        attendeeHistoryRepository.save(ah);

        BlacklistName name = new BlacklistName(attendee.getFirstName(), attendee.getLastName());
        BlacklistName legalName = new BlacklistName(attendee.getLegalFirstName(), attendee.getLegalLastName());

        blacklistRepository.save(new BlacklistName(attendee.getFirstName(), attendee.getLastName()));
        blacklistRepository.save(new BlacklistName(attendee.getLegalFirstName(), attendee.getLegalLastName()));
        log.info("Added {} to blacklist", name);
        log.info("Added {} to blacklist", legalName);
    }

    @Transactional
    public void save(Attendee attendee) {
        attendeeRepository.save(attendee);
    }
}
