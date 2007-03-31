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

package org.apache.tuscany.scdl;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.util.PrintUtil;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.policy.model.impl.DefaultPolicyFactory;
import org.apache.tuscany.scdl.impl.ComponentTypeHandler;
import org.apache.tuscany.scdl.impl.CompositeHandler;
import org.apache.tuscany.scdl.impl.ConstrainingTypeHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Test the usability of the assembly model API when loading SCDL
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    XMLReader reader;
    AssemblyFactory assemblyFactory;
    PolicyFactory policyFactory;
    
    public void setUp() throws Exception {
        reader = XMLReaderFactory.createXMLReader();
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        
        assemblyFactory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
    }

    public void tearDown() throws Exception {
        assemblyFactory = null;
        policyFactory = null;
        reader = null;
    }

    public void testReadComponentType() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("CalculatorImpl.componentType");
        ComponentTypeHandler handler = new ComponentTypeHandler(assemblyFactory, policyFactory, null);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        assertNotNull(handler.getComponentType());
        
        new PrintUtil(System.out).print(handler.getComponentType());
    }

    public void testReadConstrainingType() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("CalculatorComponent.constrainingType");
        ConstrainingTypeHandler handler = new ConstrainingTypeHandler(assemblyFactory, policyFactory, null);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        assertNotNull(handler.getConstrainingType());

        new PrintUtil(System.out).print(handler.getConstrainingType());
    }
    
    public static void main(String[] args) throws Exception {
		ReadTestCase tc = new ReadTestCase();
		tc.setUp();
		tc.testReadComposite();
	}

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("Calculator.composite");
        CompositeHandler handler = new CompositeHandler(assemblyFactory, policyFactory, null, null, null);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        assertNotNull(handler.getComposite());

        new PrintUtil(System.out).print(handler.getComposite());
    }

    public void testReadCompositeAndWireIt() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("Calculator.composite");
        CompositeHandler handler = new CompositeHandler(assemblyFactory, policyFactory, null, null, null);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        assertNotNull(handler.getComposite());
        
        new CompositeUtil(assemblyFactory, handler.getComposite()).configure(null);

        new PrintUtil(System.out).print(handler.getComposite());
    }

}
