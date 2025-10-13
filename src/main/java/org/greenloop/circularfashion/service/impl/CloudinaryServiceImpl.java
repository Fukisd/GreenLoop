package org.greenloop.circularfashion.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        log.info("Uploading image to Cloudinary - folder: {}, filename: {}", folder, file.getOriginalFilename());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Generate unique public ID
        String publicId = UUID.randomUUID().toString();
        
        // Upload parameters
        Map<String, Object> params = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", folder != null ? folder : "greenloop/items",
                "resource_type", "image",
                "overwrite", false,
                "transformation", ObjectUtils.asMap(
                        "quality", "auto:good",
                        "fetch_format", "auto"
                )
        );
        
        try {
            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String imageUrl = (String) uploadResult.get("secure_url");
            
            log.info("Image uploaded successfully: {}", imageUrl);
            return imageUrl;
            
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files, String folder) throws IOException {
        log.info("Uploading {} images to Cloudinary", files.size());
        
        List<String> uploadedUrls = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                String url = uploadImage(file, folder);
                uploadedUrls.add(url);
            } catch (Exception e) {
                String error = String.format("Failed to upload image %d (%s): %s", 
                        i + 1, file.getOriginalFilename(), e.getMessage());
                log.error(error);
                errors.add(error);
            }
        }
        
        if (!errors.isEmpty()) {
            log.warn("Some images failed to upload: {}", errors);
        }
        
        log.info("Successfully uploaded {}/{} images", uploadedUrls.size(), files.size());
        return uploadedUrls;
    }

    @Override
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        log.info("Deleting image from Cloudinary: {}", publicId);
        
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully: {}", publicId);
            return result;
            
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        try {
            // Example URL: https://res.cloudinary.com/cloud-name/image/upload/v1234567890/folder/publicId.jpg
            // Extract: folder/publicId
            
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }
            
            String pathWithVersion = parts[1];
            
            // Remove version (v1234567890/)
            String path = pathWithVersion.replaceFirst("v\\d+/", "");
            
            // Remove file extension
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex > 0) {
                path = path.substring(0, lastDotIndex);
            }
            
            return path;
            
        } catch (Exception e) {
            log.error("Failed to extract public ID from URL: {}", imageUrl, e);
            return null;
        }
    }
}










