package jvn.RentACar.client;

import jvn.RentACar.dto.soap.car.CreateOrEditCarDetailsRequest;
import jvn.RentACar.dto.soap.car.CreateOrEditCarDetailsResponse;
import jvn.RentACar.dto.soap.car.PictureInfo;
import jvn.RentACar.mapper.CarDetailsMapper;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private CarDetailsMapper carDetailsMapper;

    public CreateOrEditCarDetailsResponse createOrEdit(Car car, List<MultipartFile> multipartFiles) {

        CreateOrEditCarDetailsRequest request = new CreateOrEditCarDetailsRequest();
        request.setCreateCarDetails(carDetailsMapper.toDto(car));
        try {
            request.getPictureInfo().addAll(getPicturesInfo(multipartFiles));
        } catch (IOException e) {
            return null;
        }

        User user = userService.getLoginUser();
        if(user == null){
            return null;
        }
        request.setEmail(user.getEmail());
        CreateOrEditCarDetailsResponse response = (CreateOrEditCarDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    private List<PictureInfo> getPicturesInfo(List<MultipartFile> multipartFiles) throws IOException {
        List<PictureInfo> pictureInfos = new ArrayList<>();
        for(MultipartFile multipartFile:multipartFiles){
            PictureInfo pictureInfo = new PictureInfo();
            pictureInfo.setMultiPartFile(multipartFile.getBytes());
            pictureInfo.setFileName(multipartFile.getOriginalFilename());
            pictureInfos.add(pictureInfo);
        }
        return pictureInfos;
    }

}
