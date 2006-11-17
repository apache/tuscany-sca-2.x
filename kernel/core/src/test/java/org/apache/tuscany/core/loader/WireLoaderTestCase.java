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
package org.apache.tuscany.core.loader;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.InvalidWireException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.WireDefinition;

/**
 * @version $Rev: 471504 $ $Date: 2006-11-06 01:10:40 +0530 (Mon, 06 Nov 2006) $
 */
public class WireLoaderTestCase extends TestCase {
    private static final QName WIRE = new QName(XML_NAMESPACE_1_0, "wire");
    private static final QName SOURCE_URI = new QName(XML_NAMESPACE_1_0, "source.uri");
    private static final QName TARGET_URI = new QName(XML_NAMESPACE_1_0, "target.uri");
    
    private LoaderRegistry registry;
    private WireLoader loader;
    private XMLStreamReader reader;
    private DeploymentContext context;
    private CompositeComponent composite;

    public void testValidWire() throws LoaderException, XMLStreamException {
        
        expect(reader.getName()).andReturn(WIRE);
    	expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(SOURCE_URI).times(1);
        expect(reader.getElementText()).andReturn("source").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(TARGET_URI).times(2);
        expect(reader.getElementText()).andReturn("target").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.getName()).andReturn(WIRE).anyTimes();
        
        replay(registry, reader, context);
        WireDefinition wireDef = loader.load(composite, null, reader, context);
        
        assertNotNull(wireDef);
        verify(registry, reader, context);
    }
    
    public void testInValidWire_NoSourceElement() throws LoaderException, XMLStreamException {
        expect(reader.getName()).andReturn(WIRE).times(1);
    	expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(TARGET_URI).times(2);
        expect(reader.getElementText()).andReturn("target").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.next()).andReturn(END_ELEMENT);
        expect(reader.getName()).andReturn(WIRE).anyTimes();
        
        replay(registry, reader, context);
        try {
        	loader.load(composite, null, reader, context);
        	fail();
        } catch ( InvalidWireException e ) {
        	//expected behaviour
            //System.out.println(e);
        }
        verify(registry, reader, context);
    }
    
    public void testInValidWire_NoTargetElement() throws LoaderException, XMLStreamException {
        expect(reader.getName()).andReturn(WIRE).times(1);
    	expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(SOURCE_URI).times(1);
        expect(reader.getElementText()).andReturn("source").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.next()).andReturn(END_ELEMENT);
        expect(reader.getName()).andReturn(WIRE).anyTimes();
        
        replay(registry, reader, context);
        try {
        	loader.load(composite, null, reader, context);
        	fail();
        } catch ( InvalidWireException e ) {
        	//expected behaviour
        }
        verify(registry, reader, context);
    }
    
    public void testInValidWire_NoSourceSpecified() throws LoaderException, XMLStreamException {
        expect(reader.getName()).andReturn(WIRE).times(1);
    	expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(SOURCE_URI).times(1);
        expect(reader.getElementText()).andReturn("").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(TARGET_URI).times(2);
        expect(reader.getElementText()).andReturn("target").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.getName()).andReturn(WIRE).anyTimes();
        
        replay(registry, reader, context);
        try {
        	loader.load(composite, null, reader, context);
        	fail();
        } catch ( InvalidWireException e ) {
        	//expected behaviour
            //System.out.println(e);
        }
        //verify(registry, reader, context);
    }
    
    public void testInValidWire_NoTargetSpecified() throws LoaderException, XMLStreamException {
        expect(reader.getName()).andReturn(WIRE).times(1);
    	expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(SOURCE_URI).times(1);
        expect(reader.getElementText()).andReturn("source").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.next()).andReturn(START_ELEMENT);
        expect(reader.getName()).andReturn(TARGET_URI).times(2);
        expect(reader.getElementText()).andReturn("").times(1);
        expect(reader.next()).andReturn(END_ELEMENT);
        expect(reader.next()).andReturn(END_ELEMENT);
        
        expect(reader.getName()).andReturn(WIRE).anyTimes();
        
        replay(registry, reader, context);
        try {
        	loader.load(composite, null, reader, context);
        	fail();
        } catch ( InvalidWireException e ) {
        	//expected behaviour
        }
        //verify(registry, reader, context);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = createMock(LoaderRegistry.class);
        reader = createMock(XMLStreamReader.class);
        context = createMock(DeploymentContext.class);
        composite = createMock(CompositeComponent.class);
        
        loader = new WireLoader(registry);
    }
}
