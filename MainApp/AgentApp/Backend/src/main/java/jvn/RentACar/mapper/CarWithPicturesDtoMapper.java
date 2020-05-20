package jvn.RentACar.mapper;

import jvn.RentACar.dto.response.CarWithPicturesDTO;
import jvn.RentACar.model.Car;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarWithPicturesDtoMapper implements MapperInterface<Car, CarWithPicturesDTO> {

    private ModelMapper modelMapper;

    @Override
    public Car toEntity(CarWithPicturesDTO dto) {
        Car car = modelMapper.map(dto, Car.class);
        return car;
    }

    @Override
    public CarWithPicturesDTO toDto(Car entity) {
        CarWithPicturesDTO carDTO = modelMapper.map(entity, CarWithPicturesDTO.class);
        return carDTO;
    }

    @Autowired
    public CarWithPicturesDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}