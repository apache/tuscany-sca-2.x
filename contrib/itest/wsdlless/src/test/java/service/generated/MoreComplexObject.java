/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package service.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for moreComplexObject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moreComplexObject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="intParam" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="stringParam" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stringParam2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moreComplexObject", namespace = "", propOrder = {"intParam", "stringParam", "stringParam2"})
public class MoreComplexObject {

    protected Integer intParam;
    protected String stringParam;
    protected String stringParam2;

    /**
     * Gets the value of the intParam property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIntParam() {
        return intParam;
    }

    /**
     * Sets the value of the intParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIntParam(Integer value) {
        this.intParam = value;
    }

    /**
     * Gets the value of the stringParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringParam() {
        return stringParam;
    }

    /**
     * Sets the value of the stringParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringParam(String value) {
        this.stringParam = value;
    }

    /**
     * Gets the value of the stringParam2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringParam2() {
        return stringParam2;
    }

    /**
     * Sets the value of the stringParam2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringParam2(String value) {
        this.stringParam2 = value;
    }

}
