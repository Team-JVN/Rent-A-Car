//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 

// Generated on: 2020.06.19 at 08:07:47 AM CEST 


package jvn.RentACar.dto.soap.client;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the jvn.RentACar.dto.soap.client package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jvn.RentACar.dto.soap.client
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CheckClientPersonalInfoRequest }
     */
    public CheckClientPersonalInfoRequest createCheckClientPersonalInfoRequest() {
        return new CheckClientPersonalInfoRequest();
    }

    /**
     * Create an instance of {@link CheckClientPersonalInfoResponse }
     */
    public CheckClientPersonalInfoResponse createCheckClientPersonalInfoResponse() {
        return new CheckClientPersonalInfoResponse();
    }

    /**
     * Create an instance of {@link CreateOrEditClientRequest }
     */
    public CreateOrEditClientRequest createCreateOrEditClientRequest() {
        return new CreateOrEditClientRequest();
    }

    /**
     * Create an instance of {@link ClientDetails }
     */
    public ClientDetails createClientDetails() {
        return new ClientDetails();
    }

    /**
     * Create an instance of {@link CreateOrEditClientResponse }
     */
    public CreateOrEditClientResponse createCreateOrEditClientResponse() {
        return new CreateOrEditClientResponse();
    }

    /**
     * Create an instance of {@link DeleteClientDetailsRequest }
     */
    public DeleteClientDetailsRequest createDeleteClientDetailsRequest() {
        return new DeleteClientDetailsRequest();
    }

    /**
     * Create an instance of {@link DeleteClientDetailsResponse }
     */
    public DeleteClientDetailsResponse createDeleteClientDetailsResponse() {
        return new DeleteClientDetailsResponse();
    }

    /**
     * Create an instance of {@link GetAllClientDetailsRequest }
     */
    public GetAllClientDetailsRequest createGetAllClientDetailsRequest() {
        return new GetAllClientDetailsRequest();
    }

    /**
     * Create an instance of {@link GetAllClientDetailsResponse }
     */
    public GetAllClientDetailsResponse createGetAllClientDetailsResponse() {
        return new GetAllClientDetailsResponse();
    }

}
