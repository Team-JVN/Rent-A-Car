package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Picture;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.service.BodyStyleService;
import jvn.RentACar.service.CarService;
import jvn.RentACar.service.FuelTypeService;
import jvn.RentACar.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService {
    private final String PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "uploadedPictures" + File.separator;

    private CarRepository carRepository;

    private BodyStyleService bodyStyleService;

    private FuelTypeService fuelTypeService;

    private GearboxTypeService gearboxTypeService;

    @Override
    public Car create(Car car, List<MultipartFile> multipartFiles) {
        //TODO: Set owner.
        car.setBodyStyle(bodyStyleService.get(car.getBodyStyle().getId()));
        car.setFuelType(fuelTypeService.get(car.getFuelType().getId()));
        car.setGearBoxType(gearboxTypeService.get(car.getGearBoxType().getId()));
        car.setPictures(savePictures(multipartFiles));
        return carRepository.save(car);
    }

    @Override
    public List<CarDTO> get() {
        return null;
    }

    @Override
    public CarDTO edit(CarDTO carDTO, List<MultipartFile> multipartFiles) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    private Set<Picture> savePictures(List<MultipartFile> multipartFiles) {
        Set<Picture> pictures = new HashSet<>();
        for (MultipartFile picture : multipartFiles) {

            pictures.add(new Picture(savePicture(picture)));
        }
        return pictures;
    }

    private String savePicture(MultipartFile picture) {
        String fileName = StringUtils.cleanPath(picture.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new InvalidCarDataException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path fileStorageLocation = Paths.get(PATH);
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(picture.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new InvalidCarDataException("Could not store file " + fileName + ". Please try again!");
        }
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, BodyStyleService bodyStyleService, FuelTypeService fuelTypeService, GearboxTypeService gearboxTypeService) {
        this.carRepository = carRepository;
        this.bodyStyleService = bodyStyleService;
        this.fuelTypeService = fuelTypeService;
        this.gearboxTypeService = gearboxTypeService;
    }
}
