package jvn.RentACar.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import jvn.RentACar.client.PriceListClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class PriceListConfig {


    @Bean
    @Qualifier("marshallerPriceList")
    public Jaxb2Marshaller marshallerPriceList() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.pricelist");
        return marshaller;
    }


    @Bean
    public PriceListClient pricelistClient(@Qualifier("marshallerPriceList") Jaxb2Marshaller marshallerPriceList) throws Exception {
        PriceListClient client = new PriceListClient();
//       client.setDefaultUri("http://advertisements:8081/ws/pricelist");
        client.setDefaultUri("http://localhost:8081/ws/pricelist");
        client.setMarshaller(marshallerPriceList);
        client.setUnmarshaller(marshallerPriceList);
        return client;
    }

}
