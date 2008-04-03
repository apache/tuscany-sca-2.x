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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.example.weather package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WeatherForecasts_QNAME = new QName("http://www.webservicex.net", "WeatherForecasts");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.example.weather
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WeatherData }
     * 
     */
    public WeatherData createWeatherData() {
        return new WeatherData();
    }

    /**
     * Create an instance of {@link GetWeatherByPlaceName }
     * 
     */
    public GetWeatherByPlaceName createGetWeatherByPlaceName() {
        return new GetWeatherByPlaceName();
    }

    /**
     * Create an instance of {@link GetWeatherByPlaceNameResponse }
     * 
     */
    public GetWeatherByPlaceNameResponse createGetWeatherByPlaceNameResponse() {
        return new GetWeatherByPlaceNameResponse();
    }

    /**
     * Create an instance of {@link GetWeatherByZipCodeResponse }
     * 
     */
    public GetWeatherByZipCodeResponse createGetWeatherByZipCodeResponse() {
        return new GetWeatherByZipCodeResponse();
    }

    /**
     * Create an instance of {@link GetWeatherByZipCode }
     * 
     */
    public GetWeatherByZipCode createGetWeatherByZipCode() {
        return new GetWeatherByZipCode();
    }

    /**
     * Create an instance of {@link WeatherForecasts }
     * 
     */
    public WeatherForecasts createWeatherForecasts() {
        return new WeatherForecasts();
    }

    /**
     * Create an instance of {@link ArrayOfWeatherData }
     * 
     */
    public ArrayOfWeatherData createArrayOfWeatherData() {
        return new ArrayOfWeatherData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherForecasts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.webservicex.net", name = "WeatherForecasts")
    public JAXBElement<WeatherForecasts> createWeatherForecasts(WeatherForecasts value) {
        return new JAXBElement<WeatherForecasts>(_WeatherForecasts_QNAME, WeatherForecasts.class, null, value);
    }

}
