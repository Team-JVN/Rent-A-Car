package jvn.Cars.service;

import jvn.Cars.model.Car;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {
    void savePictures(List<MultipartFile> multipartFiles, String path, Car car);

    void editCarPictures(List<MultipartFile> multipartFiles, String path, Car car);

    Resource loadFileAsResource(String fileName, String path);

}
