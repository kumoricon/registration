package org.kumoricon.registration.model.attendee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface AttendeeHistoryRepository extends JpaRepository<AttendeeHistory, Integer> {

    @Query(value = "SELECT ah from AttendeeHistory ah where ah.attendee = ?1 ORDER BY ah.timestamp ASC")
    List<AttendeeHistory> findByAttendee(Attendee attendee);

    @Query(value = "SELECT users.first_name, users.last_name, COUNT(attendeehistory.id) FROM attendeehistory JOIN users ON attendeehistory.user_id = users.id WHERE attendeehistory.message='Attendee Checked In' AND timestamp >= (NOW() - (15 * interval '1 minute')) AND attendeehistory.timestamp <= NOW() GROUP BY user_id, first_name, last_name", nativeQuery = true)
    List<Object[]> checkInCountByUsers();
}
