package jvn.Cars.mapper;

import jvn.Cars.dto.response.CarWithAllInformationDTO;
import jvn.Cars.model.Car;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarWithAllInformationDtoMapper implements MapperInterface<Car, CarWithAllInformationDTO> {

    private ModelMapper modelMapper;

    @Override
    public Car toEntity(CarWithAllInformationDTO dto) {
        Car car = modelMapper.map(dto, Car.class);
        return car;
    }

    @Override
    public CarWithAllInformationDTO toDto(Car entity) {
        CarWithAllInformationDTO carDTO = modelMapper.map(entity, CarWithAllInformationDTO.class);
        return carDTO;
    }

    @Autowired
    public CarWithAllInformationDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
