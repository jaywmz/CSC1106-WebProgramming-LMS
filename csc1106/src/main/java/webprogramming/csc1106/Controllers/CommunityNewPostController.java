package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.*;
import webprogramming.csc1106.Services.AzureBlobService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommunityNewPostController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private AttachmentsRepo attachmentsRepo;

    @Autowired
    private AzureBlobService azureBlobService;

    // Mapping for new post form
    @GetMapping("/community/{user_group}/new-post")
    public String getNewPostForm(@PathVariable String user_group, @Param("category_id") String category_id, Model model) {
        // retrieve category entities from db by id
        Optional<CommunityCategory> category = categoryRepo.findById(Integer.parseInt(category_id)); 

        if (!category.isPresent()) {
            System.err.println("Unrecognised category ID");
            return "redirect:/community/{user_group}";
        }

        model.addAttribute("user_group", user_group);
        model.addAttribute("category_name", category.get().getName());
        model.addAttribute("category_id", category_id);
        model.addAttribute("newPost", new Post());

        return "Community/new-post"; 
    }

    // Mapping for posting new post
    @PostMapping("/community/new-post")
    public String postNewPost(@RequestParam("category_id") String category_id, @RequestParam("postAttachment") MultipartFile attachment, @CookieValue("lrnznth_User_ID") String userID, @ModelAttribute Post newPost, RedirectAttributes redirectAttributes) {
        try {
            // check if title and content of post exceeds word limit set in DB, VARCHAR(255)
            if (newPost.getTitle().length() > 255 || newPost.getContent().length() > 255) {
                throw new Exception("Post title and content exceeds character limit");
            }

            // retrieve user entity by user id and set postee and username attribute
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            if (user.isPresent()) {
                newPost.setUser(user.get());
                newPost.setPosterName(user.get().getUserName());
            }
            else { throw new Exception("Cannot find user from user id in cookie"); }

            // set post's category
            Optional<CommunityCategory> category = categoryRepo.findById(Integer.parseInt(category_id));
            if (category.isPresent()) {
                newPost.setCategory(category.get());
            }
            else { throw new Exception("Category ID given does not corespond to any real category ID recognised in DB"); }

            // set post's timestamp
            java.util.Date date = new java.util.Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            newPost.setTimestamp(timestamp);

            // save post to repository
            postRepo.save(newPost);

            // if post has image attachment, save attachment to attachment repo
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
        
            redirectAttributes.addFlashAttribute("successMessage", "Post created successfully!");
            
            // redirect to the category page corresponding to the category that this new post is under
            String catGroup = category.get().getGroup();
            if("announcements".equals(catGroup)){
                return "redirect:/community/announcements";
            }else if("students".equals(catGroup)){
                return "redirect:/community/students/" + category_id;
            }else if("instructors".equals(catGroup)){
                return "redirect:/community/instructors/" + category_id;
            }else{
                return "redirect:/community/general/" + category_id; 
            }
        }
        catch (Exception exception) {
            System.err.println(exception);
            return "redirect:/community/new-post";
        }
    }
    
}
