package jvn.Cars.service;

import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.enumeration.LogicalStatus;
import jvn.Cars.model.Car;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {
    Car create(Car car, List<MultipartFile> multipartFiles, UserDTO userDTO);

    Car get(Long id, Long loggedInUser);

    Car get(Long id, LogicalStatus logicalStatus);

    Resource get(String fileName);

    List<Car> get(UserDTO userDTO);

//    Car editAll(Long id, CarDTO carDTO, List<MultipartFile> multipartFiles);

//    Car editPartial(Long id, CarEditDTO carDTO, List<MultipartFile> multipartFiles);

    void delete(Long id, Long loggedInUserId, String jwtToken, String user);

//    EditType getEditType(Long id);

}
