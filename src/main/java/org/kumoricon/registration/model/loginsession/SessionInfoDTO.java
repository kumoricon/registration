package org.kumoricon.registration.model.loginsession;

/**
 * This is a more limited view of the sessions stored in the database, just used for showing the session list. Note
 * that it is not a Hibernate entity, just a simple DTO. Making it a Hibernate entity was unnecessary because these
 * objects will never be written back to the database, they're just for reporting.
 */
public class SessionInfoDTO {
    private final String primaryId;
    private final String principalName;
    private final Long creationTime;
    private final Long lastAccessTime;
    private final Long expiryTime;

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
