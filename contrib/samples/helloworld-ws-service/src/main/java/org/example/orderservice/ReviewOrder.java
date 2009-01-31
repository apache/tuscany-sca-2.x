
package org.example.orderservice;

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
 *         &lt;element name="myData" type="{http://www.example.org/OrderService/}order"/>
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
    "myData"
})
@XmlRootElement(name = "reviewOrder")
public class ReviewOrder {

    @XmlElement(required = true)
    protected Order myData;

    /**
     * Gets the value of the myData property.
     * 
     * @return
     *     possible object is
     *     {@link Order }
     *     
     */
    public Order getMyData() {
        return myData;
    }

    /**
     * Sets the value of the myData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Order }
     *     
     */
    public void setMyData(Order value) {
        this.myData = value;
    }

}
