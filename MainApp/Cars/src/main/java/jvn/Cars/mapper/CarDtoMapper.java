package jvn.Cars.mapper;

import jvn.Cars.dto.both.CarDTO;
import jvn.Cars.model.Car;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarDtoMapper implements MapperInterface<Car, CarDTO> {

    private ModelMapper modelMapper;

    @Override
    public Car toEntity(CarDTO dto) {
        Car car = modelMapper.map(dto, Car.class);
        return car;
    }

    @Override
    public CarDTO toDto(Car entity) {
        CarDTO carDTO = modelMapper.map(entity, CarDTO.class);
        return carDTO;
    }

    @Autowired
    public CarDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
