package jvn.Advertisements;

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
//        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/priceList/*");
    }

    @Bean(name = "priceListDetails")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema priceListSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PriceListDetailsPort");
        wsdl11Definition.setLocationUri("http://localhost:8080/advertisements/priceList/get");
        wsdl11Definition.setTargetNamespace("/advertisements/ws");
        wsdl11Definition.setSchema(priceListSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema priceListSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/priceListSchema.xsd"));
    }
}
