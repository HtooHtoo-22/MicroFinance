package com.microfinance.code.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    private static final Logger logger = Logger.getLogger(CloudinaryService.class.getName());
    /**
     * Uploads a file to Cloudinary and returns the URL of the uploaded file.
     *
     * @param file the file to be uploaded
     * @return the URL of the uploaded file
     * @throws IOException if the file could not be uploaded
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Optional: Check file size before uploading
        if (file.getSize() > 100 * 1024 * 1024) { // Example: Max size of 100MB
            throw new IOException("File size exceeds the allowed limit of 100MB");
        }

        try {
            // Upload the file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                    ObjectUtils.asMap("resource_type", "auto"));
            
            // Return the URL of the uploaded file
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error uploading file to Cloudinary", e);
            throw new IOException("Error uploading file to Cloudinary", e);
        } catch (Exception e) {
            // Catch other possible exceptions (e.g., Cloudinary API exceptions)
            logger.log(Level.SEVERE, "Unexpected error during file upload", e);
            throw new IOException("Unexpected error during file upload", e);
        }
    }

    /**
     * Deletes a file from Cloudinary using the public_id.
     *
     * @param publicId the public_id of the file to be deleted
     * @return a confirmation message from Cloudinary
     * @throws IOException if the file could not be deleted
     */
    public String deleteFile(String publicId) throws IOException {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return result.get("result").toString();
        } catch (IOException e) {
            throw new IOException("Error deleting file from Cloudinary", e);
        }
    }
}
