//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.15 at 10:43:03 PM CEST 
//


package jvn.Advertisements.dto.soap.pricelist;

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
 *         &lt;element name="priceListDetails" type="{http://www.soap.dto/pricelist}priceListDetails"/>
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
    "priceListDetails"
})
@XmlRootElement(name = "getPriceListDetailsResponse")
public class GetPriceListDetailsResponse {

    @XmlElement(required = true)
    protected PriceListDetails priceListDetails;

    /**
     * Gets the value of the priceListDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PriceListDetails }
     *     
     */
    public PriceListDetails getPriceListDetails() {
        return priceListDetails;
    }

    /**
     * Sets the value of the priceListDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriceListDetails }
     *     
     */
    public void setPriceListDetails(PriceListDetails value) {
        this.priceListDetails = value;
    }

}