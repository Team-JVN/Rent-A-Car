//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 12:01:51 AM CEST 
//


package jvn.Users.dto.soap.agent;

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
 *         &lt;element name="agentDetails" type="{http://www.soap.dto/agent}agentDetails"/>
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
    "agentDetails"
})
@XmlRootElement(name = "getProfileAgentDetailsResponse")
public class GetProfileAgentDetailsResponse {

    @XmlElement(required = true)
    protected AgentDetails agentDetails;

    /**
     * Gets the value of the agentDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AgentDetails }
     *     
     */
    public AgentDetails getAgentDetails() {
        return agentDetails;
    }

    /**
     * Sets the value of the agentDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentDetails }
     *     
     */
    public void setAgentDetails(AgentDetails value) {
        this.agentDetails = value;
    }

}
