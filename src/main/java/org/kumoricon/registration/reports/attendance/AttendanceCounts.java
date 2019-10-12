package org.kumoricon.registration.reports.attendance;

class AttendanceCounts {
    private Integer totalConventionMembers;
    private Integer warmBodyCount;
    private Integer paidMembers;
    private Integer paidAttendance;

    AttendanceCounts() {
        this.totalConventionMembers = 0;
        this.warmBodyCount = 0;
        this.paidMembers = 0;
        this.paidAttendance = 0;
    }

    void incrmentTotalConventionMembers() {
        synchronized (this) {
            this.totalConventionMembers += 1;
        }
    }

    void incrementWarmBodyCount() {
        synchronized (this) {
            this.warmBodyCount += 1;
        }
    }

    void incrementPaidMembers() {
        synchronized (this) {
            this.paidMembers += 1;
        }
    }

    void incrementPaidAttendees() {
        synchronized (this) {
            this.paidAttendance += 1;
        }
    }

    public Integer getTotalConventionMembers() {
        synchronized (this) {
            return totalConventionMembers;
        }
    }

    public Integer getWarmBodyCount() {
        synchronized (this) {
            return warmBodyCount;
        }
    }

    public Integer getPaidMembers() {
        synchronized (this) {
            return paidMembers;
        }
    }

    public Integer getPaidAttendance() {
        synchronized (this) {
            return paidAttendance;
        }
    }
}