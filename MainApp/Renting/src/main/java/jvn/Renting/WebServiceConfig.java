package jvn.Renting;

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
        return new ServletRegistrationBean(servlet, "/rentrequest/ws/*");
    }

    @Bean(name = "rentRequests")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema rentRequestsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/rentrequest/ws");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(rentRequestsSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema rentRequestsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("rentRequests.xsd"));
    }

    @Bean(name = "changeRentRequestStatus")
    public DefaultWsdl11Definition defaultWsdl11DefinitionChangeRentRequestStatus(@Qualifier("changeRentRequestStatus") XsdSchema changeRentRequestStatus) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/rentrequest/ws");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(changeRentRequestStatus);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("changeRentRequestStatus")
    public XsdSchema changeRentRequestStatusSchema() {
        return new SimpleXsdSchema(new ClassPathResource("changeRentRequestStatus.xsd"));
    }


    @Bean(name = "checkDate")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCheckDate(@Qualifier("checkDate") XsdSchema checkDate) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/rentrequest/ws");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(checkDate);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("checkDate")
    public XsdSchema checkDateSchema() {
        return new SimpleXsdSchema(new ClassPathResource("checkDate.xsd"));
    }

    @Bean(name = "checkIfCanAcceptRentRequest")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCheckIfCanAcceptRentRequest(@Qualifier("checkIfCanAcceptRentRequest") XsdSchema checkIfCanAcceptRentRequest) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/rentrequest/ws");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(checkIfCanAcceptRentRequest);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("checkIfCanAcceptRentRequest")
    public XsdSchema checkIfCanAcceptRentRequestSchema() {
        return new SimpleXsdSchema(new ClassPathResource("checkIfCanAcceptRentRequest.xsd"));
    }


    @Bean(name = "hasDebt")
    public DefaultWsdl11Definition defaultWsdl11DefinitionHasDebt(@Qualifier("hasDebt") XsdSchema hasDebt) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/rentrequest/ws");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(hasDebt);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("hasDebt")
    public XsdSchema hasDebtSchema() {
        return new SimpleXsdSchema(new ClassPathResource("hasDebt.xsd"));
    }


    @Bean(name = "listRentRequest")
    public DefaultWsdl11Definition defaultWsdl11DefinitionListRentRequest(@Qualifier("listRentRequest") XsdSchema listRentRequest) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/rentrequest/ws");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(listRentRequest);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listRentRequest")
    public XsdSchema listRentRequestSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listRentRequest.xsd"));
    }
}
