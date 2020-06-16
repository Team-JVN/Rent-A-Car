//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 11:06:47 AM CEST 
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
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="createCarDetails" type="{http://www.soap.dto/car}carDetails"/>
 *         &lt;element name="multiPartFile" type="{http://www.w3.org/2001/XMLSchema}base64Binary" maxOccurs="unbounded"/>
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
    "email",
    "createCarDetails",
    "multiPartFile"
})
@XmlRootElement(name = "createOrEditCarDetailsRequest")
public class CreateOrEditCarDetailsRequest {

    @XmlElement(required = true)
    protected String email;
    @XmlElement(required = true)
    protected CarDetails createCarDetails;
    @XmlElement(required = true)
    protected List<byte[]> multiPartFile;

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the createCarDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CarDetails }
     *     
     */
    public CarDetails getCreateCarDetails() {
        return createCarDetails;
    }

    /**
     * Sets the value of the createCarDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CarDetails }
     *     
     */
    public void setCreateCarDetails(CarDetails value) {
        this.createCarDetails = value;
    }

    /**
     * Gets the value of the multiPartFile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the multiPartFile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMultiPartFile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * byte[]
     * 
     */
    public List<byte[]> getMultiPartFile() {
        if (multiPartFile == null) {
            multiPartFile = new ArrayList<byte[]>();
        }
        return this.multiPartFile;
    }

}
