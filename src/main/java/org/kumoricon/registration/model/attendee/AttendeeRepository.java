package org.kumoricon.registration.model.attendee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public interface AttendeeRepository extends JpaRepository<Attendee, Integer>, JpaSpecificationExecutor {
    Attendee findOneById(Integer id);

    @Query(value = "select a from Attendee a inner join a.order as order where order.orderId like ?1")
    List<Attendee> findByOrderNumber(String searchString);

    @Query(value = "SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as CheckInDate, COUNT(id) AS cnt, SUM(paid_amount) as Amount FROM attendees WHERE checked_in = TRUE AND pre_registered = FALSE GROUP BY CheckInDate ORDER BY CheckInDate;", nativeQuery = true)
    List<Object[]> findAtConCheckInCountsByDate();

    @Query(value = "SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as CheckInDate, COUNT(id) AS cnt, SUM(paid_amount) as Amount FROM attendees WHERE checked_in = TRUE AND pre_registered = TRUE GROUP BY CheckInDate ORDER BY CheckInDate;", nativeQuery = true)
    List<Object[]> findPreRegCheckInCountsByDate();

    @Query(value = "SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as checkInDate, EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') as checkInHour, COALESCE(atConCheckedIn.cnt, 0) as AtConCheckedIn, COALESCE(preRegCheckedIn.cnt, 0) as PreRegCheckedIn, COUNT(checked_in) as Total FROM attendees LEFT OUTER JOIN (SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as aCheckInDate, EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') as aCheckInHour, COUNT(attendees.checked_in) as cnt FROM attendees  WHERE attendees.checked_in = TRUE AND attendees.pre_registered = TRUE GROUP BY aCheckInDate, aCheckInHour) as preRegCheckedIn ON DATE(check_in_time at time zone 'America/Los_Angeles') = preRegCheckedIn.aCheckInDate AND EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') = preRegCheckedIn.aCheckInHour LEFT OUTER JOIN (SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as aCheckInDate, EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') as aCheckInHour, COUNT(attendees.checked_in) as cnt FROM attendees  WHERE attendees.checked_in = TRUE AND attendees.pre_registered = FALSE GROUP BY aCheckInDate, aCheckInHour) as atConCheckedIn ON DATE(check_in_time at time zone 'America/Los_Angeles') = atConCheckedIn.aCheckInDate AND EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') = atConCheckedIn.aCheckInHour WHERE checked_in = TRUE GROUP BY checkInDate, checkInHour, atConCheckedIn.cnt, preRegCheckedIn.cnt ORDER BY checkInDate DESC, checkInHour;", nativeQuery = true)
    List<Object[]> findCheckInCountsByHour();

    // Warm body count includes all badges
    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT first_name, last_name, zip, birth_date FROM attendees WHERE checked_in=TRUE) as t", nativeQuery = true)
    Integer findWarmBodyCount();

    // Total attendee count calculation. From: https://www.kumoricon.org/history
    // Attendance figures for all years are unique, paid (rather than "turnstile")—this means that weekend badges are
    // counted only once, and the count is a number of unique individual attendees who registered in a given year.
    // Attendance figures count paid membership purchases at standard or VIP rates (staff, exhibitors, artists,
    // guests, industry, press, and complimentary badges are not counted). Prior to 2014, multiple single-day badges
    // were double-counted (for example, a person purchases Saturday, then Sunday the next day); for 2014 and after,
    // only one is counted (this is an estimated less than 2% discrepancy).
    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT first_name, last_name, zip, birth_date FROM attendees WHERE checked_in=TRUE AND paid_amount > 0) as t", nativeQuery = true)
    Integer findTotalAttendeeCount();

    @Transactional
    @Modifying
    @Query(value = "delete from Attendee a where a.id = ?1")
    void deleteById(Integer id);

}