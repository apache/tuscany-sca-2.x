
package com.example.uszip;

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
 *         &lt;element name="USState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "usState"
})
@XmlRootElement(name = "GetInfoByState")
public class GetInfoByState {

    @XmlElement(name = "USState")
    protected String usState;

    /**
     * Gets the value of the usState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSState() {
        return usState;
    }

    /**
     * Sets the value of the usState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSState(String value) {
        this.usState = value;
    }

}
