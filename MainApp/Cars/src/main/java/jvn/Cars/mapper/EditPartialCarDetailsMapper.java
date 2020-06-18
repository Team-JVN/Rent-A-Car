package jvn.Cars.mapper;
import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.dto.soap.car.EditPartialCarDetails;
import org.springframework.stereotype.Component;

@Component
public class EditPartialCarDetailsMapper implements MapperInterface<CarEditDTO, EditPartialCarDetails> {

    @Override
    public CarEditDTO toEntity(EditPartialCarDetails dto) {
        CarEditDTO car = new CarEditDTO();
        car.setId(dto.getId());
        car.setMileageInKm(dto.getMileageInKm());
        car.setKidsSeats(dto.getKidsSeats());
        car.setAvailableTracking(dto.isAvailableTracking());
        return car;
    }

    @Override
    public EditPartialCarDetails toDto(CarEditDTO entity) {
        EditPartialCarDetails carDTO = new EditPartialCarDetails();
        carDTO.setId(entity.getId());
        carDTO.setMileageInKm(entity.getMileageInKm());
        carDTO.setKidsSeats(entity.getKidsSeats());
        carDTO.setAvailableTracking(entity.getAvailableTracking());
        return carDTO;
    }

}
