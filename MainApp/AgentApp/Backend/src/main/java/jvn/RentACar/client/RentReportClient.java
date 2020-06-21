package jvn.RentACar.client;

import jvn.RentACar.dto.soap.rentreport.CreateRentReportRequest;
import jvn.RentACar.dto.soap.rentreport.CreateRentReportResponse;
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
}
