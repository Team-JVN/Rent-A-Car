package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CreateCarDTO;
import jvn.RentACar.model.Car;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class CarMapperImpl {
    public ModelMapper modelMapper;

    public CarDTO convertToCarDto(Car car) {
        CarDTO carDTO = modelMapper.map(car, CarDTO.class);
        return carDTO;
    }

    public Car convertToEntity(CreateCarDTO createCarDTO) throws ParseException {
        Car car = modelMapper.map(createCarDTO, Car.class);
        return car;
    }

    public Car convertToEntity(CarDTO carDTO) throws ParseException {
        Car car = modelMapper.map(carDTO, Car.class);
        return car;
    }

    @Autowired
    public CarMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
