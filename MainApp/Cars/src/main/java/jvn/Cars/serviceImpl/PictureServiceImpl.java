package jvn.Cars.serviceImpl;

import jvn.Cars.exceptionHandler.FileNotFoundException;
import jvn.Cars.exceptionHandler.InvalidCarDataException;
import jvn.Cars.model.Car;
import jvn.Cars.model.Picture;
import jvn.Cars.repository.PictureRepository;
import jvn.Cars.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PictureServiceImpl implements PictureService {

    private PictureRepository pictureRepository;

    @Override
    public Resource loadFileAsResource(String fileName, String path) {
        try {
            Path fileStorageLocation = Paths.get(path);
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            resource.getFilename();
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName, HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void savePictures(List<MultipartFile> multipartFiles, String path, Car car) {
        List<String> uniquePictures = new ArrayList<>();
        for (MultipartFile picture : multipartFiles) {
            String fileName = StringUtils.cleanPath(picture.getOriginalFilename());
            if (!uniquePictures.contains(fileName)) {
                uniquePictures.add(fileName);
                pictureRepository.save(new Picture(savePictureOnDisk(picture, path, car.getId()), car));
            }
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void editCarPictures(List<MultipartFile> multipartFiles, String path, Car car) {
        Set<Picture> removedPictures = car.getPictures();
        List<String> uniquePictures = new ArrayList<>();
        Long carId = car.getId();
        for (MultipartFile picture : multipartFiles) {
            String fileName = StringUtils.cleanPath(picture.getOriginalFilename());
            Picture pictureData = pictureRepository.findByDataAndCarId(fileName, carId);
            if (pictureData == null) {
                fileName = carId + "_" + fileName;
                pictureData = pictureRepository.findByDataAndCarId(fileName, carId);
                if (pictureData == null) {
                    if (!uniquePictures.contains(fileName)) {
                        uniquePictures.add(fileName);
                        pictureData = new Picture(savePictureOnDisk(picture, path, carId), car);
                        pictureRepository.save(pictureData);
                    }
                }
            } else {
                removedPictures.remove(pictureData);
            }
        }
        deleteUnusedPictures(removedPictures, path);
    }

    private String savePictureOnDisk(MultipartFile picture, String path, Long id) {
        String fileName = StringUtils.cleanPath(picture.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new InvalidCarDataException("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.BAD_REQUEST);
            }
            Path fileStorageLocation = Paths.get(path);
            Path targetLocation = fileStorageLocation.resolve(id + "_" + fileName);
            Files.copy(picture.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return id + "_" + fileName;
        } catch (IOException ex) {
            throw new InvalidCarDataException("Could not store file " + fileName + ". Please try again!", HttpStatus.BAD_REQUEST);
        }
    }

    private void deleteUnusedPictures(Set<Picture> pictureForDeleting, String path) {
        for (Picture picture : pictureForDeleting) {
            Path fileStorageLocation = Paths.get(path);
            Path filePath = fileStorageLocation.resolve(picture.getData()).normalize();
            File file = new File(String.valueOf(filePath));
            file.delete();
            pictureRepository.deleteById(picture.getId());
        }
    }

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }
}
