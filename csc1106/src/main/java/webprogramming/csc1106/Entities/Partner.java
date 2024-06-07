package webprogramming.csc1106.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "partner")
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Integer partnerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "partner_email", length = 255)
    private String partnerEmail;

    @Column(name = "approval_reason", length = 255)
    private String approvalReason;

    @Column(name = "validity_end", length = 255)
    private String validityEnd;

    @Column(name = "partner_status", length = 20)
    private String partnerStatus;

    // Constructors
    public Partner() {}

    // Getters and setters
    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public String getValidityEnd() {
        return validityEnd;
    }

    public void setValidityEnd(String validityEnd) {
        this.validityEnd = validityEnd;
    }

    public String getPartnerStatus() {
        return partnerStatus;
    }

    public void setPartnerStatus(String partnerStatus) {
        this.partnerStatus = partnerStatus;
    }
}
