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
package com.example.weather;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeatherData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WeatherData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Day" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WeatherImage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MaxTemperatureF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MinTemperatureF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MaxTemperatureC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MinTemperatureC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherData", propOrder = {
    "day",
    "weatherImage",
    "maxTemperatureF",
    "minTemperatureF",
    "maxTemperatureC",
    "minTemperatureC"
})
public class WeatherData {

    @XmlElement(name = "Day")
    protected String day;
    @XmlElement(name = "WeatherImage")
    protected String weatherImage;
    @XmlElement(name = "MaxTemperatureF")
    protected String maxTemperatureF;
    @XmlElement(name = "MinTemperatureF")
    protected String minTemperatureF;
    @XmlElement(name = "MaxTemperatureC")
    protected String maxTemperatureC;
    @XmlElement(name = "MinTemperatureC")
    protected String minTemperatureC;

    /**
     * Gets the value of the day property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDay(String value) {
        this.day = value;
    }

    /**
     * Gets the value of the weatherImage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeatherImage() {
        return weatherImage;
    }

    /**
     * Sets the value of the weatherImage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeatherImage(String value) {
        this.weatherImage = value;
    }

    /**
     * Gets the value of the maxTemperatureF property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxTemperatureF() {
        return maxTemperatureF;
    }

    /**
     * Sets the value of the maxTemperatureF property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxTemperatureF(String value) {
        this.maxTemperatureF = value;
    }

    /**
     * Gets the value of the minTemperatureF property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinTemperatureF() {
        return minTemperatureF;
    }

    /**
     * Sets the value of the minTemperatureF property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinTemperatureF(String value) {
        this.minTemperatureF = value;
    }

    /**
     * Gets the value of the maxTemperatureC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxTemperatureC() {
        return maxTemperatureC;
    }

    /**
     * Sets the value of the maxTemperatureC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxTemperatureC(String value) {
        this.maxTemperatureC = value;
    }

    /**
     * Gets the value of the minTemperatureC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinTemperatureC() {
        return minTemperatureC;
    }

    /**
     * Sets the value of the minTemperatureC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinTemperatureC(String value) {
        this.minTemperatureC = value;
    }

}
