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
    // private Resource truststore;

    // @Value("${server.ssl.trust-store-type}")
    // private String truststoreType;

    // @Value("${server.ssl.trust-store-password}")
    // private String truststorePassword;

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
        client.setDefaultUri("http://zuul:8080/users/ws");
        client.setMarshaller(marshallerClient);
        client.setUnmarshaller(marshallerClient);
        // client.setMessageSender(httpComponentsMessageSenderCar());
        return client;
    }

    // @Bean
    // @Qualifier("webServiceTemplateClient")
    // public WebServiceTemplate webServiceTemplateClient() throws Exception {
    //     WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
    //     webServiceTemplate.setMarshaller(marshallerClient());
    //     webServiceTemplate.setUnmarshaller(marshallerClient());
    //     webServiceTemplate.setDefaultUri("https://localhost:8080/users/ws");
    //     // set a httpsUrlConnectionMessageSender to handle the HTTPS session
    //     // webServiceTemplate.setMessageSender(httpsUrlConnectionMessageSenderClient());

    //     return webServiceTemplate;
    // }

    // @Bean
    // public HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSenderClient() throws Exception {
    //     HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender =
    //             new HttpsUrlConnectionMessageSender();
    //     httpsUrlConnectionMessageSender.setTrustManagers(trustManagersFactoryBeanClient().getObject());
    //     httpsUrlConnectionMessageSender.setHostnameVerifier(new HostnameVerifier() {
    //         @Override
    //         public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
    //             if ("localhost".equals(hostname)) {
    //                 return true;
    //             }
    //             return false;
    //         }
    //     });

    //     return httpsUrlConnectionMessageSender;
    // }

    // @Bean
    // public KeyStoreFactoryBean trustStoreClient() {
    //     KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
    //     keyStoreFactoryBean.setLocation(truststore);
    //     keyStoreFactoryBean.setPassword(truststorePassword);

    //     return keyStoreFactoryBean;
    // }

    // @Bean
    // public TrustManagersFactoryBean trustManagersFactoryBeanClient() {
    //     TrustManagersFactoryBean trustManagersFactoryBean = new TrustManagersFactoryBean();
    //     trustManagersFactoryBean.setKeyStore(trustStoreClient().getObject());

    //     return trustManagersFactoryBean;
    // }
}
