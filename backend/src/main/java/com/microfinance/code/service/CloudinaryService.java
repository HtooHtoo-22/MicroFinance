package com.microfinance.code.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    private static final Logger logger = Logger.getLogger(CloudinaryService.class.getName());

    /**
     * Uploads a file to Cloudinary asynchronously and returns the secure URL.
     *
     * @param file the file to be uploaded
     * @return a CompletableFuture containing the secure URL of the uploaded file
     * @throws IOException if the file could not be uploaded
     */
    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file) throws IOException {
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IOException("File size exceeds the allowed limit of 100MB");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

            String uploadedUrl = uploadResult.get("secure_url").toString();
            logger.log(Level.INFO, "File uploaded successfully: " + uploadedUrl);

            return CompletableFuture.completedFuture(uploadedUrl);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error uploading file to Cloudinary", e);
            throw new IOException("Error uploading file to Cloudinary", e);
        } catch (Exception e) {
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
        if (publicId == null || publicId.isEmpty()) {
            throw new IOException("Invalid public ID for deletion");
        }

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            String deleteStatus = result.get("result").toString();
            logger.log(Level.INFO, "File deletion status: " + deleteStatus);

            return deleteStatus;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deleting file from Cloudinary", e);
            throw new IOException("Error deleting file from Cloudinary", e);
        }
    }
}