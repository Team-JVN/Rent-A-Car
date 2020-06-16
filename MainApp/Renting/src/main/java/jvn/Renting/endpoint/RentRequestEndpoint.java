package jvn.Renting.endpoint;

import jvn.Renting.client.UserClient;
import jvn.Renting.dto.response.UserInfoDTO;
import jvn.Renting.dto.soap.rentrequest.CreateRentRequestRequest;
import jvn.Renting.dto.soap.rentrequest.CreateRentRequestResponse;
import jvn.Renting.dto.soap.rentrequest.RentRequestDetails;
import jvn.Renting.mapper.RentRequestDetailsMapper;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.text.ParseException;
import java.time.format.DateTimeParseException;

@Endpoint
public class RentRequestEndpoint {
    private static final String NAMESPACE_URI = "http://www.soap.dto/rentrequest";

    private RentRequestService rentRequestService;

    private RentRequestDetailsMapper rentRequestDetailsMapper;

    private UserClient userClient;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createRentRequestRequest")
    @ResponsePayload
    public CreateRentRequestResponse createOrEdit(@RequestPayload CreateRentRequestRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        RentRequestDetails rentRequestDetails = request.getRentRequestDetails();
        rentRequestDetails.setId(null);
        try {
            rentRequestDetails = rentRequestDetailsMapper.toDto(rentRequestService.create(rentRequestDetailsMapper.toEntity(rentRequestDetails),
                    dto.getId(), true));
        } catch (DateTimeParseException | ParseException e) {
            return null;
        }

        CreateRentRequestResponse response = new CreateRentRequestResponse();
        response.setRentRequestDetails(rentRequestDetails);
        return response;
    }

/*
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deletePriceListDetailsRequest")
    @ResponsePayload
    public DeletePriceListDetailsResponse deletePriceList(@RequestPayload DeletePriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        DeletePriceListDetailsResponse response = new DeletePriceListDetailsResponse();
        response.setCanDelete(priceListService.checkIfCanDeleteAndDelete(request.getId(), dto.getId()));
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllPriceListDetailsRequest")
    @ResponsePayload
    public GetAllPriceListDetailsResponse createOrEdit(@RequestPayload GetAllPriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        List<PriceListDetails> list = priceListService.getPriceListsDeletedAndExisting(dto.getId()).stream().map(priceListDetailsMapper::toDto).
                collect(Collectors.toList());
        GetAllPriceListDetailsResponse response = new GetAllPriceListDetailsResponse();
        response.getPriceListDetails().addAll(list);
        return response;
    }
 */

    @Autowired
    public RentRequestEndpoint(RentRequestService rentRequestService, RentRequestDetailsMapper rentRequestDetailsMapper, UserClient userClient) {
        this.rentRequestService = rentRequestService;
        this.userClient = userClient;
        this.rentRequestDetailsMapper = rentRequestDetailsMapper;
    }
}
