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
package org.apache.tuscany.idl.wsdl;

import java.io.IOException;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * Verifies the default WSDL registry implementation
 *
 * @version $Rev$ $Date$
 */
public class WSDLDefinitionRegistryTestCase extends TestCase {
    private static final String NS = "http://www.example.org";
    private static final WSDLDefinitionRegistryImpl.Monitor NULL_MONITOR = new WSDLDefinitionRegistryImpl.Monitor() {
        public void readingWSDL(String namespace, URL location) {
        }

        public void cachingDefinition(String namespace, URL location) {
        }
    };
    private WSDLDefinitionRegistryImpl wsdlRegistry;
    private ClassLoader cl;
    private URL exampleWsdl;


    public void testLoadFromAbsoluteWSDLLocation() {
        try {
            Definition def = wsdlRegistry.loadDefinition(NS + ' ' + exampleWsdl, cl);
            Assert.assertNotNull(def.getPortType(new QName(NS, "HelloWorld")));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (WSDLException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testLoadFromRelativeWSDLLocation() {
        try {
            Definition def = wsdlRegistry.loadDefinition(NS + " org/apache/tuscany/idl/wsdl/example.wsdl", cl);
            Assert.assertNotNull(def.getPortType(new QName(NS, "HelloWorld")));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (WSDLException e) {
            Assert.fail(e.getMessage());
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        wsdlRegistry = new WSDLDefinitionRegistryImpl();
        wsdlRegistry.setSchemaRegistry(new XMLSchemaRegistryImpl());
        wsdlRegistry.setMonitor(NULL_MONITOR);
        exampleWsdl = getClass().getResource("example.wsdl");
        cl = getClass().getClassLoader();
    }

}
