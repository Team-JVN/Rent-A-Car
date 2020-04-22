package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.dto.response.CarWithPicturesDTO;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.CarDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Picture;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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

    private CarDtoMapper carMapper;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car create(Car car, List<MultipartFile> multipartFiles) {
        //TODO: Set owner.
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
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
        List<Car> cars = carRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
        List<CarWithPicturesDTO> carDTOList = new ArrayList<>();
        for (Car car : cars) {
            CarDTO carDTO = carMapper.toDto(car);
            List<String> pictures = new ArrayList<>();
            for (Picture picture : car.getPictures()) {
                pictures.add(picture.getData());
            }
            carDTOList.add(new CarWithPicturesDTO(carDTO, pictures));
        }
        return carDTOList;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car editAll(Long id, CarDTO carDTO, List<MultipartFile> multipartFiles) {
        isEditable(id);
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        Car car = get(id);
        car.setMake(carDTO.getMake());
        car.setModel(carDTO.getModel());
        car.setBodyStyle(bodyStyleService.get(carDTO.getBodyStyle().getId()));
        car.setFuelType(fuelTypeService.get(carDTO.getFuelType().getId()));
        car.setGearBoxType(gearboxTypeService.get(carDTO.getGearBoxType().getId()));
        car.setMileageInKm(carDTO.getMileageInKm());
        car.setKidsSeats(carDTO.getKidsSeats());
        car.setAvailableTracking(carDTO.getAvailableTracking());
        Car newCar = carRepository.save(car);
        pictureService.editCarPictures(multipartFiles, PATH, car);
        return newCar;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car editPartial(Long id, CarEditDTO carDTO, List<MultipartFile> multipartFiles) {
        isEditable(id);
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        Car car = get(id);
        car.setMileageInKm(carDTO.getMileageInKm());
        car.setKidsSeats(carDTO.getKidsSeats());
        car.setAvailableTracking(carDTO.getAvailableTracking());
        Car newCar = carRepository.save(car);
        pictureService.editCarPictures(multipartFiles, PATH, car);
        return newCar;
    }

    @Override
    public void delete(Long id) {
        Car car = get(id);

        if (carRepository.findByIdAndAdvertisementsLogicalStatus(id, LogicalStatus.EXISTING) != null) {
            throw new InvalidCarDataException("Car is in use and therefore can not be deleted.", HttpStatus.FORBIDDEN);
        }
        car.setLogicalStatus(LogicalStatus.DELETED);
        carRepository.save(car);
    }

    @Override
    public EditType getEditType(Long id) {
        try {
            isEditable(id);
        } catch (InvalidCarDataException ex) {
            return EditType.PARTIAL;
        }
        return EditType.ALL;
    }

    @Override
    public Car get(Long id) {
        Car car = carRepository.findOneByIdAndLogicalStatusNot(id, LogicalStatus.DELETED);
        if (car == null) {
            throw new InvalidCarDataException("This car doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return car;
    }

    private void isEditable(Long id) {
        for (Advertisement advertisement : get(id).getAdvertisements()) {
            if (advertisement.getLogicalStatus().equals(LogicalStatus.EXISTING) && !advertisement.getRentInfos().isEmpty()) {
                throw new InvalidCarDataException("Car is in use and therefore can not be edited.", HttpStatus.FORBIDDEN);
            }
        }
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, BodyStyleService bodyStyleService,
                          FuelTypeService fuelTypeService, GearboxTypeService gearboxTypeService,
                          PictureService pictureService, CarDtoMapper carMapper) {
        this.carRepository = carRepository;
        this.bodyStyleService = bodyStyleService;
        this.fuelTypeService = fuelTypeService;
        this.gearboxTypeService = gearboxTypeService;
        this.pictureService = pictureService;
        this.carMapper = carMapper;
    }
}
