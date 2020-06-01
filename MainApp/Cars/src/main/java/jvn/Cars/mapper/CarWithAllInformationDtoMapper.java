package jvn.Cars.mapper;

import jvn.Cars.dto.response.CarWithAllInformationDTO;
import jvn.Cars.model.Car;
import jvn.Cars.model.Picture;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.ldap.SortResponseControl;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarWithAllInformationDtoMapper implements MapperInterface<Car, CarWithAllInformationDTO> {

    private ModelMapper modelMapper;

    @Override
    public Car toEntity(CarWithAllInformationDTO dto) {
        // Unusable
        Car car = modelMapper.map(dto, Car.class);
        return car;
    }

    @Override
    public CarWithAllInformationDTO toDto(Car entity) {
        CarWithAllInformationDTO dto = new CarWithAllInformationDTO();
        dto.setId(entity.getId());
        dto.setLogicalStatus(entity.getLogicalStatus().toString());
        dto.setMake(entity.getMake().getName());
        dto.setModel(entity.getModel().getName());
        dto.setFuelType(entity.getFuelType().getName());
        dto.setGearBoxType(entity.getGearBoxType().getName());
        dto.setBodyStyle(entity.getBodyStyle().getName());
        dto.setMileageInKm(entity.getMileageInKm());
        dto.setKidsSeats(entity.getKidsSeats());
        dto.setAvailableTracking(entity.getAvailableTracking());
        dto.setAvgRating(entity.getAvgRating());
        dto.setCommentsCount(entity.getCommentsCount());
        dto.setPictures(entity.getPictures().stream().map(Picture::getData).collect(Collectors.toList()));
        dto.setOwner(entity.getOwner());

        return dto;
    }

    @Autowired
    public CarWithAllInformationDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
