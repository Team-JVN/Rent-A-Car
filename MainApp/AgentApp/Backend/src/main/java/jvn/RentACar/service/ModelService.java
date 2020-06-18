package jvn.RentACar.service;

import jvn.RentACar.dto.both.ModelDTO;
import jvn.RentACar.model.Make;
import jvn.RentACar.model.Model;

import java.util.List;

public interface ModelService {
    Model create(Model model, Make make);

    Model get(Long modelId, Long makeId);

    Model get(String name,Long makeId);

    List<Model> getAll(Long makeId);

    Model edit(Long modelId, ModelDTO modelDTO, Long makeId);

    void delete(Long modelId, Long makeId);
}
