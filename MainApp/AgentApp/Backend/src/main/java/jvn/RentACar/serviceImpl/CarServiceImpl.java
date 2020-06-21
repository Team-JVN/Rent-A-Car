package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.CarClient;
import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.dto.soap.car.*;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.CarDetailsMapper;
import jvn.RentACar.mapper.CarDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.Picture;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CarServiceImpl implements CarService {

    @Value("${UPLOADED_PICTURES_PATH:src/main/resources/uploadedPictures/}")
    private String UPLOADED_PICTURES_PATH;

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

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

    private CarDetailsMapper carDetailsMapper;

    private LogService logService;

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
        Car savedCar = carRepository.saveAndFlush(car);
        pictureService.savePictures(multipartFiles, UPLOADED_PICTURES_PATH, savedCar);
        car.setMainAppId(saveInMainApp(car, multipartFiles));
        savedCar = carRepository.saveAndFlush(car);
        return savedCar;
    }

    @Override
    public Resource get(String fileName) {
        return pictureService.loadFileAsResource(fileName, UPLOADED_PICTURES_PATH);
    }

    @Override
    public List<Car> get() {
        synchronizeCars();
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
        if (!getEditType(car.getMainAppId()).equals(EditType.ALL)) {
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
            carClient.createOrEdit(car, multipartFiles);
        } catch (Exception e) {
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

        carDTO.setId(newCar.getMainAppId());
        carClient.editPartial(carDTO, multipartFiles);
        pictureService.editCarPictures(multipartFiles, UPLOADED_PICTURES_PATH, car);
        return newCar;
    }

    @Override
    public void delete(Long id) {
        Car car = get(id);
        checkOwner(car);

        DeleteCarDetailsResponse response = carClient.checkAndDeleteIfCan(car);
        if (response == null || !response.isCanDelete()) {
            throw new InvalidCarDataException("Car is in use and therefore can not be deleted.", HttpStatus.BAD_REQUEST);
        }

        car.setLogicalStatus(LogicalStatus.DELETED);
        carRepository.save(car);
    }

    @Override
    public EditType getEditType(Long id) {
        Car car = get(id);
        GetCarEditTypeResponse response = carClient.getEditType(car);
        if (response != null) {
            if (response.getEditType().equals("ALL")) {
                return EditType.ALL;
            }
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
    public Car getByMainAppId(Long id) {
        return carRepository.findByMainAppId(id);
    }

    @Override
    public List<Car> getStatistics(String filter) {
        synchronizeCars();
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

    private Long saveInMainApp(Car car, List<MultipartFile> multipartFiles) {
        try {
            CreateOrEditCarDetailsResponse response = carClient.createOrEdit(car, multipartFiles);
            return response.getCreateCarDetails().getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkOwner(Car car) {
        if (!userService.getLoginAgent().getEmail().equals(car.getOwner().getEmail())) {
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CHO", String.format("User %s is not the owner of car %s", userService.getLoginUser().getId(), car.getId())));
            throw new InvalidCarDataException("You are not owner of this car.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Scheduled(cron = "0 10 0/3 * * ?")
    public void synchronizeCars() {
        try {
            GetAllCarDetailsResponse response = carClient.getAll();
            if (response == null) {
                return;
            }
            List<CarWithPictures> carWithPictures = response.getCarWithPictures();
            if (carWithPictures == null || carWithPictures.isEmpty()) {
                return;
            }

            for (CarWithPictures carWithPicture : carWithPictures) {
                Car car = carDetailsMapper.toEntity(carWithPicture.getCreateCarDetails());
                List<PictureInfo> pictureInfos = carWithPicture.getPictureInfo();
                Car dbCar = carRepository.findByMainAppId(car.getMainAppId());
                if (dbCar == null) {
                    createCarSynchronize(car, pictureInfos);
                } else {
                    editCarSynchronize(car, dbCar, pictureInfos);
                }

            }
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN", "[SOAP] Cars are successfully synchronized"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createCarSynchronize(Car car, List<PictureInfo> pictureInfos) {
        if (pictureInfos.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        car.setOwner(userService.getLoginUser());
        Car savedCar = carRepository.saveAndFlush(car);
        pictureService.savePicturesSynchronize(pictureInfos, UPLOADED_PICTURES_PATH, savedCar);
        savedCar = carRepository.saveAndFlush(savedCar);
        carRepository.refresh(savedCar);
    }

    private void editCarSynchronize(Car car, Car dbCar, List<PictureInfo> pictureInfos) {

        if (pictureInfos.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        if (!car.getMake().getName().equals("Other")) {
            dbCar.setMake(car.getMake());
            dbCar.setModel(car.getModel());
        }
        if (!car.getBodyStyle().getName().equals("Other")) {
            dbCar.setBodyStyle(car.getBodyStyle());
        }
        if (!car.getFuelType().getName().equals("Other")) {
            dbCar.setFuelType(car.getFuelType());
        }
        if (!car.getGearBoxType().getName().equals("Other")) {
            dbCar.setGearBoxType(car.getGearBoxType());
        }
        dbCar.setMileageInKm(car.getMileageInKm());
        dbCar.setKidsSeats(car.getKidsSeats());
        dbCar.setAvailableTracking(car.getAvailableTracking());
        dbCar.setAvgRating(car.getAvgRating());
        dbCar.setCommentsCount(car.getCommentsCount());
        Car newCar = carRepository.saveAndFlush(dbCar);
        if (!pictureInfos.isEmpty()) {
            pictureService.editCarPicturesSynchronize(pictureInfos, UPLOADED_PICTURES_PATH, newCar);
        }
        newCar = carRepository.saveAndFlush(newCar);
        carRepository.refresh(newCar);
    }


    @Autowired
    public CarServiceImpl(CarRepository carRepository, BodyStyleService bodyStyleService,
                          FuelTypeService fuelTypeService, GearboxTypeService gearboxTypeService,
                          PictureService pictureService, CarDtoMapper carMapper, ModelService modelService, MakeService makeService,
                          UserService userService, CarClient carClient, CarDetailsMapper carDetailsMapper, LogService logService) {
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
        this.carDetailsMapper = carDetailsMapper;
        this.logService = logService;
    }

}
