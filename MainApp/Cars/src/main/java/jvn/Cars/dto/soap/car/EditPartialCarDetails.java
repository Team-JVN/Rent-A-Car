//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 07:21:52 PM CEST 
//


package jvn.Cars.dto.soap.car;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for editPartialCarDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="editPartialCarDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long">
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="mileageInKm">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="kidsSeats">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="availableTracking" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "editPartialCarDetails", propOrder = {
    "id",
    "mileageInKm",
    "kidsSeats",
    "availableTracking"
})
public class EditPartialCarDetails {

    protected long id;
    protected int mileageInKm;
    protected int kidsSeats;
    protected boolean availableTracking;

    /**
     * Gets the value of the id property.
     * 
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the mileageInKm property.
     * 
     */
    public int getMileageInKm() {
        return mileageInKm;
    }

    /**
     * Sets the value of the mileageInKm property.
     * 
     */
    public void setMileageInKm(int value) {
        this.mileageInKm = value;
    }

    /**
     * Gets the value of the kidsSeats property.
     * 
     */
    public int getKidsSeats() {
        return kidsSeats;
    }

    /**
     * Sets the value of the kidsSeats property.
     * 
     */
    public void setKidsSeats(int value) {
        this.kidsSeats = value;
    }

    /**
     * Gets the value of the availableTracking property.
     * 
     */
    public boolean isAvailableTracking() {
        return availableTracking;
    }

    /**
     * Sets the value of the availableTracking property.
     * 
     */
    public void setAvailableTracking(boolean value) {
        this.availableTracking = value;
    }

}
