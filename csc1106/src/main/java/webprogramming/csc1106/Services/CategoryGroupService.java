package webprogramming.csc1106.Services;

// Import necessary packages and classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Repositories.CategoryGroupRepository;

import java.io.IOException;
import java.util.List;

@Service // Indicate that this class is a Spring service component
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepository; // Inject the CategoryGroupRepository

    @Autowired
    private AzureBlobService azureBlobService; // Inject the AzureBlobService

    // Method to add a new CategoryGroup with an optional cover image
    public void addCategoryGroup(CategoryGroup categoryGroup, MultipartFile coverImageFile) throws IOException {
        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            String contentType = coverImageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type for cover image. Only images are allowed.");
            }
            String fileName = coverImageFile.getOriginalFilename();
            String coverImageUrl = azureBlobService.uploadToAzureBlob(coverImageFile.getInputStream(), fileName);
            coverImageUrl = azureBlobService.generateSasUrl(coverImageUrl);
            categoryGroup.setCoverImageUrl(coverImageUrl);
        }
        categoryGroupRepository.save(categoryGroup); // Save the CategoryGroup entity
    }

    // Method to retrieve all CategoryGroups and calculate course counts for each
    public List<CategoryGroup> getAllCategoryGroups() {
        List<CategoryGroup> categoryGroups = categoryGroupRepository.findAll();
        for (CategoryGroup categoryGroup : categoryGroups) {
            categoryGroup.calculateCourseCount(); // Calculate course count for each CategoryGroup
        }
        return categoryGroups;
    }

    // Method to delete a CategoryGroup by its ID
    public void deleteCategoryGroup(Long id) {
        CategoryGroup categoryGroup = categoryGroupRepository.findById(id).orElse(null);
        if (categoryGroup != null && categoryGroup.getCoverImageUrl() != null) {
            azureBlobService.deleteBlob(categoryGroup.getCoverImageUrl()); // Delete the associated cover image blob
        }
        categoryGroupRepository.deleteById(id); // Delete the CategoryGroup entity
    }

    // Method to retrieve a CategoryGroup by its ID and calculate its course count
    public CategoryGroup getCategoryGroupById(Long id) {
        CategoryGroup categoryGroup = categoryGroupRepository.findById(id).orElse(null);
        if (categoryGroup != null) {
            categoryGroup.calculateCourseCount(); // Calculate course count for the CategoryGroup
        }
        return categoryGroup;
    }
}
