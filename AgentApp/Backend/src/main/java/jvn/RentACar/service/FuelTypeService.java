package jvn.RentACar.service;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.model.FuelType;

import java.util.List;

public interface FuelTypeService {

    FuelTypeDTO create(CreateFuelTypeDTO createFuelTypeDTO);

    FuelType get(Long id);

    List<FuelTypeDTO> get();

    FuelTypeDTO edit(Long id, FuelTypeDTO fuelTypeDTO);

    FuelTypeDTO delete(Long id);
}
