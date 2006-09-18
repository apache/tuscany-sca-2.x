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

import java.net.URI;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class WSDLLoaderImplTestCase extends TestCase {
    private WSDLLoaderImpl loader;
    private URI exampleURI;
    private QName exampleName;

    public void testGetNamespace() throws UnresolveableResourceException {
        assertEquals("http://example.org/TicketAgent.wsdl20", loader.getNamespace(exampleURI));
    }

    public void testGetInterfaceNameFromFragment() throws UnresolveableResourceException, InvalidFragmentException {
        assertEquals("TicketAgent", loader.getInterfaceName("wsdl.interface(TicketAgent)"));
    }

    public void testGetInterfaceNameFromURI() throws UnresolveableResourceException, InvalidFragmentException {
        assertEquals(exampleName, loader.getInterfaceName(exampleURI));
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new WSDLLoaderImpl();
        exampleURI = URI.create("http://example.org/TicketAgent.wsdl20#wsdl.interface(TicketAgent)");
        exampleName = new QName("http://example.org/TicketAgent.wsdl20", "TicketAgent");
    }
}
