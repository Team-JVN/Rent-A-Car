package jvn.Cars.serviceImpl;

import jvn.Cars.dto.message.Log;
import jvn.Cars.exceptionHandler.FileNotFoundException;
import jvn.Cars.exceptionHandler.InvalidCarDataException;
import jvn.Cars.model.Car;
import jvn.Cars.model.Picture;
import jvn.Cars.producer.LogProducer;
import jvn.Cars.repository.CarRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PictureServiceImpl implements PictureService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private LogProducer logProducer;

    private PictureRepository pictureRepository;

    private CarRepository carRepository;

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
                logProducer.send(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                        String.format("Picture \"%s\" not found on server", fileName)));
                throw new FileNotFoundException("File not found " + fileName, HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException ex) {
            logProducer.send(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                    String.format("Invalid path to picture \"%s\"", fileName)));
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
                Picture pic = pictureRepository.save(new Picture(savePictureOnDisk(picture, path, car.getId()), car));
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                        String.format("Picture %s successfully saved in DB", pic.getId())));
            }
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public Set<Picture> editCarPictures(List<MultipartFile> multipartFiles, String path, Car car) {
        Set<Picture> removedPictures = car.getPictures();
        Set<Picture> carPictures = new HashSet<>();
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
                        Picture pic = pictureRepository.saveAndFlush(pictureData);
                        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                                String.format("Picture %s successfully saved in DB", pic.getId())));
                        carPictures.add(pic);
                    }
                } else {
                    carPictures.add(pictureData);
                    removedPictures.remove(pictureData);
                }
            } else {
                carPictures.add(pictureData);
                removedPictures.remove(pictureData);
            }
        }
        car.setPictures(carPictures);
        carRepository.saveAndFlush(car);
        deleteUnusedPictures(removedPictures, path);
        return carPictures;
    }

    private String savePictureOnDisk(MultipartFile picture, String path, Long id) {
        String fileName = StringUtils.cleanPath(picture.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                logProducer.send(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                        String.format("Invalid path to picture \"%s_%s\"", id, fileName)));
                throw new InvalidCarDataException("Filename contains invalid path sequence " + fileName,
                        HttpStatus.BAD_REQUEST);
            }
            Path fileStorageLocation = Paths.get(path);
            Path targetLocation = fileStorageLocation.resolve(id + "_" + fileName);
            Files.copy(picture.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDI",
                    String.format("Picture \"%s_%s\" successfully saved on server", id, fileName)));
            return id + "_" + fileName;
        } catch (IOException ex) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                    String.format("Picture \"%s_%s\" could not be stored on server", id, fileName)));
            throw new InvalidCarDataException("Could not store file " + fileName + ". Please try again!",
                    HttpStatus.BAD_REQUEST);
        }
    }


    private void deleteUnusedPictures(Set<Picture> pictureForDeleting, String path) {
        for (Picture picture : pictureForDeleting) {
            Path fileStorageLocation = Paths.get(path);
            Path filePath = fileStorageLocation.resolve(picture.getData()).normalize();
            File file = new File(String.valueOf(filePath));
            if (file.delete()) {
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDI",
                        String.format("Picture \"%s\" successfully deleted from server", file.getName())));
            } else {
                logProducer.send(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                        String.format("Picture \"%s\" not found on server", file.getName())));
            }

            picture.setCar(null);
            pictureRepository.delete(picture);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                    String.format("Picture %s successfully deleted from DB", picture.getId())));
        }
    }

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, LogProducer logProducer, CarRepository carRepository) {
        this.pictureRepository = pictureRepository;
        this.logProducer = logProducer;
        this.carRepository = carRepository;
    }
}
