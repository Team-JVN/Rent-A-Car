package jvn.RentACar.config;
import javax.net.ssl.HostnameVerifier;

import jvn.RentACar.client.PriceListClient;
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
public class PriceListConfig {

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
    @Qualifier("marshallerPriceList")
    public Jaxb2Marshaller marshallerPriceList() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.pricelist");
        return marshaller;
    }

    @Bean
    public PriceListClient priceListClient(@Qualifier("marshallerPriceList") Jaxb2Marshaller marshallerPriceList) throws Exception {
        PriceListClient client = new PriceListClient();
        client.setDefaultUri("http://zuul:8080/advertisements/ws/pricelist");
        client.setMarshaller(marshallerPriceList);
        client.setUnmarshaller(marshallerPriceList);
//        client.setMessageSender(httpComponentsMessageSender());
        return client;
    }
//    @Bean
//    @Qualifier("webServiceTemplatePriceList")
//    public WebServiceTemplate webServiceTemplatePriceList() throws Exception {
//        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
//        webServiceTemplate.setMarshaller(marshallerPriceList());
//        webServiceTemplate.setUnmarshaller(marshallerPriceList());
//        webServiceTemplate.setDefaultUri("https://localhost:8080/advertisements/ws/pricelist");
////        // set a httpsUrlConnectionMessageSender to handle the HTTPS session
////        webServiceTemplate.setMessageSender(httpsUrlConnectionMessageSender());
//
//        return webServiceTemplate;
//    }

//    @Bean
//    public HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender() throws Exception {
//        HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender =
//                new HttpsUrlConnectionMessageSender();
//        httpsUrlConnectionMessageSender.setTrustManagers(trustManagersFactoryBean().getObject());
//        httpsUrlConnectionMessageSender.setHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//                if ("localhost".equals(hostname)) {
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        return httpsUrlConnectionMessageSender;
//    }
//
//    @Bean
//    public KeyStoreFactoryBean trustStore() {
//        KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
//        keyStoreFactoryBean.setLocation(truststore);
//        keyStoreFactoryBean.setPassword(truststorePassword);
//
//        return keyStoreFactoryBean;
//    }
//
//    @Bean
//    public TrustManagersFactoryBean trustManagersFactoryBean() {
//        TrustManagersFactoryBean trustManagersFactoryBean = new TrustManagersFactoryBean();
//        trustManagersFactoryBean.setKeyStore(trustStore().getObject());
//
//        return trustManagersFactoryBean;
//    }
//
//
//    @Bean
//    @Qualifier("webServiceTemplatePriceList")
//    public WebServiceTemplate webServiceTemplatePriceList() throws Exception {
//      WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
//      webServiceTemplate.setMarshaller(marshallerPriceList());
//      webServiceTemplate.setUnmarshaller(marshallerPriceList());
//      webServiceTemplate.setDefaultUri("https://localhost:8080/advertisements/ws/pricelist");
//      webServiceTemplate.setMessageSender(httpComponentsMessageSenderPriceList());
//
//      return webServiceTemplate;
//    }
//
//    @Bean
//    public HttpComponentsMessageSender httpComponentsMessageSenderPriceList() throws Exception {
//      HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
//      httpComponentsMessageSender.setHttpClient(httpClient());
//
//      return httpComponentsMessageSender;
//    }
//
//
////    public SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
////      return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
////    }
//
//
//    @Bean
//    public HttpClient httpClient() {
//        try {
//            Files.list(new File("/etc/keystore/").toPath())
//                    .limit(10)
//                    .forEach(path -> {
//                        System.out.println(path);
//                    });
//            KeyStore keyStore = KeyStore.getInstance(keystoreType);
//            keyStore.load(new FileInputStream(new File(keystore)), keystorePassword.toCharArray());
//            if(keyStore == null){
//                System.out.println("HAJ");
//            }else{
//                if(keyStore.size() == 0){
//                    System.out.println("HAJsssss");
//                }
//            }
//            KeyStore trustStore = KeyStore.getInstance(truststoreType);
//            trustStore.load(new FileInputStream(new File(truststore)), truststorePassword.toCharArray());
//            if(trustStore == null){
//                System.out.println("HAJ");
//            }else{
//                if(trustStore.size() == 0){
//                    System.out.println("HAJsssss");
//                }
//            }
//            SSLContext sslcontext = SSLContexts.custom()
//                    .loadTrustMaterial(trustStore, null)
//                    .loadKeyMaterial(keyStore, keystorePassword.toCharArray(), (aliases, socket) -> applicationKeyAlias)
//                    .build();
//
//            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslcontext,
//                    new String[]{algorithm},
//                    null, (hostname, sslSession) -> true);
//
//            return HttpClients.custom().setSSLSocketFactory(sslFactory).addInterceptorFirst(new RemoveSoapHeadersInterceptor()).build();
//        } catch (Exception e) {
//            throw new IllegalStateException("Error while configuring rest template", e);
//        }
//    }


}
