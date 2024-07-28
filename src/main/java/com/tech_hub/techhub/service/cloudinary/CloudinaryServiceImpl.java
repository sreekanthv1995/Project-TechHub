package com.tech_hub.techhub.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService{

    @Resource
    private Cloudinary cloudinary;

    public Map<?,?> uploadImage(MultipartFile file, String folderName){
        try{
            HashMap<Object,Object> options = new HashMap<>();
            options.put("folder",folderName);
            return cloudinary.uploader().upload(file.getBytes(),options);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public Map<?,?> delete(String imageId) throws IOException {
        return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    }
}
