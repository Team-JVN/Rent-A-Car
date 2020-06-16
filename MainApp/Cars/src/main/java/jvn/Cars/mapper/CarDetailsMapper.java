package jvn.Cars.mapper;

import jvn.Cars.dto.request.CreateBodyStyleDTO;
import jvn.Cars.dto.request.CreateFuelTypeDTO;
import jvn.Cars.dto.request.CreateGearboxTypeDTO;
import jvn.Cars.dto.soap.car.CarDetails;
import jvn.Cars.model.*;
import jvn.Cars.service.*;
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
        car.setMake(getMake(dto.getMake()));
        car.setModel(getModel(dto.getModel(),car.getMake()));
        car.setFuelType(getFuelType(dto.getFuelType()));
        car.setGearBoxType(getGearBoxType(dto.getFuelType()));
        car.setBodyStyle(getBodyStyle(dto.getFuelType()));
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
        carDTO.setId(entity.getId());
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

    private Make getMake(String name){
        Make make = makeService.get(name);
        if(make != null){
            return make;
        }
        make = makeService.get("Other");
        if(make == null){
            Make createMake = new Make();
            createMake.setName("Other");
            return makeService.create(createMake);
        }
        return make;
    }

    private GearboxType getGearBoxType(String name){
        GearboxType gearboxType = gearboxTypeService.get(name);
        if(gearboxType != null){
            return gearboxType;
        }
        gearboxType = gearboxTypeService.get("Other");
        if(gearboxType == null){
            CreateGearboxTypeDTO create = new CreateGearboxTypeDTO();
            create.setName("Other");
            return gearboxTypeService.create(create);
        }
        return gearboxType;
    }


    private BodyStyle getBodyStyle(String name){
        BodyStyle bodyStyle = bodyStyleService.get(name);
        if(bodyStyle != null){
            return bodyStyle;
        }
        bodyStyle = bodyStyleService.get("Other");
        if(bodyStyle == null){
            CreateBodyStyleDTO create = new CreateBodyStyleDTO();
            create.setName("Other");
            return bodyStyleService.create(create);
        }
        return bodyStyle;
    }

    private FuelType getFuelType(String name){
        FuelType fuelType = fuelTypeService.get(name);
        if(fuelType != null){
            return fuelType;
        }
        fuelType = fuelTypeService.get("Other");
        if(fuelType == null){
            CreateFuelTypeDTO create = new CreateFuelTypeDTO();
            create.setName("Other");
            return fuelTypeService.create(create);
        }
        return fuelType;
    }

    private Model getModel(String name,Make make){
        Model model = modelService.get(name,make.getId());
        if(model != null){
            return model;
        }
        model = modelService.get("Other",make.getId());
        if(model == null){
            Model createModel = new Model();
            createModel.setName("Other");
            return modelService.create(createModel,make);
        }
        return model;
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

