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
package sample;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.DOMInvoker;
import org.apache.tuscany.sca.runtime.TuscanyComponentContext;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Init;
import org.xml.sax.SAXException;

import test.EndpointHelper;


public class HelloworldDynamicWSImpl implements Helloworld {

    @Context
    private TuscanyComponentContext context;

    private DOMHelper domHelper;
    private Node node;

    @Init
    public void init() {
        domHelper = DOMHelper.getInstance(context.getExtensionPointRegistry());
        node = context.getNode();
    }
    
    public String sayHello(String name) {
        try {
            
            String curi = node.installContribution("src/test/resources/resources.zip");

            Endpoint endpoint = EndpointHelper.createWSEndpoint("SomeEndpointName", new QName("http://sample/", "Helloworld"), "http://localhost:8080/testComponent/Helloworld", curi, node);

            ((NodeImpl)node).getEndpointRegistry().addEndpoint(endpoint);

            DOMInvoker domInvoker = node.getDOMInvoker("SomeEndpointName");
            
            org.w3c.dom.Node arg = getRequestDOM(name);
            org.w3c.dom.Node response = domInvoker.invoke("sayHello", arg);
            return "Remote WS says: " + getResponseString(response);
            
        } catch (ContributionReadException e) {
            throw new RuntimeException(e);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        } catch (NoSuchServiceException e) {
            throw new RuntimeException(e);
        } catch (InvalidInterfaceException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResponseString(org.w3c.dom.Node responseDOM) {
        String xml = domHelper.saveAsString(responseDOM); 
        int x = xml.indexOf("<return>") + "<return>".length();
        int y = xml.indexOf("</return>");
        return xml.substring(x, y);
    }

    private org.w3c.dom.Node getRequestDOM(String name) {
        try {

            String xml = "<ns2:sayHello xmlns:ns2=\"http://sample/\"><arg0>"+ name + "</arg0></ns2:sayHello>";
            return domHelper.load(xml);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
    

}
