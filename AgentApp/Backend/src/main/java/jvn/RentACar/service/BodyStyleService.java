package jvn.RentACar.service;

import jvn.RentACar.dto.both.BodyStyleDTO;
import jvn.RentACar.dto.request.CreateBodyStyleDTO;
import jvn.RentACar.model.BodyStyle;

import java.util.List;

public interface BodyStyleService {

    BodyStyle create(CreateBodyStyleDTO createBodyStyleDTO);

    BodyStyle get(Long id);

    List<BodyStyleDTO> get();

    BodyStyleDTO edit(BodyStyleDTO bodyStyleDTO);

    BodyStyleDTO delete(Long id);
}
