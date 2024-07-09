package webprogramming.csc1106.Entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "partner_renew")
public class PartnerRenew {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "renew_id")
        private Integer renewId;
    
        @ManyToOne
        @JoinColumn(name = "partner_id", nullable = false)
        private Partner partner;
    
        @Column(name = "renew_status", nullable = false, length = 20)
        private String renewStatus;

        @Column(name = "renew_reason", nullable = false)
        private String reason;
        
        @Column(name = "send_datetime", nullable = false)
        private Timestamp senddatetime;
       
        @Column(name = "approval_datetime", nullable = true)
        private Timestamp approvaldatetime;
    
        // Constructors
        public PartnerRenew() {}

        //Give me constrtuctor
        public PartnerRenew(Partner partner, String renewStatus, String reason, Timestamp senddatetime, Timestamp approvaldatetime) {
            this.partner = partner;
            this.renewStatus = renewStatus;
            this.reason = reason;
            this.senddatetime = senddatetime;
            this.approvaldatetime = approvaldatetime;
        }

        // Getters and setters
        public Integer getRenewId() {
            return renewId;
        }
        public void setRenewId(Integer renewId) {
            this.renewId = renewId;
        }
        public Partner getPartner() {
            return partner;
        }
        public void setPartner(Partner partner) {
            this.partner = partner;
        }
        public String getRenewStatus() {
            return renewStatus;
        }
        public void setRenewStatus(String renewStatus) {
            this.renewStatus = renewStatus;
        }
        public String getReason() {
            return reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
        public Timestamp getSenddatetime() {
            return senddatetime;
        }
        public void setSenddatetime(Timestamp senddatetime) {
            this.senddatetime = senddatetime;
        }
        public Timestamp getApprovaldatetime() {
            return approvaldatetime;
        }
        public void setApprovaldatetime(Timestamp approvaldatetime) {
            this.approvaldatetime = approvaldatetime;
        }
        
    }
