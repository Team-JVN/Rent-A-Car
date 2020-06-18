package jvn.RentACar.client;

import jvn.RentACar.dto.soap.advertisement.*;
import jvn.RentACar.mapper.AdvertisementDetailsMapper;
import jvn.RentACar.mapper.EditPartialAdvertisementMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class AdvertisementClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertisementDetailsMapper advertisementDetailsMapper;

    @Autowired
    private EditPartialAdvertisementMapper editPartialAdvertisementMapper;

    public CreateOrEditAdvertisementDetailsResponse createOrEdit(Advertisement advertisement) {

        CreateOrEditAdvertisementDetailsRequest request = new CreateOrEditAdvertisementDetailsRequest();
        request.setAdvertisementDetails(advertisementDetailsMapper.toDto(advertisement));
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CreateOrEditAdvertisementDetailsResponse response = (CreateOrEditAdvertisementDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public DeleteAdvertisementDetailsResponse checkAndDeleteIfCan(Advertisement advertisement) {
        DeleteAdvertisementDetailsRequest request = new DeleteAdvertisementDetailsRequest();
        request.setId(advertisement.getMainAppId());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        DeleteAdvertisementDetailsResponse response = (DeleteAdvertisementDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAdvertisementEditTypeResponse getEditType(Advertisement advertisement) {
        GetAdvertisementEditTypeRequest request = new GetAdvertisementEditTypeRequest();
        request.setId(advertisement.getMainAppId());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        GetAdvertisementEditTypeResponse response = (GetAdvertisementEditTypeResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public EditPartialAdvertisementDetailsResponse editPartial(Advertisement advertisement) {

        EditPartialAdvertisementDetailsRequest request = new EditPartialAdvertisementDetailsRequest();
        request.setEditPartialAdvertisementDetails(editPartialAdvertisementMapper.toDto(advertisement));
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        EditPartialAdvertisementDetailsResponse response = (EditPartialAdvertisementDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAllAdvertisementDetailsResponse getAll() {
        GetAllAdvertisementDetailsRequest request = new GetAllAdvertisementDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());

        GetAllAdvertisementDetailsResponse response = (GetAllAdvertisementDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

}
