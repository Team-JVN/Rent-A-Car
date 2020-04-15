package jvn.RentACar.service;

import jvn.RentACar.model.Car;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {
    void savePictures(List<MultipartFile> multipartFiles, String path, Car car);

    Resource loadFileAsResource(String fileName, String path);
}
