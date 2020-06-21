// Generated on: 2020.06.19 at 08:07:47 AM CEST


package jvn.RentACar.dto.soap.client;

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
 *         &lt;element name="clientEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "clientEmail",
        "phoneNumber"
})
@XmlRootElement(name = "checkClientPersonalInfoRequest")
public class CheckClientPersonalInfoRequest {

    protected String clientEmail;
    @XmlElement(required = true)
    protected String phoneNumber;

    /**
     * Gets the value of the clientEmail property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getClientEmail() {
        return clientEmail;
    }

    /**
     * Sets the value of the clientEmail property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setClientEmail(String value) {
        this.clientEmail = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

}
