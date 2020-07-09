//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.09 at 09:14:40 PM CEST 
//


package jvn.RentACar.dto.soap.rentrequest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for rentInfoDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rentInfoDetails"&gt;
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
 *         &lt;element name="dateTimeFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="dateTimeTo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="advertisement"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="car"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="optedForCDW" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="additionalCost" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="0.0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="paid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="rating" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="commentDetails" type="{http://www.soap.dto/rentrequest}commentDetails" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="kilometersLimit" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="pricePerKm" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="0.0"/&gt;
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
@XmlType(name = "rentInfoDetails", propOrder = {
    "id",
    "dateTimeFrom",
    "dateTimeTo",
    "advertisement",
    "car",
    "optedForCDW",
    "additionalCost",
    "paid",
    "rating",
    "commentDetails",
    "kilometersLimit",
    "pricePerKm"
})
public class RentInfoDetails {

    protected Long id;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTimeFrom;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTimeTo;
    protected long advertisement;
    protected long car;
    protected Boolean optedForCDW;
    protected Double additionalCost;
    protected Boolean paid;
    protected BigInteger rating;
    protected List<CommentDetails> commentDetails;
    protected BigInteger kilometersLimit;
    protected Double pricePerKm;

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
     * Gets the value of the dateTimeFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTimeFrom() {
        return dateTimeFrom;
    }

    /**
     * Sets the value of the dateTimeFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTimeFrom(XMLGregorianCalendar value) {
        this.dateTimeFrom = value;
    }

    /**
     * Gets the value of the dateTimeTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTimeTo() {
        return dateTimeTo;
    }

    /**
     * Sets the value of the dateTimeTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTimeTo(XMLGregorianCalendar value) {
        this.dateTimeTo = value;
    }

    /**
     * Gets the value of the advertisement property.
     * 
     */
    public long getAdvertisement() {
        return advertisement;
    }

    /**
     * Sets the value of the advertisement property.
     * 
     */
    public void setAdvertisement(long value) {
        this.advertisement = value;
    }

    /**
     * Gets the value of the car property.
     * 
     */
    public long getCar() {
        return car;
    }

    /**
     * Sets the value of the car property.
     * 
     */
    public void setCar(long value) {
        this.car = value;
    }

    /**
     * Gets the value of the optedForCDW property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOptedForCDW() {
        return optedForCDW;
    }

    /**
     * Sets the value of the optedForCDW property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptedForCDW(Boolean value) {
        this.optedForCDW = value;
    }

    /**
     * Gets the value of the additionalCost property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAdditionalCost() {
        return additionalCost;
    }

    /**
     * Sets the value of the additionalCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAdditionalCost(Double value) {
        this.additionalCost = value;
    }

    /**
     * Gets the value of the paid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaid() {
        return paid;
    }

    /**
     * Sets the value of the paid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaid(Boolean value) {
        this.paid = value;
    }

    /**
     * Gets the value of the rating property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRating(BigInteger value) {
        this.rating = value;
    }

    /**
     * Gets the value of the commentDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commentDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommentDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommentDetails }
     * 
     * 
     */
    public List<CommentDetails> getCommentDetails() {
        if (commentDetails == null) {
            commentDetails = new ArrayList<CommentDetails>();
        }
        return this.commentDetails;
    }

    /**
     * Gets the value of the kilometersLimit property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getKilometersLimit() {
        return kilometersLimit;
    }

    /**
     * Sets the value of the kilometersLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setKilometersLimit(BigInteger value) {
        this.kilometersLimit = value;
    }

    /**
     * Gets the value of the pricePerKm property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPricePerKm() {
        return pricePerKm;
    }

    /**
     * Sets the value of the pricePerKm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPricePerKm(Double value) {
        this.pricePerKm = value;
    }

}
