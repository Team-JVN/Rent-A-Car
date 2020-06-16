package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.CarClient;
import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.dto.soap.car.CreateOrEditCarDetailsResponse;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.CarDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService {

    @Value("${UPLOADED_PICTURES_PATH:src/main/resources/uploadedPictures/}")
    private String UPLOADED_PICTURES_PATH;

    private CarRepository carRepository;

    private BodyStyleService bodyStyleService;

    private FuelTypeService fuelTypeService;

    private GearboxTypeService gearboxTypeService;

    private PictureService pictureService;

    private CarDtoMapper carMapper;

    private ModelService modelService;

    private MakeService makeService;

    private UserService userService;

    private CarClient carClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Car create(Car car, List<MultipartFile> multipartFiles) {
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        for (MultipartFile file : multipartFiles) {
            if (file.getSize() > 2097152) {
                throw new InvalidCarDataException("Picture exceeds maximum size of 2MB.", HttpStatus.PAYLOAD_TOO_LARGE);
            }
        }
        car.setOwner(userService.getLoginUser());
        car.setMake(makeService.get(car.getMake().getId()));
        car.setModel(modelService.get(car.getModel().getId(), car.getMake().getId()));
        car.setBodyStyle(bodyStyleService.get(car.getBodyStyle().getId()));
        car.setFuelType(fuelTypeService.get(car.getFuelType().getId()));
        car.setGearBoxType(gearboxTypeService.get(car.getGearBoxType().getId()));
        car.setCommentsCount(0);
        car.setAvgRating(0.0);
        car.setMainAppId(saveInMainApp(car,multipartFiles));
        Car savedCar = carRepository.saveAndFlush(car);
        pictureService.savePictures(multipartFiles, UPLOADED_PICTURES_PATH, savedCar);
        return savedCar;
    }

    @Override
    public Resource get(String fileName) {
        return pictureService.loadFileAsResource(fileName, UPLOADED_PICTURES_PATH);
    }

    @Override
    public List<Car> get() {
        return carRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car editAll(Long id, CarDTO carDTO, List<MultipartFile> multipartFiles) {

        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        Car car = get(id);
        checkOwner(car);
        Set<Advertisement> advertisements = get(id).getAdvertisements();
        if (advertisements != null && !advertisements.isEmpty()) {
            throw new InvalidCarDataException("Car is in use and therefore can not be edited.", HttpStatus.BAD_REQUEST);
        }
        car.setMake(makeService.get(carDTO.getMake().getId()));
        car.setModel(modelService.get(carDTO.getModel().getId(), carDTO.getMake().getId()));
        car.setBodyStyle(bodyStyleService.get(carDTO.getBodyStyle().getId()));
        car.setFuelType(fuelTypeService.get(carDTO.getFuelType().getId()));
        car.setGearBoxType(gearboxTypeService.get(carDTO.getGearBoxType().getId()));
        car.setMileageInKm(carDTO.getMileageInKm());
        car.setKidsSeats(carDTO.getKidsSeats());
        car.setAvailableTracking(carDTO.getAvailableTracking());
        Car newCar = carRepository.save(car);
        try {
            carClient.createOrEdit(car,multipartFiles);
        }catch (Exception e){
            e.printStackTrace();
        }
        pictureService.editCarPictures(multipartFiles, UPLOADED_PICTURES_PATH, car);
        return newCar;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car editPartial(Long id, CarEditDTO carDTO, List<MultipartFile> multipartFiles) {
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        Car car = get(id);
        checkOwner(car);
        for (Advertisement advertisement : get(id).getAdvertisements()) {
            if (advertisement.getLogicalStatus().equals(LogicalStatus.EXISTING) && !advertisement.getRentInfos().isEmpty()) {
                throw new InvalidCarDataException("Car is in use and therefore can not be edited.", HttpStatus.BAD_REQUEST);
            }
        }
        car.setMileageInKm(carDTO.getMileageInKm());
        car.setKidsSeats(carDTO.getKidsSeats());
        car.setAvailableTracking(carDTO.getAvailableTracking());
        Car newCar = carRepository.save(car);
        pictureService.editCarPictures(multipartFiles, UPLOADED_PICTURES_PATH, car);
        return newCar;
    }

    @Override
    public void delete(Long id) {
        Car car = get(id);
        checkOwner(car);
        if (carRepository.findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToGreaterThanEqual(id, LogicalStatus.EXISTING, LocalDate.now()) != null) {
            throw new InvalidCarDataException("Car is in use and therefore can not be deleted.", HttpStatus.BAD_REQUEST);
        }

        if (carRepository.findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToEquals(id, LogicalStatus.EXISTING, null) != null) {
            throw new InvalidCarDataException("Car is in use and therefore can not be deleted.", HttpStatus.BAD_REQUEST);
        }
        car.setLogicalStatus(LogicalStatus.DELETED);
        carRepository.save(car);
    }

    @Override
    public EditType getEditType(Long id) {
        Set<Advertisement> advertisements = get(id).getAdvertisements();
        if (advertisements == null || advertisements.isEmpty()) {
            return EditType.ALL;
        }
        return EditType.PARTIAL;
    }

    @Override
    public Car get(Long id) {
        Car car = carRepository.findOneByIdAndLogicalStatusNot(id, LogicalStatus.DELETED);
        if (car == null) {
            throw new InvalidCarDataException("This car doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return car;
    }

    @Override
    public List<Car> getStatistics(String filter) {
        String sortFilter = "";
        switch (filter) {
            case "most-km-made":
                sortFilter = "mileageInKm";
                break;
            case "best-rated":
                sortFilter = "avgRating";
                break;
            case "most-commented":
                sortFilter = "commentsCount";
                break;
        }

        return carRepository.findFirst3ByLogicalStatus(LogicalStatus.EXISTING, Sort.by(Sort.Direction.DESC, sortFilter));
    }

    private Long saveInMainApp(Car car,List<MultipartFile> multipartFiles){
        try{
            CreateOrEditCarDetailsResponse response= carClient.createOrEdit(car,multipartFiles);
            car.setMainAppId(response.getCreateCarDetails().getId());
            carRepository.saveAndFlush(car);
            return response.getCreateCarDetails().getId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private void checkOwner(Car car) {
        if (!userService.getLoginAgent().getEmail().equals(car.getOwner().getEmail())) {
            throw new InvalidCarDataException("You are not owner of this car.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, BodyStyleService bodyStyleService,
                          FuelTypeService fuelTypeService, GearboxTypeService gearboxTypeService,
                          PictureService pictureService, CarDtoMapper carMapper, ModelService modelService, MakeService makeService,
                          UserService userService,CarClient carClient) {
        this.carRepository = carRepository;
        this.bodyStyleService = bodyStyleService;
        this.fuelTypeService = fuelTypeService;
        this.gearboxTypeService = gearboxTypeService;
        this.pictureService = pictureService;
        this.carMapper = carMapper;
        this.modelService = modelService;
        this.makeService = makeService;
        this.userService = userService;
        this.carClient = carClient;
    }
}
