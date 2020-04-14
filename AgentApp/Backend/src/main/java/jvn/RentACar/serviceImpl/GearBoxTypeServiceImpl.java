package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.GearBoxTypeDTO;
import jvn.RentACar.dto.request.CreateGearBoxTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidGearBoxTypeDataException;
import jvn.RentACar.model.GearBoxType;
import jvn.RentACar.repository.GearBoxTypeRepository;
import jvn.RentACar.service.GearBoxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GearBoxTypeServiceImpl implements GearBoxTypeService {

    private GearBoxTypeRepository gearBoxTypeRepository;

    @Override
    public GearBoxTypeDTO create(CreateGearBoxTypeDTO createGearBoxTypeDTO) throws InvalidGearBoxTypeDataException {
        if (gearBoxTypeRepository.findByName(createGearBoxTypeDTO.getName()) != null) {
            throw new InvalidGearBoxTypeDataException("This Body Style already exist.");
        }
        return new GearBoxTypeDTO(gearBoxTypeRepository.save(new GearBoxType(createGearBoxTypeDTO.getName())));
    }

    @Override
    public List<GearBoxTypeDTO> get() throws InvalidGearBoxTypeDataException {
        List<GearBoxType> gearBoxTypes = gearBoxTypeRepository.findAll();
        List<GearBoxTypeDTO> gearBoxTypeDTOS = new ArrayList<>();
        for (GearBoxType bodyStyle : gearBoxTypes) {
            gearBoxTypeDTOS.add(new GearBoxTypeDTO(bodyStyle));
        }
        return gearBoxTypeDTOS;
    }

    @Override
    public GearBoxTypeDTO edit(GearBoxTypeDTO gearBoxTypeDTO) throws InvalidGearBoxTypeDataException {
        if (gearBoxTypeRepository.findByNameAndIdNot(gearBoxTypeDTO.getName(), gearBoxTypeDTO.getId()) != null) {
            throw new InvalidGearBoxTypeDataException("This Gearbox Type already exist.");
        }
        GearBoxType bodyStyle = isEditable(gearBoxTypeDTO.getId());
        bodyStyle.setName(gearBoxTypeDTO.getName());
        return new GearBoxTypeDTO(gearBoxTypeRepository.save(bodyStyle));
    }

    @Override
    public GearBoxTypeDTO delete(Long id) throws InvalidGearBoxTypeDataException {
        GearBoxType gearBoxType = isEditable(id);
        gearBoxTypeRepository.deleteById(id);
        return new GearBoxTypeDTO(gearBoxType);
    }

    private GearBoxType isEditable(Long id) throws InvalidGearBoxTypeDataException {
        GearBoxType gearBoxType = gearBoxTypeRepository.getById(id);
        if (gearBoxType.getCars().isEmpty()) {
            return gearBoxType;
        }
        throw new InvalidGearBoxTypeDataException("There's at least one car with this gearbox type so you can not edit it.");
    }

    @Autowired
    public GearBoxTypeServiceImpl(GearBoxTypeRepository gearBoxTypeRepository) {
        this.gearBoxTypeRepository = gearBoxTypeRepository;
    }
}
