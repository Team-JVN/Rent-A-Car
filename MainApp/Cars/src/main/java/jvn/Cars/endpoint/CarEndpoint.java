package jvn.Cars.endpoint;

import jvn.Cars.producer.LogProducer;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import jvn.Cars.client.UserClient;
import jvn.Cars.dto.BASE64DecodedMultipartFile;
import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.dto.response.UserInfoDTO;
import jvn.Cars.dto.soap.car.*;
import jvn.Cars.mapper.CarDetailsMapper;
import jvn.Cars.mapper.EditPartialCarDetailsAndCarMapper;
import jvn.Cars.mapper.EditPartialCarDetailsMapper;
import jvn.Cars.model.Car;
import jvn.Cars.model.Picture;
import jvn.Cars.service.CarService;
import jvn.Cars.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Endpoint
public class CarEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Value("${UPLOADED_PICTURES_PATH:uploadedPictures/}")
    private String UPLOADED_PICTURES_PATH;

    private static final String NAMESPACE_URI = "http://www.soap.dto/car";

    private CarService carService;

    private CarDetailsMapper carDetailsMapper;

    private UserClient userClient;

    private EditPartialCarDetailsMapper editPartialCarDetailsmapper;

    private EditPartialCarDetailsAndCarMapper editPartialCarDetailsAndCarMapper;

    @Autowired
    private PictureService pictureService;

    private LogProducer logProducer;

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
            carDetailsForResponse = carDetailsMapper.toDto(carService.editAll(car.getId(), car, getFiles(request.getPictureInfo()), dto.getId()));
        } else {
            carDetailsForResponse = carDetailsMapper.toDto(carService.create(car, getFiles(request.getPictureInfo()), dto.getId()));
        }
        CreateOrEditCarDetailsResponse response = new CreateOrEditCarDetailsResponse();
        response.setCreateCarDetails(carDetailsForResponse);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteCarDetailsRequest")
    @ResponsePayload
    public DeleteCarDetailsResponse delete(@RequestPayload DeleteCarDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        DeleteCarDetailsResponse response = new DeleteCarDetailsResponse();
        response.setCanDelete(carService.checkIfCanDeleteAndDelete(request.getId(), dto.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCarEditTypeRequest")
    @ResponsePayload
    public GetCarEditTypeResponse getCarEditTypeRequest(@RequestPayload GetCarEditTypeRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        GetCarEditTypeResponse response = new GetCarEditTypeResponse();
        response.setEditType(carService.getCarEditType(request.getId(), dto.getId()));
        return response;
    }

    private List<MultipartFile> getFiles(List<PictureInfo> pictureInfos) {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (PictureInfo pictureInfo : pictureInfos) {
            BASE64DecodedMultipartFile base64DecodedMultipartFile = new BASE64DecodedMultipartFile(pictureInfo.getMultiPartFile(), pictureInfo.getFileName());
            multipartFiles.add(base64DecodedMultipartFile);
        }
        return multipartFiles;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "editPartialCarDetailsRequest")
    @ResponsePayload
    public EditPartialCarDetailsResponse editPartialCarDetailsRequest(@RequestPayload EditPartialCarDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        CarEditDTO carEditDTO = editPartialCarDetailsmapper.toEntity(request.getEditPartialCarDetails());
        Car car = carService.editPartial(carEditDTO.getId(), carEditDTO, getFiles(request.getPictureInfo()), dto.getId());

        EditPartialCarDetailsResponse response = new EditPartialCarDetailsResponse();
        response.setEditPartialCarDetails(editPartialCarDetailsAndCarMapper.toDto(car));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllCarDetailsRequest")
    @ResponsePayload
    public GetAllCarDetailsResponse getAll(@RequestPayload GetAllCarDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        List<Car> cars = carService.getAll(dto.getId());
        List<CarWithPictures> carWithPictures = new ArrayList<>();
        for (Car car : cars) {
            CarWithPictures carWithPicture = new CarWithPictures();
            carWithPicture.setCreateCarDetails(carDetailsMapper.toDto(car));
            List<PictureInfo> pictureInfos = getAllPictures(car.getPictures());
            if (!pictureInfos.isEmpty()) {
                carWithPicture.getPictureInfo().addAll(pictureInfos);
            }
            carWithPictures.add(carWithPicture);
        }
        GetAllCarDetailsResponse response = new GetAllCarDetailsResponse();
        response.getCarWithPictures().addAll(carWithPictures);

        return response;
    }

    private List<PictureInfo> getAllPictures(Set<Picture> pictures) {
        List<PictureInfo> pictureInfos = new ArrayList<>();
        for (Picture picture : pictures) {
            PictureInfo pictureInfo = new PictureInfo();
            String name = picture.getData().split("_")[1];
            pictureInfo.setFileName(name);
            pictureInfo.setMultiPartFile(loadImage(picture.getData(), UPLOADED_PICTURES_PATH));
            pictureInfos.add(pictureInfo);
        }
        return pictureInfos;
    }

    public byte[] loadImage(String fileName, String path) {
        try {
            Path fileStorageLocation = Paths.get(path);
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            byte[] fileContent = FileUtils.readFileToByteArray(new File(String.valueOf(filePath)));
            return fileContent;
        } catch (IOException ex) {

        }
        return null;
    }

    @Autowired
    public CarEndpoint(CarService carService, CarDetailsMapper carDetailsMapper, UserClient userClient,
                       EditPartialCarDetailsMapper editPartialCarDetailsmapper,
                       EditPartialCarDetailsAndCarMapper editPartialCarDetailsAndCarMapper, LogProducer logProducer) {
        this.carService = carService;
        this.carDetailsMapper = carDetailsMapper;
        this.userClient = userClient;
        this.editPartialCarDetailsmapper = editPartialCarDetailsmapper;
        this.editPartialCarDetailsmapper = editPartialCarDetailsmapper;
        this.logProducer = logProducer;
    }
}
