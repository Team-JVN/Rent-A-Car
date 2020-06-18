package jvn.RentACar.service;

import jvn.RentACar.dto.both.BodyStyleDTO;
import jvn.RentACar.dto.request.CreateBodyStyleDTO;
import jvn.RentACar.model.BodyStyle;

import java.util.List;

public interface BodyStyleService {

    BodyStyle create(CreateBodyStyleDTO createBodyStyleDTO);

    BodyStyle get(Long id);

    BodyStyle get(String name);

    List<BodyStyle> get();

    BodyStyle edit(Long id, BodyStyleDTO bodyStyleDTO);

    void delete(Long id);
}
