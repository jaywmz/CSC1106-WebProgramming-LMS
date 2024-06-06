package webprogramming.csc1106.Controllers;

import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import webprogramming.csc1106.Entities.CommunityCategory;
import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Entities.PostAttachments;
import webprogramming.csc1106.Repositories.AttachmentsRepo;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.PostRepo;
import webprogramming.csc1106.Services.AzureBlobService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CommunityNewPostController {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private AttachmentsRepo attachmentsRepo;

    @Autowired
    private AzureBlobService azureBlobService;

    @GetMapping("/community/new-post")
    public String getNewPostForm(@Param("category_name") String category_name, Model model) {
        model.addAttribute("category_name", category_name);
        model.addAttribute("newPost", new Post());
        return "Community/new-post"; 
    }

    @PostMapping("/community/new-post")
    public String postNewPost(@Param("category_name") String category_name, @RequestParam("postAttachment") MultipartFile attachment, @ModelAttribute Post newPost) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newPost.setTimestamp(timestamp);
        newPost.setPosterName("Example Name"); // placeholder
        
        CommunityCategory category = categoryRepo.findByName(category_name);
        newPost.setCategory(category);
        postRepo.save(newPost);

        try {
            if (attachment != null && !attachment.isEmpty()) {
                String contentType = attachment.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new RuntimeException("Invalid file type for cover image. Only images are allowed.");
                }
                String attachmentUrl = azureBlobService.uploadToAzureBlob(attachment.getInputStream(), attachment.getOriginalFilename());
                attachmentUrl = azureBlobService.generateSasUrl(attachmentUrl);
                PostAttachments newAttachment = new PostAttachments();
                newAttachment.setPost(newPost);
                newAttachment.setURI(attachmentUrl);
                attachmentsRepo.save(newAttachment);
            }
        } catch (IOException exception) {
            return "Community/new-post";
        }

        return "redirect:/community/" + category_name; 
    }
    
}