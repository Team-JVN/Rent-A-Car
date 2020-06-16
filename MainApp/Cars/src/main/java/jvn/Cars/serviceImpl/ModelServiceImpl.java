package jvn.Cars.serviceImpl;

import jvn.Cars.dto.both.ModelDTO;
import jvn.Cars.exceptionHandler.InvalidModelDataException;
import jvn.Cars.model.Make;
import jvn.Cars.model.Model;
import jvn.Cars.repository.ModelRepository;
import jvn.Cars.service.MakeService;
import jvn.Cars.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    private ModelRepository modelRepository;

    private MakeService makeService;

    @Override
    public Model create(Model model, Make make) {
        if (modelRepository.findByNameAndMakeId(model.getName(), make.getId()) != null) {
            throw new InvalidModelDataException("This Model already exist.", HttpStatus.BAD_REQUEST);
        }
        model.setMake(make);
        return modelRepository.save(model);
    }

    @Override
    public Model get(Long modelId, Long makeId) {
        Model model = modelRepository.findOneByIdAndMakeId(modelId, makeId);
        if (model == null) {
            throw new InvalidModelDataException("This Model doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return model;
    }

    @Override
    public Model get(String model, Long makeId) {
        return modelRepository.findByNameAndMakeId(model,makeId);
    }

    @Override
    public List<Model> getAll(Long makeId) {
        return modelRepository.findByMakeIdOrderByNameAsc(makeId);
    }

    @Override
    public Model edit(Long modelId, ModelDTO modelDTO, Long makeId) {
        if (modelRepository.findByNameAndIdNotAndMakeId(modelDTO.getName(), modelId, makeId) != null) {
            throw new InvalidModelDataException("This Model already exist.", HttpStatus.BAD_REQUEST);
        }
        Model model = isEditable(modelId, makeId);
        model.setName(modelDTO.getName());
        return modelRepository.save(model);
    }

    @Override
    public void delete(Long modelId, Long makeId) {
        Model model = isEditable(modelId, makeId);
        model.setMake(null);
        modelRepository.deleteById(modelId);
    }

    private Model isEditable(Long modelId, Long makeId) {
        Model model = get(modelId, makeId);

        if (model.getCars().isEmpty()) {
            return model;
        }
        throw new InvalidModelDataException("There's at least one car with this model so you can not edit it.", HttpStatus.BAD_REQUEST);
    }

    @Autowired
    public ModelServiceImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }
}
