package jvn.RentACar.config;

import org.apache.http.impl.client.HttpClientBuilder;
import jvn.RentACar.client.AdvertisementClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import javax.net.ssl.SSLContext;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;


@Configuration
public class AdvertisementConfig {

    @Bean
    @Qualifier("marshallerAdvertisement")
    public Jaxb2Marshaller marshallerAdvertisement() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.advertisement");
        return marshaller;
    }

    @Bean
    public AdvertisementClient advertisementClient(@Qualifier("marshallerAdvertisement") Jaxb2Marshaller marshallerAdvertisement) throws Exception {
        AdvertisementClient client = new AdvertisementClient();
        client.setDefaultUri("http://localhost:8080/advertisements/ws/advertisement");
        client.setMarshaller(marshallerAdvertisement);
        client.setUnmarshaller(marshallerAdvertisement);
//        client.setMessageSender(httpComponentsMessageSender());
        return client;
    }

//    @Bean
//    public HttpComponentsMessageSender httpComponentsMessageSender() throws Exception {
//        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
//        httpComponentsMessageSender.setHttpClient(httpClient());
//
//        return httpComponentsMessageSender;
//    }
//
//    public HttpClient httpClient() throws Exception {
//        return HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory())
//                .addInterceptorFirst(new RemoveSoapHeadersInterceptor()).build();
//    }
//
//    public SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
//        return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
//    }
//
//    public SSLContext sslContext() throws Exception {
//        return SSLContextBuilder.create()
//                .loadTrustMaterial(trustStore.getFile(), trustStorePassword.toCharArray()).build();
//    }
}
