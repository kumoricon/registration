package org.kumoricon.registration.model.attendee;

public class CheckInByBadgeTypeDTO {
    private final String badgeName;
    private final Integer checkedInPreReg;
    private final Integer notCheckedInPreReg;
    private final Integer checkedInAtCon;
    private final Integer notCheckedInAtCon;

    public CheckInByBadgeTypeDTO(String badgeName, Integer checkedInPreReg, Integer notCheckedInPreReg, Integer checkedInAtCon, Integer notCheckedInAtCon) {
        this.badgeName = badgeName;


        this.checkedInPreReg = checkedInPreReg == null ? 0 : checkedInPreReg;
        this.notCheckedInPreReg = notCheckedInPreReg == null ? 0 : notCheckedInPreReg;
        this.checkedInAtCon = checkedInAtCon == null ? 0 : checkedInAtCon;
        this.notCheckedInAtCon = notCheckedInAtCon == null ? 0 : notCheckedInAtCon;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public Integer getCheckedInPreReg() {
        return checkedInPreReg;
    }

    public Integer getNotCheckedInPreReg() {
        return notCheckedInPreReg;
    }

    public Integer getCheckedInAtCon() {
        return checkedInAtCon;
    }

    public Integer getNotCheckedInAtCon() {
        return notCheckedInAtCon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckInByBadgeTypeDTO that = (CheckInByBadgeTypeDTO) o;

        return badgeName.equals(that.badgeName);
    }

    @Override
    public int hashCode() {
        return badgeName.hashCode();
    }
}
