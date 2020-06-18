package jvn.RentACar.service;

import jvn.RentACar.dto.soap.car.PictureInfo;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Picture;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {
    void savePictures(List<MultipartFile> multipartFiles, String path, Car car);

    void savePicturesSynchronize(List<PictureInfo> pictureInfos, String path, Car car);

    void editCarPictures(List<MultipartFile> multipartFiles, String path, Car car);

    void editCarPicturesSynchronize(List<PictureInfo> pictureInfos, String path, Car car);

    Resource loadFileAsResource(String fileName, String path);


}
