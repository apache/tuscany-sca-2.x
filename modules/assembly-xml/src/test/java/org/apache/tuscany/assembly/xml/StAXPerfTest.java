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

package org.apache.tuscany.assembly.xml;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.services.spi.contribution.DefaultStAXArtifactProcessorRegistry;

/**
 * Test the performance of StAX parsing.
 * 
 * @version $Rev$ $Date$
 */
public class StAXPerfTest {

    private XMLInputFactory inputFactory;
    private AssemblyFactory assemblyFactory;
    private PolicyFactory policyFactory;
    private DefaultStAXArtifactProcessorRegistry registry;

    public static void main(String[] args) throws Exception {

        StAXPerfTest perfTest = new StAXPerfTest();
        perfTest.setUp();

        // Warm up
        for (long i = 0; i < 500; i++) {
            perfTest.testReadComposite();
        }

        long begin = System.currentTimeMillis();
        long iter = 50000;
        for (long i = 0; i < iter; i++) {
            perfTest.testReadComposite();
        }
        long end = System.currentTimeMillis();
        System.out.println("Iterations: " + iter);
        double time = ((double)(end - begin)) / ((double)iter);
        System.out.println("Time: " + time);
        System.out.println("Memory: " + Runtime.getRuntime().totalMemory() / 1024);

    }

    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
        assemblyFactory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
        registry = new DefaultStAXArtifactProcessorRegistry();
    }

    public void tearDown() throws Exception {
        assemblyFactory = null;
        policyFactory = null;
        inputFactory = null;
        registry = null;
    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("TestAllCalculator.composite");
        CompositeProcessor loader = new CompositeProcessor(assemblyFactory, policyFactory, registry);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);

        Composite composite = loader.read(reader);
        is.close();

        if (composite == null) {
            throw new IllegalStateException("Null composite");
        }
    }

}
