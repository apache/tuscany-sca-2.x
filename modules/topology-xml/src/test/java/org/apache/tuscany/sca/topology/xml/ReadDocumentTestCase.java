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

package org.apache.tuscany.sca.topology.xml;

import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.topology.DefaultTopologyFactory;
import org.apache.tuscany.sca.topology.Node;
import org.apache.tuscany.sca.topology.Runtime;
import org.apache.tuscany.sca.topology.Scheme;
import org.apache.tuscany.sca.topology.Component;
import org.apache.tuscany.sca.topology.TopologyFactory;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class ReadDocumentTestCase extends TestCase {

    private ExtensibleURLArtifactProcessor documentProcessor;
    private TestModelResolver resolver; 

    public void setUp() throws Exception {
        AssemblyFactory factory = new DefaultAssemblyFactory();
        TopologyFactory topologyFactory = new DefaultTopologyFactory();
        
        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors); 
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new TopologyProcessor(topologyFactory, factory, staxProcessor));        
        
        // Create document processors
        XMLInputFactory inputFactory = XMLInputFactory.newInstance(); 
        documentProcessors.addArtifactProcessor(new TopologyDocumentProcessor(staxProcessor, inputFactory));

        resolver = new TestModelResolver(getClass().getClassLoader());
    }

    public void tearDown() throws Exception {
        documentProcessor = null;
        resolver = null;
    }

    public void testReadTopology() throws Exception {
        URL url = getClass().getResource("runtime.topology");
        Runtime runtime = (Runtime)documentProcessor.read(null, null, url);
        assertNotNull(runtime);
        
        Node node0 = runtime.getNodes().get(0);
        Scheme scheme0 = node0.getSchemes("nodomain").get(0);
        assertEquals(scheme0.getName(), "http");
        
        Node node1 = runtime.getNodes().get(1);
        Component component0 = node1.getComponents("domainA").get(0);
        assertEquals(component0.getName(), "AddServiceComponent");
    }
}
