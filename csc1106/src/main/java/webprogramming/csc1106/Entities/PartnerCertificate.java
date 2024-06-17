package webprogramming.csc1106.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "partner_certificate")
public class PartnerCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Integer certificateId;

    @Column(name = "certificate_name", length = 255)
    private String certificateName;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "blob_url")
    private String blobUrl;

    @OneToOne(mappedBy = "certificate")
    private PartnerPublish partnerPublish;

    // Constructors
    public PartnerCertificate() {
    }

    public PartnerCertificate(String certificateName, LocalDateTime issueDate) {
        this.certificateName = certificateName;
        this.issueDate = issueDate;
    }

    // Getters and setters
    public Integer getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Integer certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public String getBlobUrl() {
        return blobUrl;
    }

    public void setBlobUrl(String blobUrl) {
        this.blobUrl = blobUrl;
    }


    public PartnerPublish getPartnerPublish() {
        return partnerPublish;
    }

    public void setPartnerPublish(PartnerPublish partnerPublish) {
        this.partnerPublish = partnerPublish;
    }
}
