package jvn.Zuul;


import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;

@EnableWs
@Configuration
public class WebServiceConfig {
//
//    @Bean
//    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
//        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
//        servlet.setApplicationContext(applicationContext);
//        servlet.setTransformWsdlLocations(true);
//        return new ServletRegistrationBean(servlet, "/ws/*");
//    }

    //    @Bean(name = "priceListDetails")
//    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema priceListSchema) {
//        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//        wsdl11Definition.setPortTypeName("PriceListDetailsPort");
//        wsdl11Definition.setLocationUri("http://localhost:8080/advertisements/priceList/get");
//        wsdl11Definition.setTargetNamespace("/advertisements/ws");
//        wsdl11Definition.setSchema(priceListSchema);
//        return wsdl11Definition;
//    }
//
//    @Bean
//    public XsdSchema priceListSchema() {
//        return new SimpleXsdSchema(new ClassPathResource("xsd/priceListSchema.xsd"));
//    }
//    @Bean(name = "countries")
//    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
//        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//        wsdl11Definition.setPortTypeName("CountriesPort");
//        wsdl11Definition.setLocationUri("/ws");
//        wsdl11Definition.setTargetNamespace("http://www.baeldung.com/springsoap/gen");
//        wsdl11Definition.setSchema(countriesSchema);
//        return wsdl11Definition;
//    }
//
//    @Bean
//    public XsdSchema countriesSchema() {
//        return new SimpleXsdSchema(new ClassPathResource("/xsd/proba.xsd"));
//    }
}

