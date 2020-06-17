package jvn.Cars.service;

import jvn.Cars.dto.both.MakeDTO;
import jvn.Cars.model.Make;

import java.util.List;

public interface MakeService {
    Make create(Make make);

    Make get(Long id);

    Make get(String name);

    List<Make> get();

    Make edit(Long id, MakeDTO makeDTO);

    void delete(Long id);
}
