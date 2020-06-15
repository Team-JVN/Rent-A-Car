package jvn.Zuul;

import jvn.Zuul.com.baeldung.springsoap.gen.Country;
import jvn.Zuul.com.baeldung.springsoap.gen.Currency;
import jvn.Zuul.com.baeldung.springsoap.gen.GetCountryRequest;
import jvn.Zuul.com.baeldung.springsoap.gen.GetCountryResponse;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CountryEndpoint {

    private static final String NAMESPACE_URI = "http://www.baeldung.com/springsoap/gen";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
        GetCountryResponse response = new GetCountryResponse();
        Country country = new Country();
        country.setName("sss");
        country.setCapital("ssssss");

        country.setCurrency(Currency.EUR);
        response.setCountry(country);

        return response;
    }

    public CountryEndpoint() {

    }
}
