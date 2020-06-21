package jvn.RentACar.config;

import jvn.RentACar.client.ClientClient;

import javax.net.ssl.HostnameVerifier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

@Configuration
public class ClientConfig {

    @Bean
    @Qualifier("marshallerClient")
    public Jaxb2Marshaller marshallerClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.client");
        return marshaller;
    }

    @Bean
    public ClientClient clientClient(@Qualifier("marshallerClient") Jaxb2Marshaller marshallerClient) {
        ClientClient client = new ClientClient();
//       client.setDefaultUri("http://users:8084/ws");
        client.setDefaultUri("http://localhost:8084/ws");
        client.setMarshaller(marshallerClient);
        client.setUnmarshaller(marshallerClient);
        return client;
    }
}
