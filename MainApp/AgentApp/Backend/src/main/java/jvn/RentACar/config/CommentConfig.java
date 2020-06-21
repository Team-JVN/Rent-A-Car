package jvn.RentACar.config;

import jvn.RentACar.client.CommentClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CommentConfig {
    @Bean
    @Qualifier("marshallerComment")
    public Jaxb2Marshaller marshallerComment() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.comment");
        return marshaller;
    }

    @Bean
    public CommentClient commentClient(Jaxb2Marshaller marshallerComment) {
        CommentClient client = new CommentClient();
        client.setDefaultUri("http://renting:8083/renting/ws/comment");
        client.setMarshaller(marshallerComment);
        client.setUnmarshaller(marshallerComment);
        return client;
    }
}
