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
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "rentRequests")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema rentRequestsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentRequestsPort");
        wsdl11Definition.setLocationUri("/ws/rentrequest");
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
        wsdl11Definition.setLocationUri("/ws/rentrequest");
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
        wsdl11Definition.setLocationUri("/ws/rentrequest");
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
        wsdl11Definition.setLocationUri("/ws/rentrequest");
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
        wsdl11Definition.setLocationUri("/ws/rentrequest");
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
        wsdl11Definition.setLocationUri("/ws/rentrequest");
        wsdl11Definition.setTargetNamespace("http://www.rentrequest.dto/soap");
        wsdl11Definition.setSchema(listRentRequest);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listRentRequest")
    public XsdSchema listRentRequestSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listRentRequest.xsd"));
    }

    @Bean(name = "listMessages")
    public DefaultWsdl11Definition defaultWsdl11DefinitionListMessages(@Qualifier("listMessages") XsdSchema listMessages) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("MessagesPort");
        wsdl11Definition.setLocationUri("/ws/message");
        wsdl11Definition.setTargetNamespace("http://www.message.dto/soap");
        wsdl11Definition.setSchema(listMessages);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listMessages")
    public XsdSchema listMessagesSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listMessages.xsd"));
    }

    @Bean(name = "rentReport")
    public DefaultWsdl11Definition defaultWsdl11DefinitionRentReport(XsdSchema rentReportSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentReportsPort");
        wsdl11Definition.setLocationUri("/ws/rentreport");
        wsdl11Definition.setTargetNamespace("http://www.rentreport.dto/soap");
        wsdl11Definition.setSchema(rentReportSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema rentReportSchema() {
        return new SimpleXsdSchema(new ClassPathResource("createRentReport.xsd"));
    }

    @Bean(name = "rentReports")
    public DefaultWsdl11Definition defaultWsdl11DefinitionRentReports(@Qualifier("rentReports") XsdSchema rentReports) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentReportsPort");
        wsdl11Definition.setLocationUri("/ws/rentreport");
        wsdl11Definition.setTargetNamespace("http://www.rentreport.dto/soap");
        wsdl11Definition.setSchema(rentReports);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("rentReports")
    public XsdSchema rentReportsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("rentReports.xsd"));
    }

    @Bean(name = "checkIfCanCreateRentReport")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCheckIfCanCreateRentReport(@Qualifier("checkIfCanCreateRentReport") XsdSchema checkIfCanCreateRentReport) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RentReportsPort");
        wsdl11Definition.setLocationUri("/ws/rentreport");
        wsdl11Definition.setTargetNamespace("http://www.rentreport.dto/soap");
        wsdl11Definition.setSchema(checkIfCanCreateRentReport);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("checkIfCanCreateRentReport")
    public XsdSchema checkIfCanCreateRentReportSchema() {
        return new SimpleXsdSchema(new ClassPathResource("checkIfCanCreateRentReport.xsd"));
    }

    @Bean(name = "checkIfCanComment")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCheckIfCanComment(@Qualifier("checkIfCanComment") XsdSchema checkIfCanComment) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CommentsPort");
        wsdl11Definition.setLocationUri("/ws/comment");
        wsdl11Definition.setTargetNamespace("http://www.comment.dto/soap");
        wsdl11Definition.setSchema(checkIfCanComment);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("checkIfCanComment")
    public XsdSchema checkIfCanCommentSchema() {
        return new SimpleXsdSchema(new ClassPathResource("checkIfCanComment.xsd"));
    }

    @Bean(name = "leaveFeedback")
    public DefaultWsdl11Definition defaultWsdl11DefinitionLeaveFeedback(XsdSchema leaveFeedbackSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CommentsPort");
        wsdl11Definition.setLocationUri("/ws/comment");
        wsdl11Definition.setTargetNamespace("http://www.comment.dto/soap");
        wsdl11Definition.setSchema(leaveFeedbackSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema leaveFeedbackSchema() {
        return new SimpleXsdSchema(new ClassPathResource("leaveFeedback.xsd"));
    }

    @Bean(name = "createComment")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCreateComment(XsdSchema createCommentSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CommentsPort");
        wsdl11Definition.setLocationUri("/ws/comment");
        wsdl11Definition.setTargetNamespace("http://www.comment.dto/soap");
        wsdl11Definition.setSchema(createCommentSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema createCommentSchema() {
        return new SimpleXsdSchema(new ClassPathResource("createComment.xsd"));
    }

    @Bean(name = "createMessage")
    public DefaultWsdl11Definition defaultWsdl11DefinitionCreateMessage(XsdSchema createMessageSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("MessagesPort");
        wsdl11Definition.setLocationUri("/ws/message");
        wsdl11Definition.setTargetNamespace("http://www.message.dto/soap");
        wsdl11Definition.setSchema(createMessageSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema createMessageSchema() {
        return new SimpleXsdSchema(new ClassPathResource("createMessage.xsd"));
    }

    @Bean(name = "listComments")
    public DefaultWsdl11Definition defaultWsdl11DefinitionListComments(@Qualifier("listComments") XsdSchema listComments) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CommentsPort");
        wsdl11Definition.setLocationUri("/ws/comment");
        wsdl11Definition.setTargetNamespace("http://www.comment.dto/soap");
        wsdl11Definition.setSchema(listComments);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("listComments")
    public XsdSchema listCommentsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("listComments.xsd"));
    }

    @Bean(name = "getFeedback")
    public DefaultWsdl11Definition defaultWsdl11DefinitionGetFeedback(@Qualifier("getFeedback") XsdSchema getFeedback) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CommentsPort");
        wsdl11Definition.setLocationUri("/ws/comment");
        wsdl11Definition.setTargetNamespace("http://www.comment.dto/soap");
        wsdl11Definition.setSchema(getFeedback);
        return wsdl11Definition;
    }

    @Bean
    @Qualifier("getFeedback")
    public XsdSchema getFeedbackSchema() {
        return new SimpleXsdSchema(new ClassPathResource("getFeedback.xsd"));
    }
}
