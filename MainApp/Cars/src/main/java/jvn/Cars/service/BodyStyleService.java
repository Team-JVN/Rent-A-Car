package jvn.Cars.service;

import jvn.Cars.dto.both.BodyStyleDTO;
import jvn.Cars.dto.request.CreateBodyStyleDTO;
import jvn.Cars.model.BodyStyle;

import java.util.List;

public interface BodyStyleService {

    BodyStyle create(CreateBodyStyleDTO createBodyStyleDTO);

    BodyStyle get(Long id);

    BodyStyle get(String name);

    List<BodyStyle> get();

    BodyStyle edit(Long id, BodyStyleDTO bodyStyleDTO);

    void delete(Long id);
}
