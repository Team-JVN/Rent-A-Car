package jvn.Cars;

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
        return new ServletRegistrationBean(servlet, "/car/ws/*");
    }

    @Bean(name = "createOrEditCar")
    public DefaultWsdl11Definition defaultWsdl11Definition(@Qualifier("createOrEditCarSchema") XsdSchema createOrEditCarSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CarsPort");
        wsdl11Definition.setLocationUri("/car/ws");
        wsdl11Definition.setTargetNamespace("http://www.car.dto/soap");
        wsdl11Definition.setSchema(createOrEditCarSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("createOrEditCarSchema")
    public XsdSchema createOrEditCarSchema() {
        return new SimpleXsdSchema(new ClassPathResource("createOrEditCar.xsd"));
    }

    @Bean(name = "deleteCar")
    public DefaultWsdl11Definition defaultWsdl11DefinitionDeleteCar(@Qualifier("deleteCarSchema") XsdSchema deleteCarSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CarsPort");
        wsdl11Definition.setLocationUri("/car/ws");
        wsdl11Definition.setTargetNamespace("http://www.car.dto/soap");
        wsdl11Definition.setSchema(deleteCarSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("deleteCarSchema")
    public XsdSchema deleteCarSchema() {
        return new SimpleXsdSchema(new ClassPathResource("deleteCar.xsd"));
    }

    @Bean(name = "getCarEditType")
    public DefaultWsdl11Definition defaultWsdl11DefinitionGetCarEditTypeSchema(@Qualifier("getCarEditTypeSchema") XsdSchema getCarEditTypeSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CarsPort");
        wsdl11Definition.setLocationUri("/car/ws");
        wsdl11Definition.setTargetNamespace("http://www.car.dto/soap");
        wsdl11Definition.setSchema(getCarEditTypeSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("getCarEditTypeSchema")
    public XsdSchema getCarEditTypeSchema() {
        return new SimpleXsdSchema(new ClassPathResource("getCarEditType.xsd"));
    }

    @Bean(name = "editPartialCar")
    public DefaultWsdl11Definition defaultWsdl11DefinitionEditPartialCar(@Qualifier("editPartialCarSchema") XsdSchema editPartialCarSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CarsPort");
        wsdl11Definition.setLocationUri("/car/ws");
        wsdl11Definition.setTargetNamespace("http://www.car.dto/soap");
        wsdl11Definition.setSchema(editPartialCarSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("editPartialCarSchema")
    public XsdSchema editPartialCarSchema() {
        return new SimpleXsdSchema(new ClassPathResource("editPartialCar.xsd"));
    }

    @Bean(name = "listCar")
    public DefaultWsdl11Definition defaultWsdl11DefinitionListCarSchema(@Qualifier("listCarSchema") XsdSchema listCarSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CarsPort");
        wsdl11Definition.setLocationUri("/car/ws");
        wsdl11Definition.setTargetNamespace("http://www.car.dto/soap");
        wsdl11Definition.setSchema(listCarSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listCarSchema")
    public XsdSchema listCarSchemaSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listCar.xsd"));
    }
}
