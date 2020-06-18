package jvn.Cars.service;

import jvn.Cars.dto.both.ModelDTO;
import jvn.Cars.model.Make;
import jvn.Cars.model.Model;

import java.util.List;

public interface ModelService {
    Model create(Model model, Make make);

    Model get(Long modelId, Long makeId);

    Model get(String make,Long makeId);

    List<Model> getAll(Long makeId);

    Model edit(Long modelId, ModelDTO modelDTO, Long makeId);

    void delete(Long modelId, Long makeId);
}
