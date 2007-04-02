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

package org.apache.tuscany.implementation.java.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.ComponentType;
import org.apache.tuscany.assembly.model.Composite;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.assembly.model.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.assembly.util.PrintUtil;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.policy.model.impl.DefaultPolicyFactory;
import org.apache.tuscany.scdl.stax.LoaderRegistry;
import org.apache.tuscany.scdl.stax.impl.ComponentTypeLoader;
import org.apache.tuscany.scdl.stax.impl.CompositeLoader;
import org.apache.tuscany.scdl.stax.impl.ConstrainingTypeLoader;
import org.apache.tuscany.scdl.stax.impl.LoaderRegistryImpl;

/**
 * Test the usability of the assembly model API when loading SCDL
 * 
 * @version $Rev$ $Date$
 */
public class ReadTestCase extends TestCase {

    XMLInputFactory inputFactory;
    AssemblyFactory assemblyFactory;
    PolicyFactory policyFactory;
    LoaderRegistry loaderRegistry;
    
    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        assemblyFactory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
        loaderRegistry = new LoaderRegistryImpl();

        JavaImplementationLoader javaReader = new JavaImplementationLoader(new DefaultJavaImplementationFactory(assemblyFactory));
        loaderRegistry.addLoader(JavaImplementationConstants.IMPLEMENTATION_JAVA_QNAME, javaReader);
    }

    public void tearDown() throws Exception {
        inputFactory = null;
        assemblyFactory = null;
        policyFactory = null;
        loaderRegistry = null;
    }

    public void testReadComponentType() throws Exception {
        ComponentTypeLoader componentTypeReader = new ComponentTypeLoader(assemblyFactory, policyFactory, loaderRegistry);
        InputStream is = getClass().getClassLoader().getResourceAsStream("CalculatorImpl.componentType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ComponentType componentType = componentTypeReader.load(reader);
        assertNotNull(componentType);
        
        new PrintUtil(System.out).print(componentType);
    }

    public void testReadConstrainingType() throws Exception {
        ConstrainingTypeLoader constrainingTypeReader = new ConstrainingTypeLoader(assemblyFactory, policyFactory, loaderRegistry);
        InputStream is = getClass().getClassLoader().getResourceAsStream("CalculatorComponent.constrainingType");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        ConstrainingType constrainingType = constrainingTypeReader.load(reader);
        assertNotNull(constrainingType);

        new PrintUtil(System.out).print(constrainingType);
    }
    
    public void testReadComposite() throws Exception {
        CompositeLoader compositeReader = new CompositeLoader(assemblyFactory, policyFactory, loaderRegistry);
        InputStream is = getClass().getClassLoader().getResourceAsStream("Calculator.composite");
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        Composite composite = compositeReader.load(reader);
        assertNotNull(composite);

        new CompositeUtil(assemblyFactory, composite).configure(null);

        new PrintUtil(System.out).print(composite);
    }

}
