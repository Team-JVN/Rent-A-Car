package jvn.Renting.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.client.UserClient;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.request.RentRequestStatusDTO;
import jvn.Renting.dto.response.SignedMessageDTO;
import jvn.Renting.dto.response.UserInfoDTO;
import jvn.Renting.dto.soap.rentrequest.*;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.mapper.RentRequestDetailsMapper;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.service.DigitalSignatureService;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class RentRequestEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String USERS_ALIAS = "users";

    private static final String NAMESPACE_URI = "http://www.soap.dto/rentrequest";

    private RentRequestService rentRequestService;

    private RentRequestDetailsMapper rentRequestDetailsMapper;

    private UserClient userClient;

    private LogProducer logProducer;

    private ObjectMapper objectMapper;

    private DigitalSignatureService digitalSignatureService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createRentRequestRequest")
    @ResponsePayload
    public CreateRentRequestResponse createOrEdit(@RequestPayload CreateRentRequestRequest request) {
        UserInfoDTO dto = getUserInfo(request.getEmail());
        if (dto == null) {
            return null;
        }

        RentRequestDetails rentRequestDetails = request.getRentRequestDetails();
        rentRequestDetails.setId(null);
        try {
            RentRequest rentRequest = rentRequestService.create(rentRequestDetailsMapper.toEntity(rentRequestDetails), dto.getId(), true);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ", String.format("[SOAP] User %s successfully created rent request %s", dto.getId(), rentRequest.getId())));
            for (RentInfo rentInfo : rentRequest.getRentInfos()) {
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRI", String.format("[SOAP] User %s successfully created rent info %s", dto.getId(), rentInfo.getId())));
            }
            rentRequestDetails = rentRequestDetailsMapper.toDto(rentRequest);
        } catch (DateTimeParseException | ParseException e) {
            return null;
        }

        CreateRentRequestResponse response = new CreateRentRequestResponse();
        response.setRentRequestDetails(rentRequestDetails);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "changeRentRequestStatusRequest")
    @ResponsePayload
    public ChangeRentRequestStatusResponse changeRentRequestStatus(@RequestPayload ChangeRentRequestStatusRequest request) {
        UserInfoDTO dto = getUserInfo(request.getEmail());
        if (dto == null) {
            return null;
        }
        String status = "SUCCESS";
        try {
            RentRequestStatusDTO rentRequestStatusDTO = new RentRequestStatusDTO();
            rentRequestStatusDTO.setStatus(request.getStatus());
            RentRequest rentRequest = rentRequestService.changeRentRequestStatus(request.getRentRequestId(), rentRequestStatusDTO, dto.getId());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SRQ", String.format("User %s successfully changed rent request %s status to %s", dto.getId(), rentRequest.getId(), rentRequest.getRentRequestStatus().toString())));
        } catch (InvalidRentRequestDataException e) {
            status = "ERROR";
        }

        ChangeRentRequestStatusResponse response = new ChangeRentRequestStatusResponse();
        response.setStatus(status);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkDateRequest")
    @ResponsePayload
    public CheckDateResponse checkDate(@RequestPayload CheckDateRequest request) {
        UserInfoDTO dto = getUserInfo(request.getEmail());
        if (dto == null) {
            return null;
        }
        boolean status = true;
        try {
            LocalDate dateTo = null;
            if (request.getAdvDateTo() != null) {
                dateTo = getLocalDate(request.getAdvDateTo());
            }
            rentRequestService.checkDate(request.getAdvId(), getLocalDate(request.getAdvDateFrom()), dateTo, getLocalDateTime(request.getDateTimeFrom()).toLocalDate(),
                    getLocalDateTime(request.getDateTimeTo()).toLocalDate());
        } catch (InvalidRentRequestDataException e) {
            status = false;
        }

        CheckDateResponse response = new CheckDateResponse();
        response.setValue(status);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkIfCanAcceptRequest")
    @ResponsePayload
    public CheckIfCanAcceptResponse checkIfCanAcceptRentRequest(@RequestPayload CheckIfCanAcceptRequest request) {
        UserInfoDTO dto = getUserInfo(request.getEmail());
        if (dto == null) {
            return null;
        }
        boolean status = true;
        try {
            RentRequest rentRequest = rentRequestService.getRentRequest(request.getRentRequestId());
            if (rentRequest == null) {
                status = false;
            } else {
                rentRequestService.checkIfCanAcceptRentRequest(rentRequest);
            }
        } catch (InvalidRentRequestDataException e) {
            status = false;
        }

        CheckIfCanAcceptResponse response = new CheckIfCanAcceptResponse();
        response.setValue(status);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hasDebtRequest")
    @ResponsePayload
    public HasDebtResponse hasDebt(@RequestPayload HasDebtRequest request) {
        UserInfoDTO dto = getUserInfo(request.getEmail());
        if (dto == null) {
            return null;
        }
        boolean status = true;
        try {
            rentRequestService.hasDebt(request.getId());

        } catch (InvalidRentRequestDataException e) {
            status = false;
        }

        HasDebtResponse response = new HasDebtResponse();
        response.setValue(status);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllRentRequestDetailsRequest")
    @ResponsePayload
    public GetAllRentRequestDetailsResponse getAll(@RequestPayload GetAllRentRequestDetailsRequest request) {
        UserInfoDTO dto = getUserInfo(request.getEmail());
        if (dto == null) {
            return null;
        }

        List<RentRequestDetails> list = rentRequestService.getAll(dto.getId()).stream().map(rentRequestDetailsMapper::toDto).
                collect(Collectors.toList());
        GetAllRentRequestDetailsResponse response = new GetAllRentRequestDetailsResponse();
        response.getRentRequestDetails().addAll(list);
        return response;
    }

    private LocalDate getLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        LocalDate localDate = LocalDate.of(
                xmlGregorianCalendar.getYear(),
                xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay());
        return localDate;
    }

    private LocalDateTime getLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    private UserInfoDTO getUserInfo(String email) {
        UserInfoDTO userInfoDTO = null;
        SignedMessageDTO signedEditType = userClient.getUser(email);
        if (digitalSignatureService.decrypt(USERS_ALIAS, signedEditType.getMessageBytes(), signedEditType.getDigitalSignature())) {
            userInfoDTO = bytesToObject(signedEditType.getMessageBytes());
        } else {
            logProducer.send(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SGN", "Invalid digital signature"));
        }
        return userInfoDTO;
    }

    private UserInfoDTO bytesToObject(byte[] byteArray) {
        try {
            return objectMapper.readValue(byteArray, UserInfoDTO.class);
        } catch (IOException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping byte array to %s failed", UserInfoDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public RentRequestEndpoint(RentRequestService rentRequestService, RentRequestDetailsMapper rentRequestDetailsMapper,
                               UserClient userClient, ObjectMapper objectMapper, LogProducer logProducer,
                               DigitalSignatureService digitalSignatureService) {
        this.rentRequestService = rentRequestService;
        this.userClient = userClient;
        this.rentRequestDetailsMapper = rentRequestDetailsMapper;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
