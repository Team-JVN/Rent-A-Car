package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.MakeDTO;
import jvn.RentACar.exceptionHandler.InvalidMakeDataException;
import jvn.RentACar.model.Make;
import jvn.RentACar.model.Model;
import jvn.RentACar.repository.MakeRepository;
import jvn.RentACar.service.MakeService;
import jvn.RentACar.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MakeServiceImpl implements MakeService {

    private MakeRepository makeRepository;

    private ModelService modelService;

    @Override
    public Make create(Make make) {
        if (makeRepository.findByName(make.getName()) != null) {
            throw new InvalidMakeDataException("This Make already exist.", HttpStatus.BAD_REQUEST);
        }
        return makeRepository.save(make);
    }

    @Override
    public Make get(Long id) {
        Make make = makeRepository.findOneById(id);
        if (make == null) {
            throw new InvalidMakeDataException("This Make doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return make;
    }

    @Override
    public List<Make> get() {
        return makeRepository.findAll();
    }

    @Override
    public Make edit(Long id, MakeDTO makeDTO) {
        if (makeRepository.findByNameAndIdNot(makeDTO.getName(), id) != null) {
            throw new InvalidMakeDataException("This Make already exist.", HttpStatus.BAD_REQUEST);
        }
        Make make = isEditable(makeDTO.getId());
        make.setName(makeDTO.getName());
        return makeRepository.save(make);
    }

    @Override
    public void delete(Long id) {
        Make make = isEditable(id);
        Set<Model> models = make.getModels();
        make.setModels(null);
        for (Model model : models) {
            modelService.delete(model.getId(), make.getId());
        }
        makeRepository.deleteById(id);
    }

    private Make isEditable(Long id) {
        Make make = get(id);
        if (make.getCars().isEmpty()) {
            return make;
        }
        throw new InvalidMakeDataException("There's at least one car with this make so you can not edit it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public MakeServiceImpl(MakeRepository makeRepository, ModelService modelService) {
        this.makeRepository = makeRepository;
        this.modelService = modelService;
    }
}
