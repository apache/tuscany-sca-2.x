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

package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import junit.framework.Assert;

public class Utils {

    public static void invokeJSONPEndpoint(String s) throws MalformedURLException, IOException {
        URL url = new URL(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String response = br.readLine();
        Assert.assertEquals("foo(\"Hello petra\");", response);
    }

    public static void invokeWSEndpoint(String endpoint) throws Exception {
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL(endpoint + "?wsdl");
        Assert.assertNotNull(definition);
        Service service = (Service)definition.getServices().values().iterator().next();
        Port port = (Port)service.getPorts().values().iterator().next();

        Assert.assertEquals(new URL(endpoint).getPath(), new URL(getEndpoint(port)).getPath());
    }
    
    private static String getEndpoint(Port port) {
        List<?> wsdlPortExtensions = port.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAPAddress) {
                return ((SOAPAddress) extension).getLocationURI();
            }
        }
        throw new RuntimeException("no SOAPAddress");
    }
    
}
