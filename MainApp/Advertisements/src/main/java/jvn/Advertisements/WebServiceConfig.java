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
        return new ServletRegistrationBean(servlet, "/pricelist/ws/*");
    }

    @Bean(name = "priceLists")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema priceListsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PriceListsPort");
        wsdl11Definition.setLocationUri("/pricelist/ws");
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
        wsdl11Definition.setLocationUri("/pricelist/ws");
        wsdl11Definition.setTargetNamespace("http://www.pricelist.dto/soap");
        wsdl11Definition.setSchema(deletePriceLists);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("deletePriceListsSchema")
    public XsdSchema deletePriceListsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("deletePriceLists.xsd"));
    }
}
