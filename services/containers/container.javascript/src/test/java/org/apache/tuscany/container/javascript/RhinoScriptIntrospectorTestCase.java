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
package org.apache.tuscany.container.javascript;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import helloworld.HelloWorldService;
import junit.framework.TestCase;
import org.apache.tuscany.container.javascript.rhino.RhinoSCAConfig;
import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.apache.tuscany.idl.wsdl.XMLSchemaRegistryImpl;

public class RhinoScriptIntrospectorTestCase extends TestCase {

    private static final WSDLDefinitionRegistryImpl.Monitor NULL_MONITOR = new WSDLDefinitionRegistryImpl.Monitor() {
        public void readingWSDL(String namespace, URL location) {
        }

        public void cachingDefinition(String namespace, URL location) {
        }
    };

    public void testJavaInterface() throws MissingResourceException, InvalidServiceContractException {
        RhinoScript rs =
            new RhinoScript("javaInterfaceTest", "SCA = { javaInterface : 'helloworld.HelloWorldService',};",
                null, getClass().getClassLoader());
        RhinoSCAConfig scaConfig = new RhinoSCAConfig(rs.getScriptScope());
        JavaScriptIntrospector introspector =
            new JavaScriptIntrospector(null, new JavaInterfaceProcessorRegistryImpl());
        ComponentType comonentType = introspector.introspectScript(scaConfig, rs.getClassLoader());
        assertNotNull(comonentType);
        Map services = comonentType.getServices();
        assertEquals(1, services.size());
        ServiceDefinition serviceDefinition = (ServiceDefinition) services.values().iterator().next();
        ServiceContract serviceContract = serviceDefinition.getServiceContract();
        assertTrue(serviceContract instanceof JavaServiceContract);
        JavaServiceContract javaServiceContract = (JavaServiceContract) serviceContract;
        assertEquals(HelloWorldService.class, javaServiceContract.getInterfaceClass());
    }

    public void testWSDLLocation() throws WSDLException {
//        RhinoScript rs = new RhinoScript("wsdlLocation",
//                "SCA = { wsdlLocation : 'src/test/resources/org/apache/tuscany/container/javascript/rhino/helloworld.wsdl',};", null, getClass()
//                        .getClassLoader());
//        RhinoSCAConfig scaConfig = new RhinoSCAConfig(rs.getScriptScope());
//        JavaScriptIntrospector introspector = new JavaScriptIntrospector(null);
//        ComponentType comonentType = introspector.introspectScript(scaConfig, rs.getClassLoader());
//        assertNotNull(comonentType);
//        Map serviceBindings = comonentType.getServices();
//        assertEquals(1, serviceBindings.size());
//        ServiceDefinition serviceDefinition = (ServiceDefinition) serviceBindings.values().iterator().next();
//        ServiceContract serviceContract = serviceDefinition.getServiceContract();
//        assertTrue(serviceContract instanceof WSDLServiceContract);
//        WSDLServiceContract wsdlServiceContract = (WSDLServiceContract) serviceContract;
//        assertEquals(new QName("http://helloworld", "HelloWorld"), wsdlServiceContract.getPortType().getQName());
    }

    public void testWSDLPortType() throws WSDLException, IOException, MissingResourceException,
                                          InvalidServiceContractException {
        RhinoScript rs = new RhinoScript("wsdlPortType",
            "SCA = { wsdlPortType : 'HelloWorld', wsdlNamespace : 'http://helloworld',};", null,
            getClass().getClassLoader());
        RhinoSCAConfig scaConfig = new RhinoSCAConfig(rs.getScriptScope());

        WSDLDefinitionRegistryImpl wsdlReg = new WSDLDefinitionRegistryImpl();
        wsdlReg.setSchemaRegistry(new XMLSchemaRegistryImpl());
        wsdlReg.setMonitor(NULL_MONITOR);
        URL wsdlURL =
            getClass().getClassLoader().getResource("org/apache/tuscany/container/javascript/rhino/helloworld.wsdl");
        wsdlReg.loadDefinition("http://helloworld", wsdlURL);

        JavaScriptIntrospector introspector =
            new JavaScriptIntrospector(wsdlReg, new JavaInterfaceProcessorRegistryImpl());
        ComponentType comonentType = introspector.introspectScript(scaConfig, rs.getClassLoader());
        assertNotNull(comonentType);
        Map services = comonentType.getServices();
        assertEquals(1, services.size());
        ServiceDefinition serviceDefinition = (ServiceDefinition) services.values().iterator().next();
        ServiceContract serviceContract = serviceDefinition.getServiceContract();
        assertTrue(serviceContract instanceof WSDLServiceContract);
        WSDLServiceContract wsdlServiceContract = (WSDLServiceContract) serviceContract;
        assertEquals(new QName("http://helloworld", "HelloWorld"), wsdlServiceContract.getPortType().getQName());
    }
}
