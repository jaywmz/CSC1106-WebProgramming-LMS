package webprogramming.csc1106.Controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.LoggerFactory;
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

     private static final Logger logger = LoggerFactory.getLogger(PartnershipController.class);


    @Autowired
    private PartnerService partnerService;

    @Autowired
    private UploadCourseService uploadCourseService;

    @Autowired
    private PartnerCertificateRepository partnerCertificateRepository;

    @Autowired
    private PartnerPublishRepository partnerPublishRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private AzureBlobService azureBlobService;



    @GetMapping
    public String showPartnershipPage() {
        return "Partnership/partnership";
    }

    @GetMapping("/partner/viewAllCourses")
    public String viewAllCourses(@RequestParam("userId") int userId, Model model) {
        User user = userRepository.findById(userId).get();
        Partner partner = partnerRepository.findByUser(user);
        // Fetch courses uploaded by the partner based on userId
        List<UploadCourse> courses = uploadCourseService.getCoursesByPartnerId(partner.getPartnerId());

         // Fetch PartnerPublish entities associated with the partner
         List<PartnerPublish> partnerPublishes = partnerService.getPartnerPublishByPartnerId(partner.getPartnerId());

         // Pass courses and partnerPublishes to the view
         model.addAttribute("courses", courses);
         model.addAttribute("partnerPublishes", partnerPublishes);

         List<Long> courseIds = courses.stream().map(UploadCourse::getId).collect(Collectors.toList());
    List<CourseCategory> courseCategories = partnerService.getCourseCategoriesByCourseIds(courseIds);
    model.addAttribute("courseCategories", courseCategories);

        return "Partnership/viewAllCourses"; 
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
                           @RequestParam("certificateTitle") String certificateTitle,@RequestParam("userId") int userId) {
    try {
        logger.info("Attempting to upload course for user ID: " + userId);
        // Upload file to Azure Blob Storage and get Blob URL
        String blobUrl1 = azureBlobService.uploadToAzureBlob(coverImage1.getInputStream(), coverImage1.getOriginalFilename());
        String blobUrl2 = azureBlobService.uploadToAzureBlob(coverImage2.getInputStream(), coverImage2.getOriginalFilename());

        blobUrl2 = azureBlobService.generateSasUrl(blobUrl2);

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

        //User user = userRepository.findByLoginCookie(userId);

        logger.info("Attempting get user " + userId);

        Partner partner = partnerRepository.findByUserUserId(userId);
            if (partner == null) {
                throw new RuntimeException("Partner not found for user ID " + userId);
            }
            publish.setPartner(partner);
       

        // Set the certificate association
        publish.setCertificate(certificate);

        // Save the PartnerPublish entity
        uploadCourseService.addPartnerPublish(publish);

        return "redirect:/partner"; 
    } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception appropriately
        return "error-page"; // Redirect to an error page or handle differently
    }
}

@GetMapping("/partner/editCourse/{id}")
    public String editCourse(@PathVariable("id") Long id, Model model) {
        Optional<UploadCourse> optionalCourse = uploadCourseService.getCourseById(id);
        if (optionalCourse.isPresent()) {
            UploadCourse course = optionalCourse.get();
            model.addAttribute("course", course);
            model.addAttribute("categories", uploadCourseService.getAllCategories());
            return "Partnership/editCourse";
        } else {
            // Handle the case where the course is not found
            return "error-page"; // Redirect to an error page or handle differently
        }
    }

    @PostMapping("/partner/editCourse")
    public String updateCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage1") MultipartFile coverImage1,
                               @RequestParam("coverImage2") MultipartFile coverImage2,
                               @RequestParam("selectedCategory") Long selectedCategory,
                               @RequestParam("certificateTitle") String certificateTitle) {
        try {
            // Process file uploads and update the course details
            String blobUrl1 = azureBlobService.uploadToAzureBlob(coverImage1.getInputStream(), coverImage1.getOriginalFilename());
            String blobUrl2 = azureBlobService.uploadToAzureBlob(coverImage2.getInputStream(), coverImage2.getOriginalFilename());

            blobUrl2 = azureBlobService.generateSasUrl(blobUrl2);
            
            // Update the course details
            uploadCourseService.updateCourse(course, coverImage1, selectedCategory, blobUrl2, certificateTitle);
            
         // Fetch PartnerPublish entities associated with the partner
          Partner partner = partnerService.getPartnerByCourseId(course);
          User user = partner.getUser();

            return "redirect:/partnership/partner/viewAllCourses?userId=" + user.getUserID(); 
        } catch (IOException e) {
            e.printStackTrace();
            return "error-page";
        }

        
    }

    @GetMapping("/partner/deleteCourse/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {

        // Fetch PartnerPublish entities associated with the partner
        Optional<UploadCourse> Course = uploadCourseService.getCourseById(id);
        UploadCourse course = Course.get();
        Partner partner = partnerService.getPartnerByCourseId(course);
        User user = partner.getUser();
        partnerService.deletePartnerPublishByCourse(course);
        uploadCourseService.deleteCourse(id);

        return "redirect:/partnership/partner/viewAllCourses?userId=" + user.getUserID(); 
    }



}





