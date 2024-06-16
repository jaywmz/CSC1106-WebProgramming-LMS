package webprogramming.csc1106.Controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import webprogramming.csc1106.Services.AzureBlobService;
import webprogramming.csc1106.Services.PartnerService;
import webprogramming.csc1106.Services.UploadCourseService;
import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.*;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/partnership")
public class PartnershipController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private UploadCourseService uploadCourseService;

    @Autowired
    private PartnerCertificateRepository partnerCertificateRepository;

    @Autowired
    private PartnerPublishRepository partnerPublishRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private AzureBlobService azureBlobService;



    @GetMapping
    public String showPartnershipPage() {
        return "Partnership/partnership";
    }

    @PostMapping("/submit")
    @ResponseBody
    public String submitPartnershipApplication(
        @RequestParam("companyName") String companyName,
        @RequestParam("partnerEmail") String partnerEmail,
        @RequestParam("businessSector") String businessSector,
        @RequestParam("approvalReason") String approvalReason) {

        Partner partner = new Partner();
        partner.setCompanyName(companyName);
        partner.setPartnerEmail(partnerEmail);
        partner.setApprovalReason(approvalReason);
        partner.setPartnerStatus("Pending");  // Set default status as pending

        partnerService.savePartner(partner);

        return "{\"message\":\"Your application has been submitted successfully!\"}";
    }

    @GetMapping("/list")
    public String listPartners(Model model) {
        List<Partner> partners = partnerService.getAllPartners();
        model.addAttribute("partners", partners);
        return "Partnership/partnershiplisting";
    }

    @PostMapping("/approve/{partnerId}")
    @ResponseBody
    public ResponseEntity<String> approvePartner(@PathVariable Integer partnerId) {
        try {
            boolean success = partnerService.approvePartner(partnerId);
            if (success) {
                return ResponseEntity.ok("{\"success\": true}");
            } else {
                return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"Failed to approve partner.\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/reject/{partnerId}")
    @ResponseBody
    public ResponseEntity<String> rejectPartner(@PathVariable Integer partnerId) {
        try {
            boolean success = partnerService.rejectPartner(partnerId);
            if (success) {
                return ResponseEntity.ok("{\"success\": true}");
            } else {
                return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"Failed to reject partner.\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/partner/uploadCourse")
    public String getUploadCoursePage(Model model) {
        model.addAttribute("course", new UploadCourse());
        model.addAttribute("categories", uploadCourseService.getAllCategories());
        return "Partnership/partner_course_upload";
    }
    
    @PostMapping("/partner/uploadCourse")
public String uploadCourse(@ModelAttribute UploadCourse course,
                           @RequestParam("coverImage1") MultipartFile coverImage1,
                           @RequestParam("coverImage2") MultipartFile coverImage2,
                           @RequestParam("category") Long categoryId,
                           @RequestParam("certificateTitle") String certificateTitle) {
    try {
        // Upload file to Azure Blob Storage and get Blob URL
        String blobUrl1 = azureBlobService.uploadToAzureBlob(coverImage1.getInputStream(), coverImage1.getOriginalFilename());
        String blobUrl2 = azureBlobService.uploadToAzureBlob(coverImage2.getInputStream(), coverImage2.getOriginalFilename());

        // Create a new PartnerCertificate entity
        PartnerCertificate certificate = new PartnerCertificate();
        certificate.setCertificateName(certificateTitle);
        certificate.setBlobUrl(blobUrl2); // Set Blob URL
        certificate.setIssueDate(LocalDateTime.now());

        // Save the PartnerCertificate entity
        uploadCourseService.addPartnerCertificate(certificate);

        // Create a new PartnerPublish entity and set its properties
        PartnerPublish publish = new PartnerPublish();
        publish.setPublishDate(LocalDateTime.now()); // Set current date and time
        publish.setPublishStatus("Successful");

        // Process the course upload and capture the returned UploadCourse entity
        UploadCourse savedCourse = uploadCourseService.partnerprocessCourseUpload(course, coverImage1, categoryId);

        // Set associations for the saved entities
        publish.setCourse(savedCourse); // Set the upload course association

        // Set the partner association (temporary assumption: partner ID is 1)
        Partner partner = partnerRepository.findById(1).orElseThrow(() -> new RuntimeException("Partner with ID 1 not found"));
        publish.setPartner(partner);

        // Set the certificate association
        publish.setCertificate(certificate);

        // Save the PartnerPublish entity
        uploadCourseService.addPartnerPublish(publish);

        return "redirect:/partnership/partner/uploadCourse"; // Redirect to the upload page again
    } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception appropriately
        return "error-page"; // Redirect to an error page or handle differently
    }
}


}

