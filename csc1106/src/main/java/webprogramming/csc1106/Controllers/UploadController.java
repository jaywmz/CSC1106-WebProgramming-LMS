package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Services.UploadCourseService;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Controller
public class UploadController {
    @Autowired
    private UploadCourseService courseService;

    @GetMapping("/coursesupload")
    public String showCoursesPage(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "coursesupload";
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        model.addAttribute("course", new UploadCourse());
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadCourse(@ModelAttribute UploadCourse course, @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                // Save course without file
                courseService.addCourse(course);
            } else {
                // Save course with file
                courseService.addCourseWithFile(course, file.getInputStream(), file.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to upload the file. Please try again.");
            return "upload";
        }
        model.addAttribute("courses", courseService.getAllCourses());
        return "redirect:/coursesupload";
    }

    @GetMapping("/serveFile")
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam("courseId") Long courseId, @RequestParam("disposition") String disposition) throws IOException {
        // Fetch course metadata from MySQL
        UploadCourse course = courseService.getCourseById(courseId);
        // Retrieve the file from Azure Blob Storage using the blob name with SAS
        InputStream fileInputStream = courseService.downloadFileWithSas(course.getBlobName());
        System.out.println((disposition.equals("inline") ? "Viewing" : "Downloading") + " file: " + course.getBlobName());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + course.getBlobName() + "\"");
        headers.setContentType(getMediaTypeForFileName(course.getBlobName()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/view")
    public String viewFile(@RequestParam("courseId") Long courseId) {
        return "redirect:/serveFile?courseId=" + courseId + "&disposition=inline";
    }

    @GetMapping("/download")
    public String downloadFile(@RequestParam("courseId") Long courseId) {
        return "redirect:/serveFile?courseId=" + courseId + "&disposition=attachment";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteCourse(@RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the course. Please try again.");
            e.printStackTrace();
        }
        return "redirect:/coursesupload";
    }

    private MediaType getMediaTypeForFileName(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return MediaType.parseMediaType(mimeType);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "File size exceeds limit! Please upload a smaller file.");
        return "redirect:/upload";
    }
}
