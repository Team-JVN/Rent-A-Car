package jvn.Advertisements.endpoint;

import jvn.Advertisements.client.UserClient;
import jvn.Advertisements.dto.message.Log;
import jvn.Advertisements.dto.response.UserInfoDTO;
import jvn.Advertisements.dto.soap.pricelist.*;
import jvn.Advertisements.mapper.PriceListDetailsMapper;
import jvn.Advertisements.producer.LogProducer;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class PriceListEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String NAMESPACE_URI = "http://www.soap.dto/pricelist";

    private PriceListService priceListService;

    private PriceListDetailsMapper priceListDetailsMapper;

    private UserClient userClient;

    private LogProducer logProducer;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPriceListDetailsRequest")
    @ResponsePayload
    public GetPriceListDetailsResponse createOrEdit(@RequestPayload GetPriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        PriceListDetails priceListDetails = request.getPriceListDetails();
        if (priceListDetails.getId() != null) {
            priceListDetails = priceListDetailsMapper.toDto(priceListService.edit(priceListDetails.getId(), priceListDetailsMapper.toEntity(priceListDetails), dto.getId()));
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EPL", String.format("[SOAP] User %s successfully edited price list %s", dto.getId(), priceListDetails.getId())));
        } else {
            priceListDetails = priceListDetailsMapper.toDto(priceListService.create(priceListDetailsMapper.toEntity(priceListDetails), dto.getId()));
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPL", String.format("[SOAP] User %s successfully created price list %s", dto.getId(), priceListDetails.getId())));
        }
        GetPriceListDetailsResponse response = new GetPriceListDetailsResponse();
        response.setPriceListDetails(priceListDetails);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deletePriceListDetailsRequest")
    @ResponsePayload
    public DeletePriceListDetailsResponse deletePriceList(@RequestPayload DeletePriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        DeletePriceListDetailsResponse response = new DeletePriceListDetailsResponse();
        boolean isDeleted = priceListService.checkIfCanDeleteAndDelete(request.getId(), dto.getId());
        response.setCanDelete(isDeleted);
        if (isDeleted) {
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DPL", String.format("[SOAP] User %s successfully deleted price list %s", dto.getId(), request.getId())));
        }
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllPriceListDetailsRequest")
    @ResponsePayload
    public GetAllPriceListDetailsResponse getAllPriceListDetails(@RequestPayload GetAllPriceListDetailsRequest request) {
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

    @Autowired
    public PriceListEndpoint(PriceListService priceListService, PriceListDetailsMapper priceListDetailsMapper, UserClient userClient,
                             LogProducer logProducer) {
        this.priceListService = priceListService;
        this.userClient = userClient;
        this.priceListDetailsMapper = priceListDetailsMapper;
        this.logProducer = logProducer;
    }
}
