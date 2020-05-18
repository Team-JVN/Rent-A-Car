package jvn.Users.serviceImpl;

import jvn.Users.dto.both.BodyStyleDTO;
import jvn.Users.dto.request.CreateBodyStyleDTO;
import jvn.Users.exceptionHandler.InvalidBodyStyleDataException;
import jvn.Users.model.BodyStyle;
import jvn.Users.repository.BodyStyleRepository;
import jvn.Users.service.BodyStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
            throw new InvalidBodyStyleDataException("This body style doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return bodyStyle;
    }

    @Override
    public List<BodyStyle> get() {
        return bodyStyleRepository.findAll();
    }

    @Override
    public BodyStyle edit(Long id, BodyStyleDTO bodyStyleDTO) {
        if (bodyStyleRepository.findByNameAndIdNot(bodyStyleDTO.getName(), id) != null) {
            throw new InvalidBodyStyleDataException("This Body Stale already exist.", HttpStatus.BAD_REQUEST);
        }
        BodyStyle bodyStyle = isEditable(bodyStyleDTO.getId());
        bodyStyle.setName(bodyStyleDTO.getName());
        return bodyStyleRepository.save(bodyStyle);
    }

    @Override
    public void delete(Long id) {
        isEditable(id);
        bodyStyleRepository.deleteById(id);
    }

    private BodyStyle isEditable(Long id) {
        BodyStyle bodyStyle = get(id);
        throw new InvalidBodyStyleDataException("There's at least one car with this body style so you can not edit it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public BodyStyleServiceImpl(BodyStyleRepository bodyStyleRepository) {
        this.bodyStyleRepository = bodyStyleRepository;
    }
}
