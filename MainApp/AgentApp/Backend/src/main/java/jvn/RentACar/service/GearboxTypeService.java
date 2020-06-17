package jvn.RentACar.service;

import jvn.RentACar.dto.both.GearboxTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidGearBoxTypeDataException;
import jvn.RentACar.model.GearboxType;

import java.util.List;

public interface GearboxTypeService {

    GearboxType create(CreateGearboxTypeDTO createGearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    GearboxType get(Long id);

    GearboxType get(String name);

    List<GearboxType> get() throws InvalidGearBoxTypeDataException;

    GearboxType edit(Long id, GearboxTypeDTO gearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    void delete(Long id) throws InvalidGearBoxTypeDataException;
}
