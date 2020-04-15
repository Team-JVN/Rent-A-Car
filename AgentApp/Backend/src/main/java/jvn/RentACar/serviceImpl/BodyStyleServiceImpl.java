package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.BodyStyleDTO;
import jvn.RentACar.dto.request.CreateBodyStyleDTO;
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

    private BodyStyleRepository bodyStyleRepository;

    @Override
    public BodyStyle create(CreateBodyStyleDTO createBodyStyleDTO) {
        if (bodyStyleRepository.findByName(createBodyStyleDTO.getName()) != null) {
            throw new InvalidBodyStyleDataException("This Body Style already exist.", HttpStatus.BAD_REQUEST);
        }
        return bodyStyleRepository.save(new BodyStyle(createBodyStyleDTO.getName()));
    }

    @Override
    public BodyStyle get(Long id) {
        BodyStyle bodyStyle = bodyStyleRepository.findOneById(id);
        if (bodyStyle == null) {
            throw new InvalidBodyStyleDataException("This body style doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        return bodyStyle;
    }

    @Override
    public List<BodyStyleDTO> get() {
        List<BodyStyle> bodyStyles = bodyStyleRepository.findAll();
        List<BodyStyleDTO> bodyStyleDTOS = new ArrayList<>();
        for (BodyStyle bodyStyle : bodyStyles) {
            bodyStyleDTOS.add(new BodyStyleDTO(bodyStyle));
        }
        return bodyStyleDTOS;
    }

    @Override
    public BodyStyleDTO edit(BodyStyleDTO bodyStyleDTO) {
        if (bodyStyleRepository.findByNameAndIdNot(bodyStyleDTO.getName(), bodyStyleDTO.getId()) != null) {
            throw new InvalidBodyStyleDataException("This Body Stale already exist.", HttpStatus.BAD_REQUEST);
        }
        BodyStyle bodyStyle = isEditable(bodyStyleDTO.getId());
        bodyStyle.setName(bodyStyleDTO.getName());
        return new BodyStyleDTO(bodyStyleRepository.save(bodyStyle));
    }

    @Override
    public BodyStyleDTO delete(Long id) {
        BodyStyle bodyStyle = isEditable(id);
        bodyStyleRepository.deleteById(id);
        return new BodyStyleDTO(bodyStyle);
    }

    private BodyStyle isEditable(Long id) {
        BodyStyle bodyStyle = get(id);

        if (bodyStyle.getCars().isEmpty()) {
            return bodyStyle;
        }
        throw new InvalidBodyStyleDataException("There's at least one car with this body style so you can not edit it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public BodyStyleServiceImpl(BodyStyleRepository bodyStyleRepository) {
        this.bodyStyleRepository = bodyStyleRepository;
    }
}
