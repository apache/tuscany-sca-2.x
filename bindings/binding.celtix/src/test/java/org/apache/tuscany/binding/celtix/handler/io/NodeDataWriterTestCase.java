/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.celtix.handler.io;

import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import commonj.sdo.helper.TypeHelper;

import junit.framework.TestCase;

import org.apache.tuscany.sdo.helper.XSDHelperImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.objectweb.celtix.bindings.DataReader;
import org.objectweb.celtix.bindings.DataWriter;
import org.objectweb.celtix.bus.bindings.WSDLMetaDataCache;
import org.objectweb.celtix.context.ObjectMessageContext;
import org.objectweb.celtix.context.ObjectMessageContextImpl;



public class NodeDataWriterTestCase extends TestCase {

    private TypeHelper typeHelper;
    
    protected void setUp() throws Exception {
        super.setUp();
        DataObjectUtil.initRuntime();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            typeHelper = SDOUtil.createTypeHelper();
            URL url = getClass().getResource("/wsdl/hello_world.wsdl");
            new XSDHelperImpl(typeHelper).define(url.openStream(), null);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
        

    }
    
    public void testWriteWrapper() throws Exception {
        WSDLReader wreader =  WSDLFactory.newInstance().newWSDLReader();
        wreader.setFeature("javax.wsdl.verbose", false);
        URL url = getClass().getResource("/wsdl/hello_world.wsdl");
        Definition definition = wreader.readWSDL(url.toString());
        Port port = definition.getService(new QName("http://objectweb.org/hello_world_soap_http",
                                                    "SOAPService")).getPort("SoapPort");
        
        WSDLMetaDataCache wsdlCache = new WSDLMetaDataCache(definition,
                                                            port);
        
        SCADataBindingCallback callback = new SCADataBindingCallback(wsdlCache.getOperationInfo("greetMe"),
                                                                     typeHelper,
                                                                     false);
        
        DataWriter<Node> writer = callback.createWriter(Node.class);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = doc.createElement("ROOT");
        
        ObjectMessageContext objCtx = new ObjectMessageContextImpl();
        objCtx.setMessageObjects(new Object[] {"Hello"});
        writer.writeWrapper(objCtx , false, element);
        
        assertEquals("Value not written", "Hello", element.getFirstChild().getTextContent().trim());
        
        DataReader<Node> reader = callback.createReader(Node.class);
        reader.readWrapper(objCtx , true, element);
        
        assertEquals("Hello", objCtx.getReturn());
    }

}
