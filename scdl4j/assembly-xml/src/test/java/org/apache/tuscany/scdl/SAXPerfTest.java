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
import org.apache.tuscany.assembly.model.Composite;
import org.apache.tuscany.assembly.model.impl.DefaultAssemblyFactory;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.policy.model.impl.DefaultPolicyFactory;
import org.apache.tuscany.scdl.impl.CompositeHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Test the usability of the assembly model API when loading SCDL
 * 
 * @version $Rev$ $Date$
 */
public class SAXPerfTest extends TestCase {

    XMLReader reader;
    AssemblyFactory assemblyFactory;
    PolicyFactory policyFactory;
    
	public static void main(String[] args) throws Exception {
		
		SAXPerfTest perfTest = new SAXPerfTest();
		perfTest.setUp();
		
		// Warm up
		for (long i = 0; i<500; i++) {
			perfTest.testReadComposite();
		}
		
		long begin = System.currentTimeMillis();
		long iter = 50000;
		for (long i = 0; i<iter; i++) {
			perfTest.testReadComposite();
		}
		long end = System.currentTimeMillis();
		System.out.println("Iterations: "+ iter);
		double time = ((double)(end - begin)) / ((double)iter);
		System.out.println("Time: "+ time);
		System.out.println("Memory: "+Runtime.getRuntime().totalMemory()/1024);
		
	}

    public void setUp() throws Exception {
        reader = XMLReaderFactory.createXMLReader("com.ctc.wstx.sax.WstxSAXParser");
        
        assemblyFactory = new DefaultAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
    }

    public void tearDown() throws Exception {
        assemblyFactory = null;
        policyFactory = null;
        reader = null;
    }

    public void testReadComposite() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("TestAllCalculator.composite");
        CompositeHandler handler = new CompositeHandler(assemblyFactory, policyFactory, null, null, null);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        
        Composite composite = handler.getComposite();

        if (composite == null)
        	throw new IllegalStateException("Null composite");
    }

}
