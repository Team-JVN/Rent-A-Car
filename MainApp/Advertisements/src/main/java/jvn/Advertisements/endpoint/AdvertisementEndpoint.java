package jvn.Advertisements.endpoint;

import jvn.Advertisements.client.UserClient;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.dto.response.UserInfoDTO;
import jvn.Advertisements.dto.soap.advertisement.*;
import jvn.Advertisements.mapper.AdvertisementDetailsMapper;
import jvn.Advertisements.mapper.EditPartialAdvertisementMapper;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.service.AdvertisementService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class AdvertisementEndpoint {

    private static final String NAMESPACE_URI = "http://www.soap.dto/advertisement";

    private AdvertisementService advertisementService;

    private AdvertisementDetailsMapper advertisementDetailsMapper;

    private UserClient userClient;

    private EditPartialAdvertisementMapper editPartialAdvertisementMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrEditAdvertisementDetailsRequest")
    @ResponsePayload
    public CreateOrEditAdvertisementDetailsResponse createOrEdit(@RequestPayload CreateOrEditAdvertisementDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        UserDTO userDTO = getUserDTO(dto);
        AdvertisementDetails advertisementDetails = request.getAdvertisementDetails();
        if (advertisementDetails.getId() != null) {
            advertisementDetails = advertisementDetailsMapper.toDto(advertisementService.edit(advertisementDetails.getId(), advertisementDetailsMapper.toEntity(advertisementDetails), userDTO));
        } else {
            advertisementDetails = advertisementDetailsMapper.toDto(advertisementService.create(advertisementDetailsMapper.toEntity(advertisementDetails), userDTO));
        }
        CreateOrEditAdvertisementDetailsResponse response = new CreateOrEditAdvertisementDetailsResponse();
        response.setAdvertisementDetails(advertisementDetails);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAdvertisementDetailsRequest")
    @ResponsePayload
    public DeleteAdvertisementDetailsResponse delete(@RequestPayload DeleteAdvertisementDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        DeleteAdvertisementDetailsResponse response = new DeleteAdvertisementDetailsResponse();
        response.setCanDelete(advertisementService.checkIfCanDeleteAndDelete(request.getId(), dto.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "editPartialAdvertisementDetailsRequest")
    @ResponsePayload
    public EditPartialAdvertisementDetailsResponse editPartial(@RequestPayload EditPartialAdvertisementDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        AdvertisementEditDTO advertisementEditDTO = editPartialAdvertisementMapper.toEntity(request.getEditPartialAdvertisementDetails());
        Advertisement advertisement = advertisementService.editPartial(advertisementEditDTO.getId(), advertisementEditDTO, dto.getId());

        EditPartialAdvertisementDetailsResponse response = new EditPartialAdvertisementDetailsResponse();
        EditPartialAdvertisementDetails details = new EditPartialAdvertisementDetails();
        details.setId(advertisement.getId());
        details.setPriceList(advertisement.getPriceList().getId());
        details.setKilometresLimit(advertisement.getKilometresLimit());
        details.setKilometresLimit(advertisement.getKilometresLimit());
        details.setDiscount(advertisement.getDiscount());
        response.setEditPartialAdvertisementDetails(details);

        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAdvertisementEditTypeRequest")
    @ResponsePayload
    public GetAdvertisementEditTypeResponse getAdvertisementEditType(@RequestPayload GetAdvertisementEditTypeRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        GetAdvertisementEditTypeResponse response = new GetAdvertisementEditTypeResponse();
        response.setEditType(advertisementService.getAdvertisementEditType(request.getId(), dto.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllAdvertisementDetailsRequest")
    @ResponsePayload
    public GetAllAdvertisementDetailsResponse getAll(@RequestPayload GetAllAdvertisementDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        List<AdvertisementDetails> list = advertisementService.getAll(dto.getId()).stream().map(advertisementDetailsMapper::toDto).
                collect(Collectors.toList());
        GetAllAdvertisementDetailsResponse response = new GetAllAdvertisementDetailsResponse();
        response.getAdvertisementDetails().addAll(list);
        return response;
    }

    private UserDTO getUserDTO(UserInfoDTO userInfoDTO) {
        UserDTO dto = new UserDTO();
        dto.setId(userInfoDTO.getId());
        dto.setName(userInfoDTO.getName());
        dto.setEmail(userInfoDTO.getEmail());
        dto.setRole(userInfoDTO.getRole());
        dto.setCanCreateComments(userInfoDTO.getCanCreateComments());
        dto.setCanCreateRentRequests(userInfoDTO.getCanCreateRentRequests());
        return dto;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkIfCarIsAvailableRequest")
    @ResponsePayload
    public CheckIfCarIsAvailableResponse checkIfCarIsAvailable(@RequestPayload CheckIfCarIsAvailableRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        LocalDate dateFrom = getLocalDate(request.getDateFrom());
        LocalDate dateTo = null;
        if (request.getDateTo() != null) {
            dateTo = getLocalDate(request.getDateTo());
        }
        CheckIfCarIsAvailableResponse response = new CheckIfCarIsAvailableResponse();
        response.setAvailable(advertisementService.checkIfCarIsAvailableForSoap(request.getCarId(), dateFrom, dateTo));
        return response;
    }


    private LocalDate getLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        LocalDate localDate = LocalDate.of(
                xmlGregorianCalendar.getYear(),
                xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay());
        return localDate;
    }

    @Autowired
    public AdvertisementEndpoint(AdvertisementService advertisementService, AdvertisementDetailsMapper advertisementDetailsMapper, UserClient userClient,
                                 EditPartialAdvertisementMapper editPartialAdvertisementMapper) {
        this.advertisementService = advertisementService;
        this.advertisementDetailsMapper = advertisementDetailsMapper;
        this.userClient = userClient;
        this.editPartialAdvertisementMapper = editPartialAdvertisementMapper;
    }
}
