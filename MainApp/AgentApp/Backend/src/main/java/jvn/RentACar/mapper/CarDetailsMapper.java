package jvn.RentACar.mapper;

import jvn.RentACar.dto.request.CreateBodyStyleDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.dto.soap.car.CarDetails;
import jvn.RentACar.model.*;
import jvn.RentACar.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CarDetailsMapper implements MapperInterface<Car, CarDetails> {

    private ModelMapper modelMapper;

    private MakeService makeService;

    private ModelService modelService;

    private BodyStyleService bodyStyleService;

    private GearboxTypeService gearboxTypeService;

    private FuelTypeService fuelTypeService;

    @Override
    public Car toEntity(CarDetails dto) {
        Car car = new Car();
        car.setMainAppId(dto.getId());
        car = getMake(dto.getMake(), dto.getModel(), car);
        car.setFuelType(getFuelType(dto.getFuelType()));
        car.setGearBoxType(getGearBoxType(dto.getGearBoxType()));
        car.setBodyStyle(getBodyStyle(dto.getBodyStyle()));
        car.setMileageInKm(dto.getMileageInKm());
        car.setKidsSeats(dto.getKidsSeats());
        car.setAvailableTracking(dto.isAvailableTracking());
        car.setAvgRating(dto.getAvgRating());
        car.setCommentsCount(dto.getCommentsCount());
        return car;
    }

    @Override
    public CarDetails toDto(Car entity) {
        CarDetails carDTO = new CarDetails();
        carDTO.setId(entity.getMainAppId());
        carDTO.setMake(entity.getMake().getName());
        carDTO.setModel(entity.getModel().getName());
        carDTO.setFuelType(entity.getFuelType().getName());
        carDTO.setGearBoxType(entity.getGearBoxType().getName());
        carDTO.setBodyStyle(entity.getBodyStyle().getName());
        carDTO.setMileageInKm(entity.getMileageInKm());
        carDTO.setKidsSeats(entity.getKidsSeats());
        carDTO.setAvailableTracking(entity.getAvailableTracking());
        carDTO.setStatus(entity.getLogicalStatus().toString());
        carDTO.setAvgRating(entity.getAvgRating());
        carDTO.setCommentsCount(entity.getCommentsCount());
        return carDTO;
    }

    private Car getMake(String name, String modelName, Car car) {
        Make make = makeService.get(name);
        if (make != null) {
            car.setMake(make);
            Model model = modelService.get(modelName, make.getId());
            if (model == null) {
                Model createModel = new Model();
                createModel.setName(modelName);
                car.setModel(modelService.create(createModel, make));
                return car;
            }
            car.setModel(model);
            return car;
        }
        Make createMake = new Make();
        createMake.setName(name);
        makeService.create(createMake);
        Model createModel = new Model();
        createModel.setName(modelName);
        createModel.setMake(createMake);
        createModel = modelService.create(createModel, createMake);
        car.setModel(createModel);
        car.setMake(createModel.getMake());
        return car;
    }

    private GearboxType getGearBoxType(String name) {
        GearboxType gearboxType = gearboxTypeService.get(name);
        if (gearboxType != null) {
            return gearboxType;
        }
        CreateGearboxTypeDTO create = new CreateGearboxTypeDTO();
        create.setName(name);
        return gearboxTypeService.create(create);
    }


    private BodyStyle getBodyStyle(String name) {
        BodyStyle bodyStyle = bodyStyleService.get(name);
        if (bodyStyle != null) {
            return bodyStyle;
        }
        CreateBodyStyleDTO create = new CreateBodyStyleDTO();
        create.setName(name);
        return bodyStyleService.create(create);
    }

    private FuelType getFuelType(String name) {
        FuelType fuelType = fuelTypeService.get(name);
        if (fuelType != null) {
            return fuelType;
        }
        CreateFuelTypeDTO create = new CreateFuelTypeDTO();
        create.setName(name);
        return fuelTypeService.create(create);
    }

    @Autowired
    public CarDetailsMapper(ModelMapper modelMapper, MakeService makeService, ModelService modelService, BodyStyleService bodyStyleService, GearboxTypeService gearboxTypeService, FuelTypeService fuelTypeService) {
        this.modelMapper = modelMapper;
        this.makeService = makeService;
        this.modelService = modelService;
        this.bodyStyleService = bodyStyleService;
        this.gearboxTypeService = gearboxTypeService;
        this.fuelTypeService = fuelTypeService;
    }
}

