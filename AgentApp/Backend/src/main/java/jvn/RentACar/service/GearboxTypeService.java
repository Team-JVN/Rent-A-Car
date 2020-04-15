package jvn.RentACar.service;

import jvn.RentACar.dto.both.GearboxTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidGearBoxTypeDataException;
import jvn.RentACar.model.GearboxType;

import java.util.List;

public interface GearboxTypeService {

    GearboxTypeDTO create(CreateGearboxTypeDTO createGearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    GearboxType get(Long id);

    List<GearboxTypeDTO> get() throws InvalidGearBoxTypeDataException;

    GearboxTypeDTO edit(GearboxTypeDTO gearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    GearboxTypeDTO delete(Long id) throws InvalidGearBoxTypeDataException;
}
