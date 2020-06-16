package jvn.Cars.serviceImpl;

import jvn.Cars.client.AdvertisementClient;
import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.enumeration.EditType;
import jvn.Cars.enumeration.LogicalStatus;
import jvn.Cars.exceptionHandler.InvalidCarDataException;
import jvn.Cars.mapper.CarDtoMapper;
import jvn.Cars.model.Car;
import jvn.Cars.producer.CarProducer;
import jvn.Cars.repository.CarRepository;
import jvn.Cars.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Value("${UPLOADED_PICTURES_PATH:uploadedPictures/}")
    private String UPLOADED_PICTURES_PATH;

    private CarRepository carRepository;

    private BodyStyleService bodyStyleService;

    private FuelTypeService fuelTypeService;

    private GearboxTypeService gearboxTypeService;

    private PictureService pictureService;

    private CarDtoMapper carMapper;

    private ModelService modelService;

    private MakeService makeService;

    private AdvertisementClient advertisementClient;

    private CarProducer carProducer;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Car create(Car car, List<MultipartFile> multipartFiles, Long userId) {
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        for (MultipartFile file : multipartFiles) {
            if (file.getSize() > 2097152) {
                throw new InvalidCarDataException("Picture exceeds maximum size of 2MB.", HttpStatus.PAYLOAD_TOO_LARGE);
            }
        }

        car.setOwner(userId);
        car.setMake(makeService.get(car.getMake().getId()));
        car.setModel(modelService.get(car.getModel().getId(), car.getMake().getId()));
        car.setBodyStyle(bodyStyleService.get(car.getBodyStyle().getId()));
        car.setFuelType(fuelTypeService.get(car.getFuelType().getId()));
        car.setGearBoxType(gearboxTypeService.get(car.getGearBoxType().getId()));
        car.setCommentsCount(0);
        car.setAvgRating(0.0);
        Car savedCar = carRepository.saveAndFlush(car);
        pictureService.savePictures(multipartFiles, UPLOADED_PICTURES_PATH, savedCar);
        return savedCar;
    }

    @Override
    public Resource get(String fileName) {
        return pictureService.loadFileAsResource(fileName, UPLOADED_PICTURES_PATH);
    }

    @Override
    public List<Car> get(UserDTO userDTO) {
        return carRepository.findAllByLogicalStatusNotAndOwner(LogicalStatus.DELETED, userDTO.getId());
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

    @Override
    public Car get(Long id, Long loggedInUser) {
        Car car = carRepository.findOneByIdAndLogicalStatusNotAndOwner(id, LogicalStatus.DELETED, loggedInUser);
        if (car == null) {
            throw new InvalidCarDataException("This car doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return car;
    }

    @Override
    public Car get(Long id, LogicalStatus logicalStatus) {
        return carRepository.findByIdAndLogicalStatus(id, logicalStatus);
    }

    @Override
    public void delete(Long id, Long loggedInUserId, String jwtToken, String user) {
        Car dbCar = get(id, LogicalStatus.EXISTING);
        checkOwner(dbCar, loggedInUserId);

        if (advertisementClient.canDeleteCar(jwtToken, user, id)) {
            dbCar.setLogicalStatus(LogicalStatus.DELETED);
            carRepository.save(dbCar);
        } else {
            throw new InvalidCarDataException("This car is in use and therefore it cannot be deleted.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car editAll(Long id, Car car, List<MultipartFile> multipartFiles, Long loggedInUserId) {

        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        Car dbCar = get(id, LogicalStatus.EXISTING);
        checkOwner(dbCar, loggedInUserId);

        if (!advertisementClient.getCarEditType(id).equals(EditType.ALL)) {
            throw new InvalidCarDataException(
                    "This car is in use and therefore it cannot be edited.", HttpStatus.BAD_REQUEST);
        }

        dbCar.setMake(makeService.get(car.getMake().getId()));
        dbCar.setModel(modelService.get(car.getModel().getId(), car.getMake().getId()));
        dbCar.setBodyStyle(bodyStyleService.get(car.getBodyStyle().getId()));
        dbCar.setFuelType(fuelTypeService.get(car.getFuelType().getId()));
        dbCar.setGearBoxType(gearboxTypeService.get(car.getGearBoxType().getId()));
        dbCar.setMileageInKm(car.getMileageInKm());
        dbCar.setKidsSeats(car.getKidsSeats());
        dbCar.setAvailableTracking(car.getAvailableTracking());

        Car newCar = carRepository.save(dbCar);
        pictureService.editCarPictures(multipartFiles, UPLOADED_PICTURES_PATH, dbCar);
        return newCar;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Car editPartial(Long id, CarEditDTO carDTO, List<MultipartFile> multipartFiles, Long loggedInUserId, String jwtToken,
                           String user, UserDTO userDTO) {
        if (multipartFiles.size() > 5) {
            throw new InvalidCarDataException("You can choose 5 pictures maximally.", HttpStatus.BAD_REQUEST);
        }
        Car dbCar = get(id, LogicalStatus.EXISTING);
        checkOwner(dbCar, loggedInUserId);

        if (advertisementClient.canEditCarPartially(jwtToken, user, id)) {
            dbCar.setMileageInKm(carDTO.getMileageInKm());
            dbCar.setKidsSeats(carDTO.getKidsSeats());
            dbCar.setAvailableTracking(carDTO.getAvailableTracking());
            Car newCar = carRepository.save(dbCar);
            pictureService.editCarPictures(multipartFiles, UPLOADED_PICTURES_PATH, dbCar);

            carDTO.setId(id);
            editCar(carDTO);

            return newCar;
        } else {
            throw new InvalidCarDataException("This car is in use and therefore it cannot be edited.", HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    public void editCar(CarEditDTO carEditDTO) {
        carProducer.sendMessageForSearch(carEditDTO);
    }

    private void checkOwner(Car car, Long loggedInUserId) {
        if (!car.getOwner().equals(loggedInUserId)) {
            throw new InvalidCarDataException("You are not the owner of this car, therefore you cannot edit or delete it.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, BodyStyleService bodyStyleService, FuelTypeService fuelTypeService,
                          GearboxTypeService gearboxTypeService, PictureService pictureService, CarDtoMapper carMapper,
                          ModelService modelService, MakeService makeService, AdvertisementClient advertisementClient,
                          CarProducer carProducer) {
        this.carRepository = carRepository;
        this.bodyStyleService = bodyStyleService;
        this.fuelTypeService = fuelTypeService;
        this.gearboxTypeService = gearboxTypeService;
        this.pictureService = pictureService;
        this.carMapper = carMapper;
        this.modelService = modelService;
        this.makeService = makeService;
        this.advertisementClient = advertisementClient;
        this.carProducer = carProducer;
    }
}
