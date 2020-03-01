package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;


@Service
public class TillSessionService {
    private final TillSessionRepository repository;

    @Autowired
    public TillSessionService(TillSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TillSession getCurrentOrNewTillSession(User user, String optionalTillName) {
        if (user == null) { throw new RuntimeException("getCurrentOrNewTillSession called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        if (session == null) {
            session = getNewSessionForUser(user, optionalTillName);
        }
        return session;
    }

    @Transactional
    public TillSession getNewSessionForUser(User user, String tillName) {
        if (user == null) { throw new RuntimeException("getNewSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        if (session != null) {
            closeSession(session);
        }
        session = repository.save(new TillSession(user, tillName));
        return session;
    }

    @Transactional(readOnly = true)
    public boolean userHasOpenSession(User user) {
        if (user == null) { throw new RuntimeException("userHasOpenSession called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        return session != null;
    }

    @Transactional
    public TillSession closeSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("closeSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        return closeSession(session);
    }

    @Transactional
    public TillSession closeSessionForUser(User user, String tillName) {
        if (user == null) { throw new RuntimeException("closeSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        session.setTillName(tillName);
        return closeSession(session);
    }

    @Transactional
    public TillSession closeSession(TillSession session) {
        if (session != null) {
            if (session.isOpen()) {
                session.setEndTime(OffsetDateTime.now());
                session.setOpen(false);
                repository.save(session);
            } else {
                throw new RuntimeException(String.format("Session %s is already closed", session));
            }
        }
        return session;
    }

    @Transactional
    public TillSession closeSession(Integer id) {
        TillSession session = repository.findOneById(id);
        return closeSession(session);
    }

    public List<TillSessionDTO> getAllTillSessionDTOs() { return repository.findAllTillSessionDTO(); }
    public List<TillSessionDTO> getOpenTillSessionDTOs() { return repository.findOpenTillSessionDTOs(); }


    @Transactional(readOnly = true)
    public TillSessionDTO getOpenSessionForUser(User currentUser) {
        return repository.getOpenTillSessionDTOforUser(currentUser);
    }

    @Transactional(readOnly = true)
    public TillSessionDetailDTO getTillDetailDTO(Integer id) {
        TillSessionDTO tillSessionDTO = repository.getTillSessionDTOById(id);
        TillSessionDetailDTO detailDTO = TillSessionDetailDTO.fromTillSessionDTO(tillSessionDTO);

        detailDTO.setPaymentTotals(repository.getPaymentTotals(id));
        detailDTO.setBadgeCounts(repository.getBadgeCounts(id));
        detailDTO.setOrderDTOs(repository.getOrderDetails(id));

        return detailDTO;
    }
}
