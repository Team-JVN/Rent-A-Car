package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.both.CarWithPicturesDTO;
import jvn.RentACar.mapper.CarMapperImpl;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Picture;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    private final String PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "uploadedPictures" + File.separator;

    private CarRepository carRepository;

    private BodyStyleService bodyStyleService;

    private FuelTypeService fuelTypeService;

    private GearboxTypeService gearboxTypeService;

    private PictureService pictureService;

    private CarMapperImpl carMapper;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car create(Car car, List<MultipartFile> multipartFiles) {
        //TODO: Set owner.
        car.setBodyStyle(bodyStyleService.get(car.getBodyStyle().getId()));
        car.setFuelType(fuelTypeService.get(car.getFuelType().getId()));
        car.setGearBoxType(gearboxTypeService.get(car.getGearBoxType().getId()));
        Car savedCar = carRepository.saveAndFlush(car);
        pictureService.savePictures(multipartFiles, PATH, savedCar);
        return savedCar;
    }

    @Override
    public Resource get(String fileName) {
        return pictureService.loadFileAsResource(fileName, PATH);
    }

    @Override
    public List<CarWithPicturesDTO> get() {
        List<Car> cars = carRepository.findAll();
        List<CarWithPicturesDTO> carDTOList = new ArrayList<>();
        for (Car car : cars) {
            CarDTO carDTO = carMapper.convertToCarDto(car);
            List<String> pictures = new ArrayList<>();
            for (Picture picture : car.getPictures()) {
                pictures.add(picture.getData());
            }
            carDTOList.add(new CarWithPicturesDTO(carDTO, pictures));
        }
        return carDTOList;
    }


    @Override
    public CarDTO edit(CarDTO carDTO, List<MultipartFile> multipartFiles) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, BodyStyleService bodyStyleService,
                          FuelTypeService fuelTypeService, GearboxTypeService gearboxTypeService,
                          PictureService pictureService, CarMapperImpl carMapper) {
        this.carRepository = carRepository;
        this.bodyStyleService = bodyStyleService;
        this.fuelTypeService = fuelTypeService;
        this.gearboxTypeService = gearboxTypeService;
        this.pictureService = pictureService;
        this.carMapper = carMapper;
    }
}
