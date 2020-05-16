package jvn.Users.service;

import jvn.Users.dto.both.BodyStyleDTO;
import jvn.Users.dto.request.CreateBodyStyleDTO;
import jvn.Users.model.BodyStyle;

import java.util.List;

public interface BodyStyleService {

    BodyStyle create(CreateBodyStyleDTO createBodyStyleDTO);

    BodyStyle get(Long id);

    List<BodyStyle> get();

    BodyStyle edit(Long id, BodyStyleDTO bodyStyleDTO);

    void delete(Long id);
}
