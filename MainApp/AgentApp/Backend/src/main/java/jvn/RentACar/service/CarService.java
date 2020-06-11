package jvn.RentACar.service;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Car;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CarService {
    Car create(Car car, List<MultipartFile> multipartFiles);

    Car get(Long id);

    Resource get(String fileName);

    List<Car> get();

    Car editAll(Long id, CarDTO carDTO, List<MultipartFile> multipartFiles);

    Car editPartial(Long id, CarEditDTO carDTO, List<MultipartFile> multipartFiles);

    void delete(Long id);

    EditType getEditType(Long id);

    List<Car> getStatistics(String filter);
}
