//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 09:44:46 PM CEST 
//


package jvn.RentACar.dto.soap.car;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jvn.RentACar.dto.soap.car package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jvn.RentACar.dto.soap.car
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateOrEditCarDetailsRequest }
     * 
     */
    public CreateOrEditCarDetailsRequest createCreateOrEditCarDetailsRequest() {
        return new CreateOrEditCarDetailsRequest();
    }

    /**
     * Create an instance of {@link CarDetails }
     * 
     */
    public CarDetails createCarDetails() {
        return new CarDetails();
    }

    /**
     * Create an instance of {@link PictureInfo }
     * 
     */
    public PictureInfo createPictureInfo() {
        return new PictureInfo();
    }

    /**
     * Create an instance of {@link CreateOrEditCarDetailsResponse }
     * 
     */
    public CreateOrEditCarDetailsResponse createCreateOrEditCarDetailsResponse() {
        return new CreateOrEditCarDetailsResponse();
    }

    /**
     * Create an instance of {@link DeleteCarDetailsRequest }
     * 
     */
    public DeleteCarDetailsRequest createDeleteCarDetailsRequest() {
        return new DeleteCarDetailsRequest();
    }

    /**
     * Create an instance of {@link DeleteCarDetailsResponse }
     * 
     */
    public DeleteCarDetailsResponse createDeleteCarDetailsResponse() {
        return new DeleteCarDetailsResponse();
    }

    /**
     * Create an instance of {@link EditPartialCarDetailsRequest }
     * 
     */
    public EditPartialCarDetailsRequest createEditPartialCarDetailsRequest() {
        return new EditPartialCarDetailsRequest();
    }

    /**
     * Create an instance of {@link EditPartialCarDetails }
     * 
     */
    public EditPartialCarDetails createEditPartialCarDetails() {
        return new EditPartialCarDetails();
    }

    /**
     * Create an instance of {@link EditPartialCarDetailsResponse }
     * 
     */
    public EditPartialCarDetailsResponse createEditPartialCarDetailsResponse() {
        return new EditPartialCarDetailsResponse();
    }

    /**
     * Create an instance of {@link GetCarEditTypeRequest }
     * 
     */
    public GetCarEditTypeRequest createGetCarEditTypeRequest() {
        return new GetCarEditTypeRequest();
    }

    /**
     * Create an instance of {@link GetCarEditTypeResponse }
     * 
     */
    public GetCarEditTypeResponse createGetCarEditTypeResponse() {
        return new GetCarEditTypeResponse();
    }

    /**
     * Create an instance of {@link GetAllCarDetailsRequest }
     * 
     */
    public GetAllCarDetailsRequest createGetAllCarDetailsRequest() {
        return new GetAllCarDetailsRequest();
    }

    /**
     * Create an instance of {@link GetAllCarDetailsResponse }
     * 
     */
    public GetAllCarDetailsResponse createGetAllCarDetailsResponse() {
        return new GetAllCarDetailsResponse();
    }

    /**
     * Create an instance of {@link CarWithPictures }
     * 
     */
    public CarWithPictures createCarWithPictures() {
        return new CarWithPictures();
    }

}
