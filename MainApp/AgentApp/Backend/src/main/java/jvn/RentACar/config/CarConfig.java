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

    // @Value("${server.ssl.algorithm}")
    // private String algorithm;

    // @Value("${server.ssl.key-store}")
    // private String keystore;

    // @Value("${server.ssl.key-store-type}")
    // private String keystoreType;

    // @Value("${server.ssl.key-store-password}")
    // private String keystorePassword;

    // @Value("${server.ssl.key-alias}")
    // private String applicationKeyAlias;

    // @Value("${server.ssl.trust-store}")
    // private String truststore;

    // @Value("${server.ssl.trust-store-type}")
    // private String truststoreType;

    // @Value("${server.ssl.trust-store-password}")
    // private String truststorePassword;

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
        client.setDefaultUri("https://localhost:8080/cars/car/ws");
        client.setMarshaller(marshallerCar);
        client.setUnmarshaller(marshallerCar);
        // client.setMessageSender(httpComponentsMessageSenderCar());
        return client;
    }

    // @Bean
    // public HttpComponentsMessageSender httpComponentsMessageSenderCar()  {
    //     try{
    //         HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
    //         httpComponentsMessageSender.setHttpClient(httpClientCar());

    //         return httpComponentsMessageSender;
    //     }catch (Exception e){
    //         System.out.println("HAJAJAJA");
    //     }
    //     return null;
    // }

    // @Bean
    // public HttpClient httpClientCar() {
    //     try {
    //         KeyStore keyStore = KeyStore.getInstance(keystoreType);
    //         keyStore.load(new FileInputStream(new File(keystore)), keystorePassword.toCharArray());

    //         KeyStore trustStore = KeyStore.getInstance(truststoreType);
    //         trustStore.load(new FileInputStream(new File(truststore)), truststorePassword.toCharArray());

    //         SSLContext sslcontext = SSLContexts.custom()
    //                 .loadTrustMaterial(trustStore, null)
    //                 .loadKeyMaterial(keyStore, keystorePassword.toCharArray(), (aliases, socket) -> applicationKeyAlias)
    //                 .build();

    //         SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslcontext,
    //                 new String[]{algorithm},
    //                 null, (hostname, sslSession) -> true);

    //         return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    //     } catch (Exception e) {
    //         throw new IllegalStateException("Error while configuring rest template", e);
    //     }
    // }
}
