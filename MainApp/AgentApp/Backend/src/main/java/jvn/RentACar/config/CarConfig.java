package jvn.RentACar.config;

import jvn.RentACar.client.CarClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class CarConfig {


    @Bean
    @Qualifier("marshallerCar")
    public Jaxb2Marshaller marshallerCar() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.car");
        return marshaller;
    }

    @Bean
    public CarClient carClient(@Qualifier("marshallerCar") Jaxb2Marshaller marshallerCar) {
        CarClient client = new CarClient();
//        client.setDefaultUri("http://cars:8082/car/ws");
        client.setDefaultUri("http://localhost:8082/car/ws");
        client.setMarshaller(marshallerCar);
        client.setUnmarshaller(marshallerCar);
        return client;
    }
}
