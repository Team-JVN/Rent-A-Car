//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 12:37:06 AM CEST 
//


package jvn.RentACar.dto.soap.agent;

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
 *         &lt;element name="agentDetails" type="{http://www.soap.dto/agent}agentDetails"/&gt;
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
