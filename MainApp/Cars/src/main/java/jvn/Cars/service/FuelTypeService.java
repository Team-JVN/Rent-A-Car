package jvn.Cars.service;

import jvn.Cars.dto.both.FuelTypeDTO;
import jvn.Cars.dto.request.CreateFuelTypeDTO;
import jvn.Cars.model.FuelType;

import java.util.List;

public interface FuelTypeService {

    FuelType create(CreateFuelTypeDTO createFuelTypeDTO);

    FuelType get(Long id);

    List<FuelType> get();

    FuelType edit(Long id, FuelTypeDTO fuelTypeDTO);

    void delete(Long id);
}
