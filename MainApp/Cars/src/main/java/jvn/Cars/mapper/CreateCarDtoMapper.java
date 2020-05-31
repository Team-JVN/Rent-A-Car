package jvn.Cars.mapper;

import jvn.Cars.dto.request.CreateCarDTO;
import jvn.Cars.model.Car;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateCarDtoMapper implements MapperInterface<Car, CreateCarDTO> {

    private ModelMapper modelMapper;

    @Override
    public Car toEntity(CreateCarDTO dto) {
        Car car = modelMapper.map(dto, Car.class);
        return car;
    }

    @Override
    public CreateCarDTO toDto(Car entity) {
        CreateCarDTO createCarDTO = modelMapper.map(entity, CreateCarDTO.class);
        return createCarDTO;
    }

    @Autowired
    public CreateCarDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
