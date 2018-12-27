package org.kumoricon.registration.admin.loginsession;

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
