package jvn.Cars.service;

import jvn.Cars.model.Car;
import jvn.Cars.model.Picture;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface PictureService {
    void savePictures(List<MultipartFile> multipartFiles, String path, Car car);

    Set<Picture> editCarPictures(List<MultipartFile> multipartFiles, String path, Car car);

    Resource loadFileAsResource(String fileName, String path);

}
