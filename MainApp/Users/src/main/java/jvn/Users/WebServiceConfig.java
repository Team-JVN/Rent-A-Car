package jvn.Users;

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

    @Bean(name = "agents")
    public DefaultWsdl11Definition defaultWsdl11Definition(@Qualifier("agentsSchema") XsdSchema agentsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AgentsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.agent.dto/soap");
        wsdl11Definition.setSchema(agentsSchema);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("agentsSchema")
    public XsdSchema agentsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("agents.xsd"));
    }

    @Bean(name = "getProfileAgent")
    public DefaultWsdl11Definition defaultWsdl11DefinitionGetProfileAgent(@Qualifier("getProfileAgent") XsdSchema getProfileAgent) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AgentsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.agent.dto/soap");
        wsdl11Definition.setSchema(getProfileAgent);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("getProfileAgent")
    public XsdSchema getProfileAgentSchema() {
        return new SimpleXsdSchema(new ClassPathResource("getProfileAgent.xsd"));
    }
}
