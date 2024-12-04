package com.weekly_bump.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    // Method to upload files to the specified path
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(uploadPath + File.separator + fileName);

        destination.getParentFile().mkdirs();  // Ensure directories exist
        file.transferTo(destination);

        return fileName;  // Return the filename for storage in DB
    }
}
