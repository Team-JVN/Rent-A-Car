//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.14 at 04:23:38 PM CEST 
//

package jvn.Advertisements.dto.soap.localhost._8080.advertisements;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="priceListDetails" type="{/advertisements/ws}priceListDetails"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "priceListDetails"
})
@XmlRootElement(name = "getPriceListDetailsRequest")
public class GetPriceListDetailsRequest {

    @XmlElement(required = true)
    protected PriceListDetails priceListDetails;

    /**
     * Gets the value of the priceListDetails property.
     *
     * @return possible object is
     * {@link PriceListDetails }
     */
    public PriceListDetails getPriceListDetails() {
        return priceListDetails;
    }

    /**
     * Sets the value of the priceListDetails property.
     *
     * @param value allowed object is
     *              {@link PriceListDetails }
     */
    public void setPriceListDetails(PriceListDetails value) {
        this.priceListDetails = value;
    }

}
