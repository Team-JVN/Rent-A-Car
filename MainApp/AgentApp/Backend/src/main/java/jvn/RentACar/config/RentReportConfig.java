package jvn.RentACar.config;

import jvn.RentACar.client.RentReportClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class RentReportConfig {
    @Bean
    @Qualifier("marshallerRentReport")
    public Jaxb2Marshaller marshallerRentReport() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.rentreport");
        return marshaller;
    }

    @Bean
    public RentReportClient rentReportClient(Jaxb2Marshaller marshallerRentReport) {
        RentReportClient client = new RentReportClient();
        client.setDefaultUri("http://localhost:8080/renting/ws/rentreport");
        client.setMarshaller(marshallerRentReport);
        client.setUnmarshaller(marshallerRentReport);
        return client;
    }
}
