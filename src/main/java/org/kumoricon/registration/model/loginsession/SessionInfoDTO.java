package org.kumoricon.registration.model.loginsession;

/**
 * This is a more limited view of the sessions stored in the database, just used for showing the session list. Note
 * that it is not a Hibernate entity, just a simple DTO. Making it a Hibernate entity was unnecessary because these
 * objects will never be written back to the database, they're just for reporting.
 */
public class SessionInfoDTO {
    private String primaryId;
    private String principalName;
    private Long creationTime;
    private Long lastAccessTime;
    private Long expiryTime;

    /**
     * Constructor that takes raw Objects from database and converts them to the proper types
     * @param primaryId String, not null
     * @param principalName String, may be null
     * @param creationTime, Long, not null
     * @param lastAccessTime Long, not null
     * @param expiryTime Long, not null
     */
    public SessionInfoDTO(Object primaryId, Object principalName, Object creationTime, Object lastAccessTime, Object expiryTime) {
        assert (primaryId != null && creationTime != null && lastAccessTime != null && expiryTime != null);
        this.primaryId = primaryId.toString();

        if (principalName != null) {
            this.principalName = principalName.toString();
        } else {
            this.principalName = "Not logged in yet";
        }

        try {
            this.creationTime = Long.parseLong(creationTime.toString());
            this.lastAccessTime = Long.parseLong(lastAccessTime.toString());
            this.expiryTime = Long.parseLong(expiryTime.toString());
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex);
        }
    }

    public SessionInfoDTO(String primaryId, String principalName, Long creationTime, Long lastAccessTime, Long expiryTime) {
        this.primaryId = primaryId;
        this.principalName = principalName;
        this.creationTime = creationTime;
        this.lastAccessTime = lastAccessTime;
        this.expiryTime = expiryTime;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getLastAccessTime() {
        return lastAccessTime;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }
}
