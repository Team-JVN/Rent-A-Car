package jvn.RentACar.config;

import jvn.RentACar.client.AgentClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class AgentConfig {
    @Bean
    @Qualifier("marshallerAgent")
    public Jaxb2Marshaller marshallerAgent() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.agent");
        return marshaller;
    }
    
    @Bean
    public AgentClient agentClient(Jaxb2Marshaller marshallerAgent) {
        AgentClient client = new AgentClient();
        client.setDefaultUri("http://localhost:8080/users/agent/ws");
        client.setMarshaller(marshallerAgent);
        client.setUnmarshaller(marshallerAgent);
        return client;
    }
}
