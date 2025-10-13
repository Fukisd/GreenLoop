package org.greenloop.circularfashion.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloudinaryService {
    
    /**
     * Upload a single image to Cloudinary
     * @param file The image file to upload
     * @param folder Optional folder name in Cloudinary
     * @return URL of the uploaded image
     */
    String uploadImage(MultipartFile file, String folder) throws IOException;
    
    /**
     * Upload multiple images to Cloudinary
     * @param files List of image files to upload
     * @param folder Optional folder name in Cloudinary
     * @return List of URLs of uploaded images
     */
    List<String> uploadImages(List<MultipartFile> files, String folder) throws IOException;
    
    /**
     * Delete an image from Cloudinary
     * @param publicId The public ID of the image to delete
     * @return Result map from Cloudinary
     */
    Map<String, Object> deleteImage(String publicId) throws IOException;
    
    /**
     * Extract public ID from Cloudinary URL
     * @param imageUrl The Cloudinary image URL
     * @return Public ID of the image
     */
    String extractPublicId(String imageUrl);
}










