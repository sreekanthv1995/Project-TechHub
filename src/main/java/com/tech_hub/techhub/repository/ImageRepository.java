package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    Image save(Image image);

}
