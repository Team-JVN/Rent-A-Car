//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.17 at 02:14:55 PM CEST 
//


package jvn.RentACar.dto.soap.pricelist;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="priceListDetails" type="{http://www.soap.dto/pricelist}priceListDetails"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
