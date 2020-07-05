package jvn.RentACar.service;

import jvn.RentACar.dto.soap.car.PictureInfo;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Picture;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface PictureService {
    void savePictures(List<MultipartFile> multipartFiles, String path, Car car);

    void savePicturesSynchronize(List<PictureInfo> pictureInfos, String path, Car car);

    Set<Picture> editCarPictures(List<MultipartFile> multipartFiles, String path, Car car);

    Set<Picture> editCarPicturesSynchronize(List<PictureInfo> pictureInfos, String path, Car car);

    Resource loadFileAsResource(String fileName, String path);


}
