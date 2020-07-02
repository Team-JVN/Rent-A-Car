package jvn.Renting.endpoint;

import jvn.Renting.client.UserClient;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.response.UserInfoDTO;
import jvn.Renting.dto.soap.rentreport.*;
import jvn.Renting.dto.soap.rentrequest.CreateRentRequestResponse;
import jvn.Renting.exceptionHandler.InvalidRentReportDataException;
import jvn.Renting.mapper.RentReportDetailsMapper;
import jvn.Renting.model.Comment;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentReport;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.repository.RentInfoRepository;
import jvn.Renting.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class RentReportEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String NAMESPACE_URI = "http://www.soap.dto/rentreport";

    private UserClient userClient;

    private LogProducer logProducer;

    private RentReportService rentReportService;

    private RentReportDetailsMapper rentReportDetailsMapper;

    private RentInfoRepository rentInfoRepository;


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createRentReportRequest")
    @ResponsePayload
    public CreateRentReportResponse createRentReport(@RequestPayload CreateRentReportRequest request) {

        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        RentReportDetails rentReportDetails = request.getRentReportDetails();
        rentReportDetails.setId(null);
        try {
            RentReport rentReport = rentReportService.create(rentReportDetailsMapper.toEntity(rentReportDetails),
                    request.getRentInfoId());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ",
                    String.format("[SOAP] User %s successfully created rent report %s", dto.getId(), rentReport.getId())));
            rentReportDetails = rentReportDetailsMapper.toDto(rentReport);
        } catch (DateTimeParseException e) {
            return null;
        }

        CreateRentReportResponse response = new CreateRentReportResponse();
        response.setRentReportDetails(rentReportDetails);

        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkIfCanCreateRentReportRequest")
    @ResponsePayload
    public CheckIfCanCreateRentReportResponse checkIfCanCreateRentReport(@RequestPayload CheckIfCanCreateRentReportRequest request) {

        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        boolean status = true;
        try {
            RentInfo rentInfo = rentInfoRepository.findOneById(request.getRentInfoId());
            if (rentInfo == null) {
                status = false;
            } else {
                rentReportService.checkIfCreatingRentReportIsPossible(rentInfo);
            }
        } catch (InvalidRentReportDataException e) {
            status = false;
        }

        CheckIfCanCreateRentReportResponse response = new CheckIfCanCreateRentReportResponse();
        response.setValue(status);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllRentReportsDetailsRequest")
    @ResponsePayload
    public GetAllRentReportsDetailsResponse getRentReports(@RequestPayload GetAllRentReportsDetailsRequest request) {

        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        GetAllRentReportsDetailsResponse response = new GetAllRentReportsDetailsResponse();
        RentReport rentReport = rentReportService.getRentReports(request.getRentInfoId());
        if(rentReport == null){
            return null;
        }
        response.setRentReportDetails(rentReportDetailsMapper.toDto(rentReport));
        return response;
    }


    @Autowired
    public RentReportEndpoint(UserClient userClient, LogProducer logProducer, RentReportService rentReportService,
                              RentReportDetailsMapper rentReportDetailsMapper, RentInfoRepository rentInfoRepository) {
        this.userClient = userClient;
        this.logProducer = logProducer;
        this.rentReportService = rentReportService;
        this.rentReportDetailsMapper = rentReportDetailsMapper;
        this.rentInfoRepository = rentInfoRepository;
    }
}
