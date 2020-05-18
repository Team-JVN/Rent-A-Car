package jvn.RentACar.service;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.model.FuelType;

import java.util.List;

public interface FuelTypeService {

    FuelType create(CreateFuelTypeDTO createFuelTypeDTO);

    FuelType get(Long id);

    List<FuelType> get();

    FuelType edit(Long id, FuelTypeDTO fuelTypeDTO);

    void delete(Long id);
}
