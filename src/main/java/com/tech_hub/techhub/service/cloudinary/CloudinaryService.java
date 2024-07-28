package com.tech_hub.techhub.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    public Map<?,?> uploadImage(MultipartFile file, String folderName);
    public Map<?,?> delete(String imageId) throws IOException;
}
