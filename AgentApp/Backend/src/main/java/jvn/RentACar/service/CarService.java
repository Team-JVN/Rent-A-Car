package jvn.RentACar.service;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.model.Car;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {
    Car create(Car car, List<MultipartFile> multipartFiles);

    List<CarDTO> get();

    CarDTO edit(CarDTO carDTO, List<MultipartFile> multipartFiles);

    void delete(Long id);
}
