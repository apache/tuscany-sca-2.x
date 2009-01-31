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
package location;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import com.example.uszip.GetInfoByZIPResponse;
import com.example.uszip.USZipSoap;

/**
 * This client program to invoke the Mortgage LoanApproval service
 */
public class LocationClient {

    public static void main(String[] args) throws Exception {

        SCADomain domain = SCADomain.newInstance("USLocation.composite");
        USZipSoap zipService = domain.getService(USZipSoap.class, "USLocationService");

        GetInfoByZIPResponse.GetInfoByZIPResult result = zipService.getInfoByZIP("94555");

        GetInfoByZIPResponse response = new GetInfoByZIPResponse();
        response.setGetInfoByZIPResult(result);

        JAXBContext context = JAXBContext.newInstance(GetInfoByZIPResponse.class);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(response, writer);

        String xml = writer.toString();
        System.out.println(xml);
    }
}
