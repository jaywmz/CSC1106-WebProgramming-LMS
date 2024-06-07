package webprogramming.csc1106.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "partner_publish")
public class PartnerPublish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publish_id")
    private Integer publishId;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private UploadCourse course;

    @ManyToOne
    @JoinColumn(name = "certificate_id", nullable = false)
    private PartnerCertificate certificate;

    @Column(name = "publish_date", nullable = false)
    private LocalDateTime publishDate;

    @Column(name = "publish_status", nullable = false, length = 20)
    private String publishStatus;

    // Constructors
    public PartnerPublish() {}

    public PartnerPublish(Partner partner, UploadCourse course, PartnerCertificate certificate, LocalDateTime publishDate, String publishStatus) {
        this.partner = partner;
        this.course = course;
        this.certificate = certificate;
        this.publishDate = publishDate;
        this.publishStatus = publishStatus;
    }

    // Getters and setters
    public Integer getPublishId() {
        return publishId;
    }

    public void setPublishId(Integer publishId) {
        this.publishId = publishId;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    public PartnerCertificate getCertificate() {
        return certificate;
    }

    public void setCertificate(PartnerCertificate certificate) {
        this.certificate = certificate;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }
}
