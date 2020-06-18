package jvn.Advertisements;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {
    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "priceLists")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema priceListsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PriceListsPort");
        wsdl11Definition.setLocationUri("/ws/pricelist");
        wsdl11Definition.setTargetNamespace("http://www.pricelist.dto/soap");
        wsdl11Definition.setSchema(priceListsSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema priceListsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("priceLists.xsd"));
    }

    @Bean(name = "deletePriceLists")
    public DefaultWsdl11Definition defaultWsdl11DefinitionDeletePriceLists(@Qualifier("deletePriceListsSchema") XsdSchema deletePriceLists) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PriceListsPort");
        wsdl11Definition.setLocationUri("/ws/pricelist");
        wsdl11Definition.setTargetNamespace("http://www.pricelist.dto/soap");
        wsdl11Definition.setSchema(deletePriceLists);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("deletePriceListsSchema")
    public XsdSchema deletePriceListsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("deletePriceLists.xsd"));
    }

    @Bean(name = "listPriceList")
    public DefaultWsdl11Definition defaultWsdl11DefinitionListPriceList(@Qualifier("listPriceListSchema") XsdSchema listPriceListSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PriceListsPort");
        wsdl11Definition.setLocationUri("/ws/pricelist");
        wsdl11Definition.setTargetNamespace("http://www.pricelist.dto/soap");
        wsdl11Definition.setSchema(listPriceListSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listPriceListSchema")
    public XsdSchema listPriceListSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listPriceList.xsd"));
    }


    @Bean(name = "createOrEditAdvertisement")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCreateOrEditAdvertisement(@Qualifier("createOrEditAdvertisement") XsdSchema createOrEditAdvertisement) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AdvertisementsPort");
        wsdl11Definition.setLocationUri("/ws/advertisement");
        wsdl11Definition.setTargetNamespace("http://www.advertisement.dto/soap");
        wsdl11Definition.setSchema(createOrEditAdvertisement);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("createOrEditAdvertisement")
    public XsdSchema createOrEditAdvertisementSchema() {
        return new SimpleXsdSchema(new ClassPathResource("createOrEditAdvertisement.xsd"));
    }

    @Bean(name = "deleteAdvertisement")
    public DefaultWsdl11Definition defaultWsdl11DefinitionDeleteAdvertisement(@Qualifier("deleteAdvertisement") XsdSchema deleteAdvertisement) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AdvertisementsPort");
        wsdl11Definition.setLocationUri("/ws/advertisement");
        wsdl11Definition.setTargetNamespace("http://www.advertisement.dto/soap");
        wsdl11Definition.setSchema(deleteAdvertisement);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("deleteAdvertisement")
    public XsdSchema deleteAdvertisementSchema() {
        return new SimpleXsdSchema(new ClassPathResource("deleteAdvertisement.xsd"));
    }

    @Bean(name = "editPartialAdvertisement")
    public DefaultWsdl11Definition defaultWsdl11DefinitionEditPartialAdvertisement(@Qualifier("editPartialAdvertisement") XsdSchema editPartialAdvertisement) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AdvertisementsPort");
        wsdl11Definition.setLocationUri("/ws/advertisement");
        wsdl11Definition.setTargetNamespace("http://www.advertisement.dto/soap");
        wsdl11Definition.setSchema(editPartialAdvertisement);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("editPartialAdvertisement")
    public XsdSchema editPartialAdvertisementSchema() {
        return new SimpleXsdSchema(new ClassPathResource("editPartialAdvertisement.xsd"));
    }

    @Bean(name = "getAdvertisementEditType")
    public DefaultWsdl11Definition defaultWsdl11DefinitionGetAdvertisementEditType(@Qualifier("getAdvertisementEditType") XsdSchema getAdvertisementEditType) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AdvertisementsPort");
        wsdl11Definition.setLocationUri("/ws/advertisement");
        wsdl11Definition.setTargetNamespace("http://www.advertisement.dto/soap");
        wsdl11Definition.setSchema(getAdvertisementEditType);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("getAdvertisementEditType")
    public XsdSchema getAdvertisementEditTypeSchema() {
        return new SimpleXsdSchema(new ClassPathResource("getAdvertisementEditType.xsd"));
    }

    @Bean(name = "listAdvertisement")
    public DefaultWsdl11Definition defaultWsdl11DefinitionListAdvertisement(@Qualifier("listAdvertisement") XsdSchema listAdvertisement) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AdvertisementsPort");
        wsdl11Definition.setLocationUri("/ws/advertisement");
        wsdl11Definition.setTargetNamespace("http://www.advertisement.dto/soap");
        wsdl11Definition.setSchema(listAdvertisement);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listAdvertisement")
    public XsdSchema listAdvertisementSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listAdvertisement.xsd"));
    }

    @Bean(name = "checkIfCarIsAvailable")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCheckIfCarIsAvailable(@Qualifier("checkIfCarIsAvailable") XsdSchema checkIfCarIsAvailable) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AdvertisementsPort");
        wsdl11Definition.setLocationUri("/ws/advertisement");
        wsdl11Definition.setTargetNamespace("http://www.advertisement.dto/soap");
        wsdl11Definition.setSchema(checkIfCarIsAvailable);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("checkIfCarIsAvailable")
    public XsdSchema checkIfCarIsAvailableSchema() {
        return new SimpleXsdSchema(new ClassPathResource("checkIfCarIsAvailable.xsd"));
    }
}
