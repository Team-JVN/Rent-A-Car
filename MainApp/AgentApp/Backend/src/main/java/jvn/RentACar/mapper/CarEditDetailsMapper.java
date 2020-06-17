package jvn.RentACar.mapper;
import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.dto.soap.car.EditPartialCarDetails;
import org.springframework.stereotype.Component;

@Component
public class CarEditDetailsMapper implements MapperInterface<CarEditDTO, EditPartialCarDetails>  {

    @Override
    public CarEditDTO toEntity(EditPartialCarDetails dto) {
        CarEditDTO car = new CarEditDTO();
        car.setMileageInKm(dto.getMileageInKm());
        car.setKidsSeats(dto.getKidsSeats());
        car.setAvailableTracking(dto.isAvailableTracking());
        car.setId(dto.getId());
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
