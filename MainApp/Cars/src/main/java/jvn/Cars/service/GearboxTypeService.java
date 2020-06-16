package jvn.Cars.service;

import jvn.Cars.dto.both.GearboxTypeDTO;
import jvn.Cars.dto.request.CreateGearboxTypeDTO;
import jvn.Cars.exceptionHandler.InvalidGearBoxTypeDataException;
import jvn.Cars.model.GearboxType;

import java.util.List;

public interface GearboxTypeService {

    GearboxType create(CreateGearboxTypeDTO createGearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    GearboxType get(Long id);

    GearboxType get(String name);

    List<GearboxType> get() throws InvalidGearBoxTypeDataException;

    GearboxType edit(Long id, GearboxTypeDTO gearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    void delete(Long id) throws InvalidGearBoxTypeDataException;
}
