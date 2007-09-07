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

package org.apache.tuscany.sca.implementation.bpel;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.bpel.impl.BPELDocumentProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;

/**
 * @version $Rev$ $Date$
 */
public class BPELDocumentProcessorTestCase extends TestCase {
    
    protected static final String BPEL_PROCESS_FILE = "HelloWorld.bpel";

    private ModelFactoryExtensionPoint modelFactories;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
       
        modelFactories = new DefaultModelFactoryExtensionPoint();
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        modelFactories.addFactory(assemblyFactory);
        BPELFactory bpelFactory = new DefaultBPELFactory(new DefaultAssemblyFactory(), new DefaultWSDLFactory());
        modelFactories.addFactory(bpelFactory);
    }

    public void testLoadBPELProcessDefinition() throws Exception {
        BPELDocumentProcessor bpelDocumentProcessor = new BPELDocumentProcessor(modelFactories);
        
        URI processURI = getClass().getClassLoader().getResource(BPEL_PROCESS_FILE).toURI();
        URL processLocation = getClass().getClassLoader().getResource(BPEL_PROCESS_FILE);
        BPELProcessDefinition bpelProcessDefinition = bpelDocumentProcessor.read(null, processURI, processLocation);
        
        assertNotNull(bpelProcessDefinition);
        assertEquals(new QName("http://schemas.xmlsoap.org/ws/2004/03/business-process/", "process"), bpelProcessDefinition.getName());
        assertEquals(processLocation, bpelProcessDefinition.getLocation());
        assertEquals(true, bpelProcessDefinition.isUnresolved());
    }
}
