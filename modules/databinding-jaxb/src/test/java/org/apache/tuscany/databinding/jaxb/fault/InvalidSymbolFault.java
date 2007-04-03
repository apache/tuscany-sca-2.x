package org.apache.tuscany.databinding.jaxb.fault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"message", "symbol"})
@XmlRootElement(name = "InvalidSymbolFault")
public class InvalidSymbolFault {

    @XmlElement(required = true)
    protected String message;
    @XmlElement(required = true)
    protected String symbol;

    /**
     * Gets the value of the message property.
     * 
     * @return possible object is {@link String }
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the symbol property.
     * 
     * @return possible object is {@link String }
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setSymbol(String value) {
        this.symbol = value;
    }

}
