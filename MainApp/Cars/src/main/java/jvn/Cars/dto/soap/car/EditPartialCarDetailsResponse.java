//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 07:21:52 PM CEST 
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
 *         &lt;element name="editPartialCarDetails" type="{http://www.soap.dto/car}editPartialCarDetails"/>
 *         &lt;element name="pictureInfo" type="{http://www.soap.dto/car}pictureInfo" maxOccurs="unbounded" minOccurs="0"/>
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
    "editPartialCarDetails",
    "pictureInfo"
})
@XmlRootElement(name = "editPartialCarDetailsResponse")
public class EditPartialCarDetailsResponse {

    @XmlElement(required = true)
    protected EditPartialCarDetails editPartialCarDetails;
    protected List<PictureInfo> pictureInfo;

    /**
     * Gets the value of the editPartialCarDetails property.
     * 
     * @return
     *     possible object is
     *     {@link EditPartialCarDetails }
     *     
     */
    public EditPartialCarDetails getEditPartialCarDetails() {
        return editPartialCarDetails;
    }

    /**
     * Sets the value of the editPartialCarDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditPartialCarDetails }
     *     
     */
    public void setEditPartialCarDetails(EditPartialCarDetails value) {
        this.editPartialCarDetails = value;
    }

    /**
     * Gets the value of the pictureInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pictureInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPictureInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PictureInfo }
     * 
     * 
     */
    public List<PictureInfo> getPictureInfo() {
        if (pictureInfo == null) {
            pictureInfo = new ArrayList<PictureInfo>();
        }
        return this.pictureInfo;
    }

}
