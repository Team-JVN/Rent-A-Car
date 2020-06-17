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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        car.setId(dto.getId());
        car = getMake(dto.getMake(),dto.getModel(),car);
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

    private Car getMake(String makeName,String modelName,Car car){
        Make make = makeService.get(makeName);
        if(make != null){
            car.setMake(make);
            Model model = modelService.get(modelName,make.getId());
            if(model == null){
                Model createModel = new Model();
                createModel.setName(modelName);
                car.setModel(modelService.create(createModel,make));
                return car;
            }
            car.setModel(model);
            return car;
        }
        make = makeService.get("Other");
        if(make == null){
            Make createMake = new Make();
            createMake.setName("Other");
            Model createModel = new Model();
            createModel.setName("Other");
            createModel.setMake(createMake);
            createModel = modelService.create(createModel,createMake);
            car.setModel(createModel);
            car.setMake(createModel.getMake());
            return car;
        }
        car.setModel(modelService.get("Other",make.getId()));
        car.setMake(make);
        return car;
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

