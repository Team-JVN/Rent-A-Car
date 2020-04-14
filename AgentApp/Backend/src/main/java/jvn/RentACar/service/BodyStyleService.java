package jvn.RentACar.service;

import jvn.RentACar.dto.BodyStyleDTO;
import jvn.RentACar.dto.CreateBodyStyleDTO;
import jvn.RentACar.exceptionHandler.InvalidBodyStyleDataException;

import java.util.List;

public interface BodyStyleService {

    BodyStyleDTO create(CreateBodyStyleDTO createBodyStyleDTO) throws InvalidBodyStyleDataException;

    List<BodyStyleDTO> get() throws InvalidBodyStyleDataException;

    BodyStyleDTO edit(BodyStyleDTO bodyStyleDTO) throws InvalidBodyStyleDataException;

    BodyStyleDTO delete(Long id) throws InvalidBodyStyleDataException;
}
