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
package weather;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

import com.example.weather.GetWeatherByZipCodeResponse;
import com.example.weather.WeatherForecastSoap;
import com.example.weather.WeatherForecasts;

/**
 * This client program to invoke the Mortgage LoanApproval service
 */
public class WeatherForecastClient {

    public static void main(String[] args) throws Exception {

        String location = ContributionLocationHelper.getContributionLocation(WeatherForecastImpl.class);
        Node node = NodeFactory.newInstance().createNode("WeatherForecast.composite", new Contribution("c1", location));
        node.start();
        testJAXWS(node);

        node.stop();
    }

    static void testJAXWS(Node node) throws JAXBException, PropertyException {
        WeatherForecastSoap weatherService = node.getService(WeatherForecastSoap.class, "WeatherForecastService");

        WeatherForecasts result = weatherService.getWeatherByZipCode("94555");

        // Dump the result as XML

        // Wrap the result so that it can be marshaled
        GetWeatherByZipCodeResponse response = new GetWeatherByZipCodeResponse();
        response.setGetWeatherByZipCodeResult(result);

        // Marshal the JAXB object into XML
        JAXBContext context = JAXBContext.newInstance(GetWeatherByZipCodeResponse.class);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(response, writer);

        String xml = writer.toString();
        System.out.println(xml);
    }
}
