package jvn.Cars;

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
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema createOrEditCar) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CarsPort");
        wsdl11Definition.setLocationUri("/car/ws");
        wsdl11Definition.setTargetNamespace("http://www.car.dto/soap");
        wsdl11Definition.setSchema(createOrEditCar);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema createOrEditCarSchema() {
        return new SimpleXsdSchema(new ClassPathResource("createOrEditCar.xsd"));
    }

}
