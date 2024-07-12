package webprogramming.csc1106.Controllers;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.CourseSubscriptionEntity;
import webprogramming.csc1106.Entities.FileResource;
import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Entities.PartnerCertificate;
import webprogramming.csc1106.Entities.PartnerPublish;
import webprogramming.csc1106.Entities.PartnerRenew;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.PartnerCertificateRepository;
import webprogramming.csc1106.Repositories.PartnerPublishRepository;
import webprogramming.csc1106.Repositories.PartnerRenewRepository;
import webprogramming.csc1106.Repositories.PartnerRepository;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Services.AzureBlobService;
import webprogramming.csc1106.Services.CourseSubscriptionService;
import webprogramming.csc1106.Services.EmailService;
import webprogramming.csc1106.Services.LessonService;
import webprogramming.csc1106.Services.PartnerService;
import webprogramming.csc1106.Services.SectionService;
import webprogramming.csc1106.Services.UploadCourseService;


@Controller
@RequestMapping("/partnership")
public class PartnershipController {

     private static final Logger logger = LoggerFactory.getLogger(PartnershipController.class);


    @Autowired
    private PartnerService partnerService;

    @Autowired
    private UploadCourseService uploadCourseService;

    @Autowired
    private CourseSubscriptionService courseSubscriptionService;

    @Autowired
    private PartnerCertificateRepository partnerCertificateRepository;

    @Autowired
    private PartnerPublishRepository partnerPublishRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerRenewRepository partnerRenewRepository;

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private UploadCourseService courseService;

    @Autowired
    private EmailService emailService;


    @GetMapping
    public String showPartnershipPage() {
        return "Partnership/partnership";
    }    



    @GetMapping("/partner/renew")
    public String showRenewalForm(@RequestParam("userId") int userId, Model model) {
        User user = userRepository.findById(userId).get();
        Partner partner = partnerRepository.findByUser(user);
        model.addAttribute("partner", partner);
        return "Partnership/renew";
    }

    @PostMapping("/partner/renew")
    public String processRenewal(@RequestParam("partnerId") int partnerId, Model model) {
        boolean success = partnerService.renewPartnerSubscription(partnerId);
        Partner partner = partnerRepository.findById(partnerId).get();
        model.addAttribute("partner", partner);
        if (success) {
            model.addAttribute("message", "Subscription renewed successfully!");
        } else {
            model.addAttribute("message", "Renewal Request Unsuccessful. Subscription is not within 30 days of expiry.");
        }
        return "Partnership/renew";
    }

    //get for renewExpiredSubcription
    @GetMapping("/renewExpired")
    public String getRenewExpiredSubscription() {
        return "Partnership/renewExpiredSubscription";
    }
    
    // For renewal (EXPIRED), from login redirect
    @PostMapping("/renewExpired")
    public String renewExpiredSubscription(@RequestParam("email") String email, @RequestParam("reason") String reason, Model model) {
        User user = userRepository.findByUserEmail(email);
        Partner partner = partnerRepository.findByUser(user);
        
        if (partner == null || partner.getValidityEnd().after(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("error", "Invalid email or partner subscription is still valid.");
            return "Partnership/renewExpiredSubscription";
        }
        
        PartnerRenew existingRenewal = partnerRenewRepository.findByPartnerAndRenewStatus(partner, "Pending");
        if (existingRenewal != null) {
            model.addAttribute("error", "You have already submitted a renewal request. It is pending for admin action.");
            return "Partnership/renewExpiredSubscription";
        }
        
        PartnerRenew partnerRenew = new PartnerRenew(partner, "Pending", reason, new Timestamp(System.currentTimeMillis()), null);
        partnerRenewRepository.save(partnerRenew);
        
        model.addAttribute("success", "Renewal request submitted successfully.");
        return "Partnership/renewExpiredSubscription";
    }
    
    // For renewal (EXPIRED), for admin actions
    @GetMapping("/approvalExpiredSubscription")
    public String getApprovalExpiredSubscription(Model model) {
        List<PartnerRenew> renewList = partnerRenewRepository.findAllByRenewStatus("Pending");
        model.addAttribute("renewList", renewList);
        return "Partnership/approvalExpiredSubscription";
    }

    @PostMapping("/approveRenewal/{id}")
    public ResponseEntity<Void> approveRenewal(@PathVariable Integer id) {
        Optional<PartnerRenew> optionalRenew = partnerRenewRepository.findById(id);
        if (optionalRenew.isPresent()) {
            PartnerRenew renew = optionalRenew.get();
            renew.setRenewStatus("Approved");
            // set the renew partner validity end to be extended by 1 year
            Timestamp validityEnd = renew.getPartner().getValidityEnd();
            LocalDateTime localDateTime = validityEnd.toLocalDateTime();
            localDateTime = localDateTime.plusYears(1);
            renew.getPartner().setValidityEnd(Timestamp.valueOf(localDateTime));
            renew.setApprovaldatetime(new Timestamp(System.currentTimeMillis()));
            partnerRenewRepository.save(renew);
            //send renew success email
            emailService.sendRenewalSuccessEmail(renew.getPartner().getPartnerEmail(), renew.getPartner().getCompanyName(), renew.getPartner().getValidityEnd());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rejectRenewal/{id}")
    public ResponseEntity<Void> rejectRenewal(@PathVariable Integer id) {
        Optional<PartnerRenew> optionalRenew = partnerRenewRepository.findById(id);
        if (optionalRenew.isPresent()) {
            PartnerRenew renew = optionalRenew.get();
            renew.setRenewStatus("Rejected");
            renew.setApprovaldatetime(new Timestamp(System.currentTimeMillis()));
            partnerRenewRepository.save(renew);
        }
        return ResponseEntity.ok().build();
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


    @GetMapping("/partner/Courses")
    public String getCoursePage(@RequestParam("userId") int userId, Model model) {
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

        return "Partnership/Courses"; 
    }

    @PostMapping("/partner/sections")
    public String createSection(@RequestParam("courseId") Long courseId, @RequestParam("sectionName") String sectionName, @RequestParam("sectionDesc") String sectionDesc) {
        Section section = new Section();
        section.setTitle(sectionName);
        section.setDescription(sectionDesc);
        sectionService.createSection(section, courseId);

          // Fetch PartnerPublish entities associated with the partner
          UploadCourse course = courseService.getCourseById(courseId).get();
          Partner partner = partnerService.getPartnerByCourseId(course);
          User user = partner.getUser();

            return "redirect:/partnership/partner/Courses?userId=" + user.getUserID(); 
    }

    @PostMapping("/partner/editsections")
    public String editSection(@RequestParam("editsectionId") Long sectionId, @RequestParam("editsectionName") String sectionName, @RequestParam("editsectionDesc") String sectionDesc, @RequestParam("editsectionCourseId") Long courseId) {
        Section section = sectionService.getSectionById(sectionId);
        section.setTitle(sectionName);
        section.setDescription(sectionDesc);
        sectionService.updateSection(sectionId, section);

          // Fetch PartnerPublish entities associated with the partner
          UploadCourse course = courseService.getCourseById(courseId).get();
          Partner partner = partnerService.getPartnerByCourseId(course);
          User user = partner.getUser();

            return "redirect:/partnership/partner/Courses?userId=" + user.getUserID(); 
    }

    @PostMapping("/partner/deletesections")
    public String deleteSection(@RequestParam("deletesectionId") Long sectionId, @RequestParam("deletesectionCourseId") Long courseId) {
        Section section = sectionService.getSectionById(sectionId);
        sectionService.deleteSection(sectionId);

          // Fetch PartnerPublish entities associated with the partner
          UploadCourse course = courseService.getCourseById(courseId).get();
          Partner partner = partnerService.getPartnerByCourseId(course);
          User user = partner.getUser();

            return "redirect:/partnership/partner/Courses?userId=" + user.getUserID(); 
    }


    @PostMapping("/partner/lessons")
public String createLesson(
        @RequestParam("lessonCourseId") Long courseId,
        @RequestParam("sectionId") Long sectionId,
        @RequestParam("lessonName") String lessonName,
        @RequestParam("lessonFile") MultipartFile lessonFile) {

    Lesson lesson = new Lesson();
    lesson.setTitle(lessonName);

    // Handle file upload
    if (!lessonFile.isEmpty()) {
        try {
            String fileName = lessonFile.getOriginalFilename();
            String blobUrl = azureBlobService.uploadToAzureBlob(lessonFile.getInputStream(), fileName);
            blobUrl = azureBlobService.generateSasUrl(blobUrl);

            FileResource fileResource = new FileResource();
            fileResource.setFileName(fileName);
            fileResource.setFileUrl(blobUrl);
            fileResource.setLesson(lesson);

            lesson.getFiles().add(fileResource);
        } catch (IOException e) {
            // Handle the IOException (e.g., log the error and/or set an error message)
            e.printStackTrace(); // Example of logging the exception
            // Optionally, add a redirect to an error page or return an error message
            return "redirect:/error";
        }
    }

    lessonService.createLesson(lesson, sectionId);
    
     // Fetch PartnerPublish entities associated with the partner
     UploadCourse course = courseService.getCourseById(courseId).get();
     Partner partner = partnerService.getPartnerByCourseId(course);
     User user = partner.getUser();

    return "redirect:/partnership/partner/Courses?userId=" + user.getUserID(); 
}


@PostMapping("/partner/editlessons")
public String editLesson(
    @RequestParam("editlessonId") Long lessonId,
    @RequestParam("editlessonName") String lessonName,
    @RequestParam("editlessonCourseId") Long courseId) {
    // Your code here
    Lesson lesson = lessonService.getLessonById(lessonId);
    lesson.setTitle(lessonName);

    lessonService.updateLesson(lessonId,lesson);
         UploadCourse course = courseService.getCourseById(courseId).get();
         Partner partner = partnerService.getPartnerByCourseId(course);
         User user = partner.getUser();
        return "redirect:/partnership/partner/Courses?userId=" + user.getUserID();
}


//post for /partner/deleteLessons
@PostMapping("/partner/deletelessons")
public String deleteLesson(@RequestParam("deletelessonId") Long lessonId, @RequestParam("deletelessonCourseId") Long courseId) {
    // Your code here
    Lesson lesson = lessonService.getLessonById(lessonId);
    lessonService.deleteLesson(lessonId);
    UploadCourse course = courseService.getCourseById(courseId).get();
    Partner partner = partnerService.getPartnerByCourseId(course);
    User user = partner.getUser();
    return "redirect:/partnership/partner/Courses?userId=" + user.getUserID();
}


//post for /partner/addmorefiles
@PostMapping("/partner/addmorefiles")
public String addMoreFiles(@RequestParam("addmorefilelessonId") Long lessonId, @RequestParam("addmorefilelessonFile") MultipartFile lessonFile, @RequestParam("addmorefilelessonCourseId") Long courseId) {
    // Your code here
    Lesson lesson = lessonService.getLessonById(lessonId);
    // Handle file upload
    if (!lessonFile.isEmpty()) {
        try {
            String fileName = lessonFile.getOriginalFilename();
            String blobUrl = azureBlobService.uploadToAzureBlob(lessonFile.getInputStream(), fileName);
            blobUrl = azureBlobService.generateSasUrl(blobUrl);

            FileResource fileResource = new FileResource();
            fileResource.setFileName(fileName);
            fileResource.setFileUrl(blobUrl);
            fileResource.setLesson(lesson);

            lesson.getFiles().add(fileResource);
        } catch (IOException e) {
            // Handle the IOException (e.g., log the error and/or set an error message)
            e.printStackTrace(); // Example of logging the exception
            // Optionally, add a redirect to an error page or return an error message
            return "redirect:/error";
        }
    }
    lessonService.updateLesson(lessonId,lesson);
    UploadCourse course = courseService.getCourseById(courseId).get();
    Partner partner = partnerService.getPartnerByCourseId(course);
    User user = partner.getUser();
    return "redirect:/partnership/partner/Courses?userId=" + user.getUserID();
}

//post for /partner/deletefiles
@PostMapping("/partner/deletefiles")
public String deleteFile(@RequestParam("deletefileId") Long fileId, @RequestParam("deletefilelessonId") Long lessonId, @RequestParam("deletefilelessonCourseId") Long courseId) {
    // Your code here
    Lesson lesson = lessonService.getLessonById(lessonId);
    lessonService.deleteFile(fileId);
    UploadCourse course = courseService.getCourseById(courseId).get();
    Partner partner = partnerService.getPartnerByCourseId(course);
    User user = partner.getUser();
    return "redirect:/partnership/partner/Courses?userId=" + user.getUserID();
}

//post for /partner/editfiles
@PostMapping("/partner/editfiles")
public String editFile(@RequestParam("editfileId") Long fileId, @RequestParam("editfilelessonId") Long lessonId, @RequestParam("editfilelessonCourseId") Long courseId,
@RequestParam("editfilelessonFile")  MultipartFile lessonfile){
    // Your code here
    FileResource file = lessonService.getFileById(fileId);
    String fileName = lessonfile.getOriginalFilename();
    file.setFileName(fileName);

    if (!lessonfile.isEmpty()) {
        try {
            String blobUrl = azureBlobService.uploadToAzureBlob(lessonfile.getInputStream(), fileName);
            blobUrl = azureBlobService.generateSasUrl(blobUrl);
            file.setFileUrl(blobUrl);
            lessonService.updateFile(fileId, file);
        } catch (IOException e) {
            // Handle the IOException (e.g., log the error and/or set an error message)
            e.printStackTrace(); // Example of logging the exception
            // Optionally, add a redirect to an error page or return an error message
            return "redirect:/error";
        }
    }

    UploadCourse course = courseService.getCourseById(courseId).get();
    Partner partner = partnerService.getPartnerByCourseId(course);
    User user = partner.getUser();
    return "redirect:/partnership/partner/Courses?userId=" + user.getUserID();
}

    
    @GetMapping("/partner/viewCourseSubscriptions")
public String viewCourseSubscriptions(@RequestParam("userId") int userId, Model model) {
    User user = userRepository.findById(userId).get();
    Partner partner = partnerRepository.findByUser(user);
    
    // Fetch courses uploaded by the partner
    List<UploadCourse> courses = uploadCourseService.getCoursesByPartnerId(partner.getPartnerId());
    List<Long> courseIds = courses.stream().map(UploadCourse::getId).collect(Collectors.toList());
    
    // Fetch course subscriptions for the uploaded courses
    List<CourseSubscriptionEntity> courseSubscriptions = courseSubscriptionService.getSubscriptionsByCourseIds(courseIds);
    
    // Pass courses and course subscriptions to the view
    model.addAttribute("courses", courses);
    model.addAttribute("courseSubscriptions", courseSubscriptions);
    
    return "Partnership/viewCourseSubscriptions";
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

        // Displays the category page with the category details and its courses
        @GetMapping("/partner/category")
        public String getCategoryPage(Model model) {
            List<CategoryGroup> categories = courseService.getAllCategories();
            model.addAttribute("categories", categories);
            //get courses
            List<UploadCourse> courses = courseService.getAllCourses();
            model.addAttribute("courses", courses);

            List<Long> courseIds = courses.stream().map(UploadCourse::getId).collect(Collectors.toList());
            List<CourseCategory> courseCategories = partnerService.getCourseCategoriesByCourseIds(courseIds);
            model.addAttribute("courseCategories", courseCategories);
            
            return "Partnership/category-page";
        }

         // Subscribe to a course
    @PostMapping("/partner/subscribe/{courseId}")
    @ResponseBody
    public ResponseEntity<?> subscribeToCourse(@PathVariable Long courseId, @RequestParam("userId") int userId) {
        try {
            Optional<UploadCourse> course = uploadCourseService.getCourseById(courseId);
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            courseSubscriptionService.addpartnerSubscription(course.get(), user.getUserID());
            return ResponseEntity.ok().body(Map.of("message", "Successfully subscribed to the course."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // Unsubscribe from a course
    @PostMapping("/partner/unsubscribe/{courseId}")
    @ResponseBody
    public ResponseEntity<?> unsubscribeFromCourse(@PathVariable Long courseId, @RequestParam("userId") int userId) {
        try {
            Optional<UploadCourse> course = uploadCourseService.getCourseById(courseId);
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            courseSubscriptionService.checkPartnerUnsubscription(course.get(), user.getUserID());
            return ResponseEntity.ok().body(Map.of("message", "Successfully unsubscribed from the course."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    
    
    @GetMapping("/partner/partnerSubscriptions")
public String partnerSubscriptions(@RequestParam("userId") int userId, Model model) {
    User user = userRepository.findById(userId).get();
    
    // get all courses
    List<UploadCourse> courses = uploadCourseService.getAllCourses();
    // Pass courses and course subscriptions to the view
    model.addAttribute("courses", courses);

    //fetch courseSubscription by user id
    List<CourseSubscriptionEntity> courseSubscriptions = courseSubscriptionService.getSubscriptionsByUserId(user.getUserID());

    // Pass course subscriptions to the view
    model.addAttribute("subscriptions", courseSubscriptions);
    
    return "Partnership/partnerSubscriptions";
}




}






