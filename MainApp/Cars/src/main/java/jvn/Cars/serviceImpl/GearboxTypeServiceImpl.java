package jvn.Cars.serviceImpl;

import jvn.Cars.dto.both.GearboxTypeDTO;
import jvn.Cars.dto.request.CreateGearboxTypeDTO;
import jvn.Cars.exceptionHandler.InvalidGearBoxTypeDataException;
import jvn.Cars.model.GearboxType;
import jvn.Cars.repository.GearboxTypeRepository;
import jvn.Cars.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GearboxTypeServiceImpl implements GearboxTypeService {

    private GearboxTypeRepository gearBoxTypeRepository;

    @Override
    public GearboxType create(CreateGearboxTypeDTO createGearBoxTypeDTO) {
        if (gearBoxTypeRepository.findByName(createGearBoxTypeDTO.getName()) != null) {
            throw new InvalidGearBoxTypeDataException("This Gearbox Type already exist.", HttpStatus.BAD_REQUEST);
        }
        return gearBoxTypeRepository.save(new GearboxType(createGearBoxTypeDTO.getName()));
    }

    @Override
    public GearboxType get(Long id) {
        GearboxType gearBoxType = gearBoxTypeRepository.findOneById(id);
        if (gearBoxType == null) {
            throw new InvalidGearBoxTypeDataException("This gearbox type doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return gearBoxType;
    }

    @Override
    public List<GearboxType> get() {
        return gearBoxTypeRepository.findAll();
    }

    @Override
    public GearboxType edit(Long id, GearboxTypeDTO gearBoxTypeDTO) {
        if (gearBoxTypeRepository.findByNameAndIdNot(gearBoxTypeDTO.getName(), id) != null) {
            throw new InvalidGearBoxTypeDataException("This Gearbox Type already exist.", HttpStatus.BAD_REQUEST);
        }
        GearboxType bodyStyle = isEditable(gearBoxTypeDTO.getId());
        bodyStyle.setName(gearBoxTypeDTO.getName());
        return gearBoxTypeRepository.save(bodyStyle);
    }

    @Override
    public void delete(Long id) {
        isEditable(id);
        gearBoxTypeRepository.deleteById(id);
    }

    private GearboxType isEditable(Long id) {
        GearboxType gearBoxType = get(id);
        if (gearBoxType.getCars().isEmpty()) {
            return gearBoxType;
        }
        throw new InvalidGearBoxTypeDataException("There's at least one car with this gearbox type so you can not edit/delete it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public GearboxTypeServiceImpl(GearboxTypeRepository gearBoxTypeRepository) {
        this.gearBoxTypeRepository = gearBoxTypeRepository;
    }
}
