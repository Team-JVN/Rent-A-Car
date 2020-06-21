package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.soap.car.PictureInfo;
import jvn.RentACar.exceptionHandler.FileNotFoundException;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.Picture;
import jvn.RentACar.repository.PictureRepository;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
public class PictureServiceImpl implements PictureService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private LogService logService;

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
                logService.write(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                        String.format("Picture \"%s\" not found on server", fileName)));
                throw new FileNotFoundException("File not found " + fileName, HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException ex) {
            logService.write(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                    String.format("Invalid path to picture \"%s\"", fileName)));
            throw new FileNotFoundException("File not found " + fileName, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void savePictures(List<MultipartFile> multipartFiles, String path, Car car) {
        List<String> uniquePictures = new ArrayList<>();
        for (MultipartFile picture : multipartFiles) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));
            if (!uniquePictures.contains(fileName)) {
                uniquePictures.add(fileName);
                Picture pic = pictureRepository
                        .saveAndFlush(new Picture(savePictureOnDisk(picture, path, car.getId()), car));
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                        String.format("Picture %s successfully saved in DB", pic.getId())));
            }
        }
    }

    @Override
    public void savePicturesSynchronize(List<PictureInfo> pictureInfos, String path, Car car) {
        List<String> uniquePictures = new ArrayList<>();
        for (PictureInfo picture : pictureInfos) {
            String fileName = StringUtils.cleanPath(picture.getFileName());
            if (!uniquePictures.contains(fileName)) {
                uniquePictures.add(fileName);
                Picture dbPicture = pictureRepository
                        .saveAndFlush(new Picture(savePictureOnDiskSynchronize(picture.getMultiPartFile(), path,
                                car.getId(), picture.getFileName()), car));
                pictureRepository.refresh(dbPicture);
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                        String.format("Picture %s successfully saved in DB", dbPicture.getId())));
            }
        }
    }

    @Override
    public void editCarPictures(List<MultipartFile> multipartFiles, String path, Car car) {
        Set<Picture> removedPictures = car.getPictures();
        List<String> uniquePictures = new ArrayList<>();
        Long carId = car.getId();
        for (MultipartFile picture : multipartFiles) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));
            Picture pictureData = pictureRepository.findByDataAndCarId(fileName, carId);
            if (pictureData == null) {
                fileName = carId + "_" + fileName;
                pictureData = pictureRepository.findByDataAndCarId(fileName, carId);
                if (pictureData == null) {
                    if (!uniquePictures.contains(fileName)) {
                        uniquePictures.add(fileName);
                        pictureData = new Picture(savePictureOnDisk(picture, path, carId), car);
                        Picture pic = pictureRepository.saveAndFlush(pictureData);
                        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                                String.format("Picture %s successfully saved in DB", pic.getId())));
                    }
                }
            } else {
                removedPictures.remove(pictureData);
            }
        }
        deleteUnusedPictures(removedPictures, path);
    }

    @Override
    public void editCarPicturesSynchronize(List<PictureInfo> pictureInfos, String path, Car car) {
        Set<Picture> removedPictures = car.getPictures();
        List<String> uniquePictures = new ArrayList<>();
        Long carId = car.getId();
        for (PictureInfo picture : pictureInfos) {
            String fileName = StringUtils.cleanPath(picture.getFileName());
            Picture pictureData = pictureRepository.findByDataAndCarId(fileName, carId);
            if (pictureData == null) {
                fileName = carId + "_" + fileName;
                pictureData = pictureRepository.findByDataAndCarId(fileName, carId);
                if (pictureData == null) {
                    if (!uniquePictures.contains(fileName)) {
                        uniquePictures.add(fileName);
                        pictureData = new Picture(savePictureOnDiskSynchronize(picture.getMultiPartFile(), path,
                                car.getId(), picture.getFileName()), car);
                        Picture dbPicture = pictureRepository.saveAndFlush(pictureData);
                        pictureRepository.refresh(dbPicture);
                        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                                String.format("Picture %s successfully saved in DB", dbPicture.getId())));
                    }
                } else {
                    removedPictures.remove(pictureData);
                }
            } else {
                removedPictures.remove(pictureData);
            }
        }
        deleteUnusedPictures(removedPictures, path);
    }

    private String savePictureOnDisk(MultipartFile picture, String path, Long id) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new InvalidCarDataException("Sorry! Filename contains invalid path sequence " + fileName,
                        HttpStatus.BAD_REQUEST);
            }
            Path fileStorageLocation = Paths.get(path);
            Path targetLocation = fileStorageLocation.resolve(id + "_" + fileName);
            Files.copy(picture.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDI",
                    String.format("Picture \"%s_%s\" successfully saved on server", id, fileName)));
            return id + "_" + fileName;
        } catch (IOException ex) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                    String.format("Picture \"%s_%s\" could not be stored on server", id, fileName)));
            throw new InvalidCarDataException("Could not store file " + fileName + ". Please try again!",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private String savePictureOnDiskSynchronize(byte[] bytes, String path, Long id, String name) {
        String fileName = StringUtils.cleanPath(name);
        try {
            if (fileName.contains("..")) {
                throw new InvalidCarDataException("Sorry! Filename contains invalid path sequence " + fileName,
                        HttpStatus.BAD_REQUEST);
            }
            Path fileStorageLocation = Paths.get(path);
            Path targetLocation = fileStorageLocation.resolve(id + "_" + fileName);
            Files.copy(new ByteArrayInputStream(bytes), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDI",
                    String.format("Picture \"%s_%s\" successfully saved on server", id, fileName)));
            return id + "_" + fileName;
        } catch (IOException ex) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
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
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDI",
                        String.format("Picture \"%s\" successfully deleted from server", file.getName())));
            } else {
                logService.write(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PEX",
                        String.format("Picture \"%s\" not found on server", file.getName())));
            }
            Long picId = picture.getId();
            picture.setCar(null);
            pictureRepository.save(picture);
            pictureRepository.deleteById(picId);
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PDB",
                    String.format("Picture %s successfully deleted from DB", picId)));
        }
    }

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, LogService logService) {
        this.pictureRepository = pictureRepository;
        this.logService = logService;
    }
}
