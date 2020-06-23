package jvn.RentACar.client;

import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.dto.soap.car.*;
import jvn.RentACar.mapper.CarDetailsMapper;
import jvn.RentACar.mapper.CarEditDetailsMapper;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CarClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private CarDetailsMapper carDetailsMapper;

    @Autowired
    private CarEditDetailsMapper carEditDetailsMapper;

    public CreateOrEditCarDetailsResponse createOrEdit(Car car, List<MultipartFile> multipartFiles, Boolean create) {

        CreateOrEditCarDetailsRequest request = new CreateOrEditCarDetailsRequest();
        request.setCreateCarDetails(carDetailsMapper.toDto(car));
        try {
            if (create) {
                List<PictureInfo> pictureInfos = new ArrayList<>();

                for (MultipartFile multipartFile : multipartFiles) {
                    PictureInfo pictureInfo = new PictureInfo();
                    pictureInfo.setMultiPartFile(multipartFile.getBytes());
                    pictureInfo.setFileName(multipartFile.getOriginalFilename());
                    pictureInfos.add(pictureInfo);
                }
                request.getPictureInfo().addAll(pictureInfos);
            } else {
                request.getPictureInfo().addAll(getPicturesInfo(multipartFiles));
            }

        } catch (IOException e) {
            return null;
        }

        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CreateOrEditCarDetailsResponse response = (CreateOrEditCarDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    private List<PictureInfo> getPicturesInfo(List<MultipartFile> multipartFiles) throws IOException {
        List<PictureInfo> pictureInfos = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            PictureInfo pictureInfo = new PictureInfo();
            pictureInfo.setMultiPartFile(multipartFile.getBytes());
            int index = Objects.requireNonNull(multipartFile.getOriginalFilename()).indexOf('_');
            if (index != -1) {
                pictureInfo.setFileName(multipartFile.getOriginalFilename().substring(index + 1));
            } else {
                pictureInfo.setFileName(multipartFile.getOriginalFilename());
            }
            pictureInfos.add(pictureInfo);
        }
        return pictureInfos;
    }


    public DeleteCarDetailsResponse checkAndDeleteIfCan(Car car) {
        DeleteCarDetailsRequest request = new DeleteCarDetailsRequest();
        request.setId(car.getMainAppId());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        DeleteCarDetailsResponse response = (DeleteCarDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetCarEditTypeResponse getEditType(Car car) {
        GetCarEditTypeRequest request = new GetCarEditTypeRequest();
        request.setId(car.getMainAppId());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        GetCarEditTypeResponse response = (GetCarEditTypeResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }


    public EditPartialCarDetailsResponse editPartial(CarEditDTO carEditDTO, List<MultipartFile> multipartFiles) {

        EditPartialCarDetailsRequest request = new EditPartialCarDetailsRequest();
        request.setEditPartialCarDetails(carEditDetailsMapper.toDto(carEditDTO));
        try {
            request.getPictureInfo().addAll(getPicturesInfo(multipartFiles));
        } catch (IOException e) {
            return null;
        }

        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        EditPartialCarDetailsResponse response = (EditPartialCarDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAllCarDetailsResponse getAll() {
        GetAllCarDetailsRequest request = new GetAllCarDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());

        GetAllCarDetailsResponse response = (GetAllCarDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }
}
