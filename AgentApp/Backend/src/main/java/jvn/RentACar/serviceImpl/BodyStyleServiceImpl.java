package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.BodyStyleDTO;
import jvn.RentACar.dto.CreateBodyStyleDTO;
import jvn.RentACar.exceptionHandler.InvalidBodyStyleDataException;
import jvn.RentACar.model.BodyStyle;
import jvn.RentACar.repository.BodyStyleRepository;
import jvn.RentACar.service.BodyStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BodyStyleServiceImpl implements BodyStyleService {

    @Autowired
    private BodyStyleRepository bodyStyleRepository;

    @Override
    public BodyStyleDTO create(CreateBodyStyleDTO createBodyStyleDTO) throws InvalidBodyStyleDataException {
        if (bodyStyleRepository.findByName(createBodyStyleDTO.getName()) != null) {
            throw new InvalidBodyStyleDataException("This Body Style already exist.", HttpStatus.BAD_REQUEST);
        }
        return new BodyStyleDTO(bodyStyleRepository.save(new BodyStyle(createBodyStyleDTO.getName())));
    }

    @Override
    public List<BodyStyleDTO> get() throws InvalidBodyStyleDataException {
        List<BodyStyle> bodyStyles = bodyStyleRepository.findAll();
        List<BodyStyleDTO> bodyStyleDTOS = new ArrayList<>();
        for (BodyStyle bodyStyle : bodyStyles) {
            bodyStyleDTOS.add(new BodyStyleDTO(bodyStyle));
        }
        return bodyStyleDTOS;
    }

    @Override
    public BodyStyleDTO edit(BodyStyleDTO bodyStyleDTO) throws InvalidBodyStyleDataException {
        if (bodyStyleRepository.findByNameAndIdNot(bodyStyleDTO.getName(), bodyStyleDTO.getId()) != null) {
            throw new InvalidBodyStyleDataException("This Body Stale already exist.", HttpStatus.BAD_REQUEST);
        }
        BodyStyle bodyStyle = isEditable(bodyStyleDTO.getId());
        bodyStyle.setName(bodyStyleDTO.getName());
        return new BodyStyleDTO(bodyStyleRepository.save(bodyStyle));
    }

    @Override
    public BodyStyleDTO delete(Long id) throws InvalidBodyStyleDataException {
        BodyStyle bodyStyle = isEditable(id);
        bodyStyleRepository.deleteById(id);
        return new BodyStyleDTO(bodyStyle);
    }

    private BodyStyle isEditable(Long id) throws InvalidBodyStyleDataException {
        BodyStyle bodyStyle = bodyStyleRepository.getById(id);
        if (bodyStyle.getCars().isEmpty()) {
            return bodyStyle;
        }
        throw new InvalidBodyStyleDataException("There's at least one car with this body style so you can not edit it.", HttpStatus.BAD_REQUEST);
    }
}
