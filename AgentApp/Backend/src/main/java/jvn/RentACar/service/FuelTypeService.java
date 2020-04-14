package jvn.RentACar.service;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidFuelTypeDataException;

import java.util.List;

public interface FuelTypeService {

    FuelTypeDTO create(CreateFuelTypeDTO createFuelTypeDTO) throws InvalidFuelTypeDataException;

    List<FuelTypeDTO> get() throws InvalidFuelTypeDataException;

    FuelTypeDTO edit(FuelTypeDTO fuelTypeDTO) throws InvalidFuelTypeDataException;

    FuelTypeDTO delete(Long id) throws InvalidFuelTypeDataException;
}
