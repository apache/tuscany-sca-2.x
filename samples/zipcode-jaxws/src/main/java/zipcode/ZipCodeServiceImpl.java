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

package zipcode;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.example.uszip.GetInfoByZIPResponse;
import com.example.uszip.USZipSoap;
import com.example.uszip.GetInfoByZIPResponse.GetInfoByZIPResult;
import com.example.weather.GetWeatherByZipCodeResponse;
import com.example.weather.WeatherForecastSoap;
import com.example.weather.WeatherForecasts;

/**
 * @version $Rev$ $Date$
 */
@Service(ZipCodeService.class)
@AllowsPassByReference
public class ZipCodeServiceImpl implements ZipCodeService {
    @Reference
    protected USZipSoap usZipService;

    @Reference
    protected WeatherForecastSoap weatherForecast;

    public String lookup(String zipCode) {
        GetInfoByZIPResult result1 = usZipService.getInfoByZIP(zipCode);

        GetInfoByZIPResponse response1 = new GetInfoByZIPResponse();
        response1.setGetInfoByZIPResult(result1);

        String xml1 = toXML(response1);

        WeatherForecasts result2 = weatherForecast.getWeatherByZipCode(zipCode);
        // Wrap the result so that it can be marshaled 
        GetWeatherByZipCodeResponse response2 = new GetWeatherByZipCodeResponse();
        response2.setGetWeatherByZipCodeResult(result2);

        String xml2 = toXML(response2);
        // Marshal the JAXB object into XML
        return xml1 + "\n" + xml2;
    }

    private String toXML(Object jaxb) {
        try {
            JAXBContext context = JAXBContext.newInstance(jaxb.getClass());
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jaxb, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
