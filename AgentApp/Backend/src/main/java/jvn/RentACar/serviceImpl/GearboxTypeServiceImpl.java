package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.GearboxTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidGearBoxTypeDataException;
import jvn.RentACar.model.GearboxType;
import jvn.RentACar.repository.GearboxTypeRepository;
import jvn.RentACar.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GearboxTypeServiceImpl implements GearboxTypeService {

    private GearboxTypeRepository gearBoxTypeRepository;

    @Override
    public GearboxTypeDTO create(CreateGearboxTypeDTO createGearBoxTypeDTO) {
        if (gearBoxTypeRepository.findByName(createGearBoxTypeDTO.getName()) != null) {
            throw new InvalidGearBoxTypeDataException("This Body Style already exist.");
        }
        return new GearboxTypeDTO(gearBoxTypeRepository.save(new GearboxType(createGearBoxTypeDTO.getName())));
    }

    @Override
    public GearboxType get(Long id) {
        GearboxType gearBoxType = gearBoxTypeRepository.findOneById(id);
        if (gearBoxType == null) {
            throw new InvalidGearBoxTypeDataException("This gearbox type doesn't exist.");
        }
        return gearBoxType;
    }

    @Override
    public List<GearboxTypeDTO> get() {
        List<GearboxType> gearBoxTypes = gearBoxTypeRepository.findAll();
        List<GearboxTypeDTO> gearBoxTypeDTOS = new ArrayList<>();
        for (GearboxType bodyStyle : gearBoxTypes) {
            gearBoxTypeDTOS.add(new GearboxTypeDTO(bodyStyle));
        }
        return gearBoxTypeDTOS;
    }

    @Override
    public GearboxTypeDTO edit(GearboxTypeDTO gearBoxTypeDTO) {
        if (gearBoxTypeRepository.findByNameAndIdNot(gearBoxTypeDTO.getName(), gearBoxTypeDTO.getId()) != null) {
            throw new InvalidGearBoxTypeDataException("This Gearbox Type already exist.");
        }
        GearboxType bodyStyle = isEditable(gearBoxTypeDTO.getId());
        bodyStyle.setName(gearBoxTypeDTO.getName());
        return new GearboxTypeDTO(gearBoxTypeRepository.save(bodyStyle));
    }

    @Override
    public GearboxTypeDTO delete(Long id) {
        GearboxType gearBoxType = isEditable(id);
        gearBoxTypeRepository.deleteById(id);
        return new GearboxTypeDTO(gearBoxType);
    }

    private GearboxType isEditable(Long id) {
        GearboxType gearBoxType = get(id);
        if (gearBoxType.getCars().isEmpty()) {
            return gearBoxType;
        }
        throw new InvalidGearBoxTypeDataException("There's at least one car with this gearbox type so you can not edit it.");
    }

    @Autowired
    public GearboxTypeServiceImpl(GearboxTypeRepository gearBoxTypeRepository) {
        this.gearBoxTypeRepository = gearBoxTypeRepository;
    }
}
