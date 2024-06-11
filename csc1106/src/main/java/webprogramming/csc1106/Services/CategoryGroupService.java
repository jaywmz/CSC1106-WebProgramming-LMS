package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Repositories.CategoryGroupRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    @Autowired
    private AzureBlobService azureBlobService;

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
        categoryGroupRepository.save(categoryGroup);
    }

    public List<CategoryGroup> getAllCategoryGroups() {
        return categoryGroupRepository.findAll();
    }

    public void deleteCategoryGroup(Long id) {
        CategoryGroup categoryGroup = categoryGroupRepository.findById(id).orElse(null);
        if (categoryGroup != null && categoryGroup.getCoverImageUrl() != null) {
            azureBlobService.deleteBlob(categoryGroup.getCoverImageUrl());
        }
        categoryGroupRepository.deleteById(id);
    }

    public CategoryGroup getCategoryGroupById(Long id) {
        return categoryGroupRepository.findById(id).orElse(null);
    }
}
