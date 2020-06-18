package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidFuelTypeDataException;
import jvn.RentACar.model.FuelType;
import jvn.RentACar.repository.FuelTypeRepository;
import jvn.RentACar.service.FuelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelTypeServiceImpl implements FuelTypeService {

    private FuelTypeRepository fuelTypeRepository;

    @Override
    public FuelType create(CreateFuelTypeDTO createFuelTypeDTO) throws InvalidFuelTypeDataException {
        if (fuelTypeRepository.findByName(createFuelTypeDTO.getName()) != null) {
            throw new InvalidFuelTypeDataException("This Fuel Type already exist.", HttpStatus.BAD_REQUEST);
        }
        return fuelTypeRepository.save(new FuelType(createFuelTypeDTO.getName()));
    }

    @Override
    public FuelType get(Long id) {
        FuelType fuelType = fuelTypeRepository.findOneById(id);
        if (fuelType == null) {
            throw new InvalidFuelTypeDataException("This fuel type doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return fuelType;
    }

    @Override
    public FuelType get(String name) {
        return fuelTypeRepository.findByName(name);
    }

    @Override
    public List<FuelType> get() {
        return fuelTypeRepository.findAll();
    }

    @Override
    public FuelType edit(Long id, FuelTypeDTO fuelTypeDTO) {
        if (fuelTypeRepository.findByNameAndIdNot(fuelTypeDTO.getName(), id) != null) {
            throw new InvalidFuelTypeDataException("This Fuel Type already exist.", HttpStatus.BAD_REQUEST);
        }
        FuelType fuelType = isEditable(fuelTypeDTO.getId());
        fuelType.setName(fuelTypeDTO.getName());
        return fuelTypeRepository.save(fuelType);
    }

    @Override
    public void delete(Long id) {
        isEditable(id);
        fuelTypeRepository.deleteById(id);
    }

    private FuelType isEditable(Long id) {
        FuelType fuelType = get(id);

        if (fuelType.getCars().isEmpty()) {
            return fuelType;
        }
        throw new InvalidFuelTypeDataException("There's at least one car with this fuel type so you can not edit/delete it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public FuelTypeServiceImpl(FuelTypeRepository fuelTypeRepository) {
        this.fuelTypeRepository = fuelTypeRepository;
    }
}
