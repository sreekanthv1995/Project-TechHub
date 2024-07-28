package com.tech_hub.techhub.service.image;

import com.tech_hub.techhub.entity.Image;

import java.util.Optional;

public interface ImageService {

    void saveImage(Image images);
    void deleteImageById(Long id);
    Optional<Image> getImageById(Long id);
}
