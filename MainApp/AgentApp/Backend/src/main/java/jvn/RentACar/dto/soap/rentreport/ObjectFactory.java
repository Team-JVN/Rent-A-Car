//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.28 at 05:39:05 PM CEST 
//


package jvn.RentACar.dto.soap.rentreport;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jvn.RentACar.dto.soap.rentreport package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jvn.RentACar.dto.soap.rentreport
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAllRentReportsDetailsResponse }
     * 
     */
    public GetAllRentReportsDetailsResponse createGetAllRentReportsDetailsResponse() {
        return new GetAllRentReportsDetailsResponse();
    }

    /**
     * Create an instance of {@link RentReportDetails }
     * 
     */
    public RentReportDetails createRentReportDetails() {
        return new RentReportDetails();
    }

    /**
     * Create an instance of {@link GetAllRentReportsDetailsRequest }
     * 
     */
    public GetAllRentReportsDetailsRequest createGetAllRentReportsDetailsRequest() {
        return new GetAllRentReportsDetailsRequest();
    }

    /**
     * Create an instance of {@link CreateRentReportRequest }
     *
     */
    public CreateRentReportRequest createCreateRentReportRequest() {
        return new CreateRentReportRequest();
    }


    /**
     * Create an instance of {@link CreateRentReportResponse }
     *
     */
    public CreateRentReportResponse createCreateRentReportResponse() {
        return new CreateRentReportResponse();
    }

    /**
     * Create an instance of {@link CheckIfCanCreateRentReportRequest }
     *
     */
    public CheckIfCanCreateRentReportRequest createCheckIfCanCreateRentReportRequest() {
        return new CheckIfCanCreateRentReportRequest();
    }

    /**
     * Create an instance of {@link CheckIfCanCreateRentReportResponse }
     *
     */
    public CheckIfCanCreateRentReportResponse createCheckIfCanCreateRentReportResponse() {
        return new CheckIfCanCreateRentReportResponse();
    }
}
