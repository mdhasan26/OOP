package models;

/**
 * Member class represents a library member (student or staff).
 * Encapsulates member details including ID, name, email, and membership type.
 */
public class Member {
    private int memberId;
    private String memberName;
    private String email;
    private String membershipType;

    // Constructor
    public Member(int memberId, String memberName, String email, String membershipType) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.email = email;
        this.membershipType = membershipType;
    }

    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", email='" + email + '\'' +
                ", membershipType='" + membershipType + '\'' +
                '}';
    }
}
