//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 07:22:52 PM CEST 
//


package jvn.Cars.dto.soap.car;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="carWithPictures" type="{http://www.soap.dto/car}carWithPictures" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "carWithPictures"
})
@XmlRootElement(name = "getAllCarDetailsResponse")
public class GetAllCarDetailsResponse {

    @XmlElement(required = true)
    protected List<CarWithPictures> carWithPictures;

    /**
     * Gets the value of the carWithPictures property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the carWithPictures property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCarWithPictures().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CarWithPictures }
     * 
     * 
     */
    public List<CarWithPictures> getCarWithPictures() {
        if (carWithPictures == null) {
            carWithPictures = new ArrayList<CarWithPictures>();
        }
        return this.carWithPictures;
    }

}
