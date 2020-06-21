package jvn.Renting.endpoint;

import jvn.Renting.client.UserClient;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.response.UserInfoDTO;
import jvn.Renting.dto.soap.rentreport.CreateRentReportRequest;
import jvn.Renting.dto.soap.rentreport.CreateRentReportResponse;
import jvn.Renting.dto.soap.rentreport.RentReportDetails;
import jvn.Renting.dto.soap.rentrequest.CreateRentRequestResponse;
import jvn.Renting.mapper.RentReportDetailsMapper;
import jvn.Renting.model.Comment;
import jvn.Renting.model.RentReport;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeParseException;

@Endpoint
public class RentReportEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String NAMESPACE_URI = "http://www.soap.dto/rentreport";

    private UserClient userClient;

    private LogProducer logProducer;

    private RentReportService rentReportService;

    private RentReportDetailsMapper rentReportDetailsMapper;


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

    @Autowired
    public RentReportEndpoint(UserClient userClient, LogProducer logProducer, RentReportService rentReportService,
                              RentReportDetailsMapper rentReportDetailsMapper) {
        this.userClient = userClient;
        this.logProducer = logProducer;
        this.rentReportService = rentReportService;
        this.rentReportDetailsMapper = rentReportDetailsMapper;
    }
}
