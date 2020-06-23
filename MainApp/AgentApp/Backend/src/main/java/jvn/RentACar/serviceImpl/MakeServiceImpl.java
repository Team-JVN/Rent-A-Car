package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.MakeDTO;
import jvn.RentACar.exceptionHandler.InvalidMakeDataException;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.Make;
import jvn.RentACar.model.Model;
import jvn.RentACar.repository.MakeRepository;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.MakeService;
import jvn.RentACar.service.ModelService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MakeServiceImpl implements MakeService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private MakeRepository makeRepository;

    private ModelService modelService;

    private LogService logService;

    private UserService userService;

    @Override
    public Make create(Make make) {
        if (makeRepository.findByName(make.getName()) != null) {
            throw new InvalidMakeDataException("This Make already exist.", HttpStatus.BAD_REQUEST);
        }
        return makeRepository.saveAndFlush(make);
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
    public Make get(String name) {
        return makeRepository.findByName(name);
    }

    @Override
    public List<Make> get() {
        return makeRepository.findAllByOrderByNameAsc();
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
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DMK", String.format("Successfully deleted model %s of make %s", model.getId(), make.getId())));
        }
        makeRepository.deleteById(id);
    }

    private Make isEditable(Long id) {
        Make make = get(id);
        if (make.getCars().isEmpty()) {
            return make;
        }
        throw new InvalidMakeDataException("There's at least one car with this make so you can not edit it.",
                HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public MakeServiceImpl(MakeRepository makeRepository, ModelService modelService, LogService logService, UserService userService) {
        this.makeRepository = makeRepository;
        this.modelService = modelService;
        this.logService = logService;
        this.userService = userService;
    }
}
