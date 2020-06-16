package jvn.Cars.endpoint;

import jvn.Cars.client.UserClient;
import jvn.Cars.dto.BASE64DecodedMultipartFile;
import jvn.Cars.dto.response.UserInfoDTO;
import jvn.Cars.dto.soap.car.CarDetails;
import jvn.Cars.dto.soap.car.CreateOrEditCarDetailsRequest;
import jvn.Cars.dto.soap.car.CreateOrEditCarDetailsResponse;
import jvn.Cars.dto.soap.car.PictureInfo;
import jvn.Cars.mapper.CarDetailsMapper;
import jvn.Cars.model.Car;
import jvn.Cars.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Endpoint
public class CarEndpoint {

    private static final String NAMESPACE_URI = "http://www.soap.dto/car";

    private CarService carService;

    private CarDetailsMapper carDetailsMapper;

    private UserClient userClient;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrEditCarDetailsRequest")
    @ResponsePayload
    public CreateOrEditCarDetailsResponse createOrEdit(@RequestPayload CreateOrEditCarDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        Car car = carDetailsMapper.toEntity(request.getCreateCarDetails());

        CarDetails carDetailsForResponse;
        if (car.getId() != null) {
            carDetailsForResponse = carDetailsMapper.toDto(carService.editAll(car.getId(),car, getFiles(request.getPictureInfo()), dto.getId()));
        } else {
            carDetailsForResponse = carDetailsMapper.toDto(carService.create(car, getFiles(request.getPictureInfo()),dto.getId()));
        }
        CreateOrEditCarDetailsResponse response = new CreateOrEditCarDetailsResponse();
        response.setCreateCarDetails(carDetailsForResponse);

        return response;
    }

    private List<MultipartFile> getFiles(List<PictureInfo> pictureInfos){
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (PictureInfo pictureInfo: pictureInfos){
            BASE64DecodedMultipartFile  base64DecodedMultipartFile = new BASE64DecodedMultipartFile(pictureInfo.getMultiPartFile(),pictureInfo.getFileName());
            multipartFiles.add(base64DecodedMultipartFile);
        }
        return multipartFiles;
    }

    @Autowired
    public CarEndpoint(CarService carService, CarDetailsMapper carDetailsMapper, UserClient userClient) {
        this.carService = carService;
        this.carDetailsMapper = carDetailsMapper;
        this.userClient = userClient;
    }
}
