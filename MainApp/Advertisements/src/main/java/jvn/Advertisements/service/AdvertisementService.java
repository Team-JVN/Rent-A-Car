package jvn.Advertisements.service;

import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.dto.response.LocationDTO;
import jvn.Advertisements.enumeration.EditType;
import jvn.Advertisements.model.Advertisement;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO, UserDTO userDTO, String jwtToken, String user);

    List<Advertisement> get(List<Long> advertisements);

    void delete(Long id, Long loggedInUserId, String jwtToken, String user);

    Advertisement edit(Long id, Advertisement advertisement, Long loggedInUserId, String jwtToken, String user, UserDTO userDTO);

    Advertisement editPartial(Long id, AdvertisementEditDTO advertisement, Long loggedInUserId);

    EditType getCarEditType(Long carId);

    LocationDTO getCarLocation(Long advId, Long userId) throws IOException, ScriptException;

    Boolean canEditCarPartially(Long carId, String jwtToken, String user);

    Boolean canDeleteCar(Long carId);

    void listen(String message);
}

