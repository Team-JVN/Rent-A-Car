package jvn.RentACar.service;

import jvn.RentACar.dto.both.GearBoxTypeDTO;
import jvn.RentACar.dto.request.CreateGearBoxTypeDTO;
import jvn.RentACar.exceptionHandler.InvalidGearBoxTypeDataException;

import java.util.List;

public interface GearBoxTypeService {

    GearBoxTypeDTO create(CreateGearBoxTypeDTO createGearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    List<GearBoxTypeDTO> get() throws InvalidGearBoxTypeDataException;

    GearBoxTypeDTO edit(GearBoxTypeDTO gearBoxTypeDTO) throws InvalidGearBoxTypeDataException;

    GearBoxTypeDTO delete(Long id) throws InvalidGearBoxTypeDataException;
}
