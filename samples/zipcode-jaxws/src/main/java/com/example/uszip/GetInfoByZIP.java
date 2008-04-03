
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
 *         &lt;element name="USZip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "usZip"
})
@XmlRootElement(name = "GetInfoByZIP")
public class GetInfoByZIP {

    @XmlElement(name = "USZip")
    protected String usZip;

    /**
     * Gets the value of the usZip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSZip() {
        return usZip;
    }

    /**
     * Sets the value of the usZip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSZip(String value) {
        this.usZip = value;
    }

}
