package jvn.RentACar.client;

import jvn.RentACar.dto.soap.rentreport.*;
import jvn.RentACar.mapper.RentReportDetailsMapper;
import jvn.RentACar.model.RentReport;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class RentReportClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private RentReportDetailsMapper rentReportDetailsMapper;

    public CreateRentReportResponse createRentReport(Long rentInfoId, RentReport rentReport){
        CreateRentReportRequest request = new CreateRentReportRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setRentInfoId(rentInfoId);
        request.setEmail(user.getEmail());
        request.setRentReportDetails(rentReportDetailsMapper.toDto(rentReport));

        CreateRentReportResponse response = (CreateRentReportResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public CheckIfCanCreateRentReportResponse checkIfCanCreateRentReport(Long rentInfoId){
        CheckIfCanCreateRentReportRequest request = new CheckIfCanCreateRentReportRequest();
        request.setRentInfoId(rentInfoId);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CheckIfCanCreateRentReportResponse response = (CheckIfCanCreateRentReportResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);

        return response;
    }

    public GetAllRentReportsDetailsResponse getRentReports(Long rentInfoId){
        GetAllRentReportsDetailsRequest request = new GetAllRentReportsDetailsRequest();
        request.setRentInfoId(rentInfoId);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        GetAllRentReportsDetailsResponse response = (GetAllRentReportsDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);

        return response;
    }
}
