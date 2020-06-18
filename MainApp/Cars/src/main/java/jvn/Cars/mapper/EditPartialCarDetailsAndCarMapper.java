package jvn.Cars.mapper;

import jvn.Cars.dto.soap.car.EditPartialCarDetails;
import jvn.Cars.model.Car;
import org.springframework.stereotype.Component;

@Component
public class EditPartialCarDetailsAndCarMapper  implements MapperInterface<Car, EditPartialCarDetails> {

    @Override
    public Car toEntity(EditPartialCarDetails dto) {
        Car car = new Car();
        car.setId(dto.getId());
        car.setMileageInKm(dto.getMileageInKm());
        car.setKidsSeats(dto.getKidsSeats());
        car.setAvailableTracking(dto.isAvailableTracking());
        return car;
    }

    @Override
    public EditPartialCarDetails toDto(Car entity) {
        EditPartialCarDetails carDTO = new EditPartialCarDetails();
        carDTO.setId(entity.getId());
        carDTO.setMileageInKm(entity.getMileageInKm());
        carDTO.setKidsSeats(entity.getKidsSeats());
        carDTO.setAvailableTracking(entity.getAvailableTracking());
        return carDTO;
    }

}
