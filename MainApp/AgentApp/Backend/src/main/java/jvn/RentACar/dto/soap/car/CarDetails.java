//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 09:44:46 PM CEST 
//


package jvn.RentACar.dto.soap.car;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for carDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="carDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="make" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fuelType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="gearBoxType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="bodyStyle" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="mileageInKm"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="kidsSeats"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="availableTracking" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="status" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="EXISTING"/&gt;
 *               &lt;enumeration value="DELETED"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="avgRating"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="commentsCount"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "carDetails", propOrder = {
    "id",
    "make",
    "model",
    "fuelType",
    "gearBoxType",
    "bodyStyle",
    "mileageInKm",
    "kidsSeats",
    "availableTracking",
    "status",
    "avgRating",
    "commentsCount"
})
public class CarDetails {

    protected Long id;
    @XmlElement(required = true)
    protected String make;
    @XmlElement(required = true)
    protected String model;
    @XmlElement(required = true)
    protected String fuelType;
    @XmlElement(required = true)
    protected String gearBoxType;
    @XmlElement(required = true)
    protected String bodyStyle;
    protected int mileageInKm;
    protected int kidsSeats;
    protected boolean availableTracking;
    protected String status;
    protected double avgRating;
    protected int commentsCount;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the make property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the value of the make property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMake(String value) {
        this.make = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the fuelType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFuelType() {
        return fuelType;
    }

    /**
     * Sets the value of the fuelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFuelType(String value) {
        this.fuelType = value;
    }

    /**
     * Gets the value of the gearBoxType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGearBoxType() {
        return gearBoxType;
    }

    /**
     * Sets the value of the gearBoxType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGearBoxType(String value) {
        this.gearBoxType = value;
    }

    /**
     * Gets the value of the bodyStyle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodyStyle() {
        return bodyStyle;
    }

    /**
     * Sets the value of the bodyStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodyStyle(String value) {
        this.bodyStyle = value;
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

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the avgRating property.
     * 
     */
    public double getAvgRating() {
        return avgRating;
    }

    /**
     * Sets the value of the avgRating property.
     * 
     */
    public void setAvgRating(double value) {
        this.avgRating = value;
    }

    /**
     * Gets the value of the commentsCount property.
     * 
     */
    public int getCommentsCount() {
        return commentsCount;
    }

    /**
     * Sets the value of the commentsCount property.
     * 
     */
    public void setCommentsCount(int value) {
        this.commentsCount = value;
    }

}
