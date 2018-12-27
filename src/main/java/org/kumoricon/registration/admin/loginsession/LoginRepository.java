package org.kumoricon.registration.admin.loginsession;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * I found it easier to just query the database for active seesions instead of trying to read from the Spring
 * Session objects. This also means that it would show all sessions, not just sessions ONLY on this server, which
 * means the list will be more consistent.
 */
@Repository
public class LoginRepository {
    private final EntityManager entityManager;

    public LoginRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<SessionInfoDTO> findAll() {
        List<SessionInfoDTO> sessions = new ArrayList<>();
        List<Object[]> rows = entityManager.createNativeQuery(
                        "select " +
                                "       s.PRIMARY_ID as \"primaryId\", " +
                                "       s.PRINCIPAL_NAME as \"principalName\"," +
                                "       s.CREATION_TIME as \"creationTime\"," +
                                "       s.LAST_ACCESS_TIME as \"lastAccessTime\", " +
                                "       s.EXPIRY_TIME as \"expiryTime\" " +
                                "from SPRING_SESSION s ")
                .getResultList();

        for (Object[] row : rows) {
            sessions.add(new SessionInfoDTO(row[0].toString(),
                    row[1].toString(),
                    Long.parseLong(row[2].toString()),
                    Long.parseLong(row[3].toString()),
                    Long.parseLong(row[4].toString())
            ));
        }

        return sessions;
    }

}
