package jvn.Cars.service;

import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.enumeration.LogicalStatus;
import jvn.Cars.model.Car;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {
    Car create(Car car, List<MultipartFile> multipartFiles, Long userId);

    Car get(Long id, Long loggedInUser);

    Car get(Long id, LogicalStatus logicalStatus);

    Resource get(String fileName);

    List<Car> get(UserDTO userDTO);

    List<Car> getAll(Long loggedInUser);

    List<Car> getStatistics(String filter);

    Car editAll(Long id, Car car, List<MultipartFile> multipartFiles, Long loggedInUserId);

    Car editPartial(Long id, CarEditDTO carDTO, List<MultipartFile> multipartFiles, Long loggedInUserId);

    void delete(Long id, Long loggedInUserId, String jwtToken, String user);

    boolean checkIfCanDeleteAndDelete(Long id, Long loggedInUser);

    String getCarEditType(Long id, Long loggedInUser);

    void updateCarRating(Long carId, Integer rating);
}
