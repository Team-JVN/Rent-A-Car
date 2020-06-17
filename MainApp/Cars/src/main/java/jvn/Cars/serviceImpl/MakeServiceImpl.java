package jvn.Cars.serviceImpl;

import jvn.Cars.dto.both.MakeDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.exceptionHandler.InvalidMakeDataException;
import jvn.Cars.model.Make;
import jvn.Cars.model.Model;
import jvn.Cars.producer.LogProducer;
import jvn.Cars.repository.MakeRepository;
import jvn.Cars.service.MakeService;
import jvn.Cars.service.ModelService;
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

    private LogProducer logProducer;

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
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DMK", String.format("Successfully deleted model %s of make %s", model.getId(), make.getId())));
        }
        makeRepository.deleteById(id);
    }

    private Make isEditable(Long id) {
        Make make = get(id);
        if (make.getCars().isEmpty()) {
            return make;
        }
        throw new InvalidMakeDataException("There's at least one car of this make, therefore you cannot edit it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public MakeServiceImpl(MakeRepository makeRepository, ModelService modelService, LogProducer logProducer) {
        this.makeRepository = makeRepository;
        this.modelService = modelService;
        this.logProducer = logProducer;
    }
}
