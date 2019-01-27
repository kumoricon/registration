package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Unlike most repositories, Session access should only happen through SessionService
 */

@Service
interface TillSessionRepository extends JpaRepository<TillSession, Integer> {
    @Query(value = "select s from TillSession s where s.user = ?1 AND s.open = true")
    TillSession getOpenSessionForUser(User user);

    @Query(value = "select s from TillSession s where s.open = true")
    List<TillSession> findAllOpenSessions();

    @Query(value = "select s from TillSession s ORDER BY s.endTime desc")
    List<TillSession> findAllOrderByEnd();

    @Query(value = "select s from TillSession s WHERE s.id = ?1")
    TillSession findOneById(Integer id);
}