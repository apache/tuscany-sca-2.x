
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
