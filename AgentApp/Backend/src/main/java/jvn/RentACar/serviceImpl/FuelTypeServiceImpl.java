package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidFuelTypeDataException;
import jvn.RentACar.model.FuelType;
import jvn.RentACar.repository.FuelTypeRepository;
import jvn.RentACar.service.FuelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FuelTypeServiceImpl implements FuelTypeService {

    private FuelTypeRepository fuelTypeRepository;

    @Override
    public FuelTypeDTO create(CreateFuelTypeDTO createFuelTypeDTO) throws InvalidFuelTypeDataException {
        if (fuelTypeRepository.findByName(createFuelTypeDTO.getName()) != null) {
            throw new InvalidFuelTypeDataException("This Fuel Type already exist.");
        }
        return new FuelTypeDTO(fuelTypeRepository.save(new FuelType(createFuelTypeDTO.getName())));
    }

    @Override
    public FuelType get(Long id) {
        FuelType fuelType = fuelTypeRepository.findOneById(id);
        if (fuelType == null) {
            throw new InvalidFuelTypeDataException("This fuel type doesn't exist.");
        }
        return fuelType;
    }

    @Override
    public List<FuelTypeDTO> get() throws InvalidFuelTypeDataException {
        List<FuelType> fuelTypes = fuelTypeRepository.findAll();
        List<FuelTypeDTO> fuelTypeDTOS = new ArrayList<>();
        for (FuelType fuelType : fuelTypes) {
            fuelTypeDTOS.add(new FuelTypeDTO(fuelType));
        }
        return fuelTypeDTOS;
    }

    @Override
    public FuelTypeDTO edit(FuelTypeDTO fuelTypeDTO) {
        if (fuelTypeRepository.findByNameAndIdNot(fuelTypeDTO.getName(), fuelTypeDTO.getId()) != null) {
            throw new InvalidFuelTypeDataException("This Fuel Type already exist.");
        }
        FuelType fuelType = isEditable(fuelTypeDTO.getId());
        fuelType.setName(fuelTypeDTO.getName());
        return new FuelTypeDTO(fuelTypeRepository.save(fuelType));
    }

    @Override
    public FuelTypeDTO delete(Long id) {
        FuelType fuelType = isEditable(id);
        fuelTypeRepository.deleteById(id);
        return new FuelTypeDTO(fuelType);
    }

    private FuelType isEditable(Long id) {
        FuelType fuelType = get(id);

        if (fuelType.getCars().isEmpty()) {
            return fuelType;
        }
        throw new InvalidFuelTypeDataException("There's at least one car with this fuel type so you can not edit it.");
    }

    @Autowired
    public FuelTypeServiceImpl(FuelTypeRepository fuelTypeRepository) {
        this.fuelTypeRepository = fuelTypeRepository;
    }
}
