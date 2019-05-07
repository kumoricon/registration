package org.kumoricon.registration.model.attendee;

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
    private static final Logger log = LoggerFactory.getLogger(AttendeeService.class);

    public AttendeeService(AttendeeRepository attendeeRepository, AttendeeHistoryRepository attendeeHistoryRepository) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    @Transactional
    public Attendee checkInAttendee(Integer attendeeId, User user) {
        Attendee attendee = attendeeRepository.findById(attendeeId);
        if (attendee != null) {
            log.info("checking in attendee {}", attendee);
        } else {
            log.error("{} tried to check in attendee id {} but it wasn't found", user, attendeeId);
            throw new RuntimeException("Attendee " + attendeeId + " not found");
        }

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
            checkInAttendee(attendee.getId(), user);
        }
    }

    @Transactional
    public void save(Attendee attendee) {
        attendeeRepository.save(attendee);
    }
}
