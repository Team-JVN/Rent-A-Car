package jvn.RentACar.client;

import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.dto.soap.advertisement.CheckIfCarIsAvailableRequest;
import jvn.RentACar.dto.soap.advertisement.CheckIfCarIsAvailableResponse;
import jvn.RentACar.dto.soap.comment.*;
import jvn.RentACar.dto.soap.rentrequest.*;
import jvn.RentACar.dto.soap.rentrequest.CommentDetails;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.mapper.CommentDetailsMapper;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.mapper.MessageDetailsMapper;
import jvn.RentACar.mapper.RentRequestDetailsMapper;
import jvn.RentACar.model.Comment;
import jvn.RentACar.model.Message;
import jvn.RentACar.model.RentRequest;
import jvn.RentACar.model.User;
import jvn.RentACar.service.CommentService;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RentRequestClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private RentRequestDetailsMapper rentRequestDetailsMapper;

    @Autowired
    private CommentDetailsMapper commentDetailsMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private MessageDetailsMapper messageDetailsMapper;

    public CreateRentRequestResponse createOrEdit(RentRequest rentRequest) {
        RentRequestDetails rentRequestDetails = rentRequestDetailsMapper.toDto(rentRequest);

        CreateRentRequestRequest request = new CreateRentRequestRequest();
        request.setRentRequestDetails(rentRequestDetails);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CreateRentRequestResponse response = (CreateRentRequestResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public ChangeRentRequestStatusResponse changeRentRequestStatus(Long rentRequestId, RentRequestStatus newStatus) {
        ChangeRentRequestStatusRequest request = new ChangeRentRequestStatusRequest();
        request.setRentRequestId(rentRequestId);
        request.setStatus(newStatus.toString());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        ChangeRentRequestStatusResponse response = (ChangeRentRequestStatusResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public CheckDateResponse checkDate(Long advId, LocalDate advDateFrom, LocalDate advDateTo, LocalDateTime dateTimeFrom,
                                       LocalDateTime dateTimeTo) {
        CheckDateRequest request = new CheckDateRequest();
        request.setAdvId(advId);
        request.setAdvDateFrom(getXMLGregorianCalendar(advDateFrom));
        if (advDateTo != null) {
            request.setAdvDateTo(getXMLGregorianCalendar(advDateTo));
        } else {
            request.setAdvDateTo(null);
        }

        request.setDateTimeFrom(getXMLGregorianCalendar(dateTimeFrom));
        request.setDateTimeTo(getXMLGregorianCalendar(dateTimeTo));
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CheckDateResponse response = (CheckDateResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public CheckIfCanAcceptResponse checkIfCanAccept(Long rentRequestId) {
        CheckIfCanAcceptRequest request = new CheckIfCanAcceptRequest();
        request.setRentRequestId(rentRequestId);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CheckIfCanAcceptResponse response = (CheckIfCanAcceptResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public HasDebtResponse hasDebt(Long id) {
        HasDebtRequest request = new HasDebtRequest();
        request.setId(id);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        HasDebtResponse response = (HasDebtResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAllRentRequestDetailsResponse getAll() {
        GetAllRentRequestDetailsRequest request = new GetAllRentRequestDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());

        GetAllRentRequestDetailsResponse response = (GetAllRentRequestDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(LocalDateTime localDateTime) {
        try {
            String dateTimeStr = localDateTime.toString();
            if (localDateTime.getSecond() == 0 && localDateTime.getNano() == 0) {
                dateTimeStr += ":00"; // necessary hack because the second part is not optional in XML
            }
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeStr);
        } catch (DatatypeConfigurationException | IllegalArgumentException e) {
            throw new InvalidRentRequestDataException("Invalid date and time format.", HttpStatus.BAD_REQUEST);
        }
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(LocalDate localDate) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }
}