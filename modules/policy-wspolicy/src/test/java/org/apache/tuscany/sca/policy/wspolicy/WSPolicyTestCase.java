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

package org.apache.tuscany.sca.policy.wspolicy;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.wspolicy.xml.WSPolicyProcessor;
import org.apache.tuscany.sca.policy.xml.PolicySetProcessor;
import org.junit.Assert;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class WSPolicyTestCase extends TestCase {

    private static final String WS_POLICY1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
      + "<definitions xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\""
            + " targetNamespace=\"http://test\""
            + " xmlns:test=\"http://test\""
            + " xmlns:sca=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\">"
            + " "
            + " <policySet name=\"SecureWSPolicy\""
            + " provides=\"test:confidentiality\""
            + " appliesTo=\"sca:binding.ws\""
            + " xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\""
            + " xmlns:sp=\"http://schemas.xmlsoap.org/ws/2002/12/secext\""
            + " xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\""
            + " xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">"
            + " <wsp:Policy>"
            + "    <wsp:ExactlyOne>"
            + "       <wsp:All>"
            + "          <tuscany:tuscanyWSPolicyAssertion anAttribute=\"fred\"/>"
            + "          <sp:SecurityToken>"
            + "             <sp:TokenType>sp:X509v3</sp:TokenType>"
            + "          </sp:SecurityToken>"
            + "          <sp:UsernameToken />"
            + "           <sp:SignedParts />"
            + "          <sp:EncryptedParts>"
            + "             <sp:Body />"
            + "          </sp:EncryptedParts>"
            + "          <sp:TransportBinding>"
            + "             <sp:IncludeTimeStamp />"
            + "          </sp:TransportBinding>"
            + "        </wsp:All>"
            + "    </wsp:ExactlyOne>"
            + " </wsp:Policy>"
            + " </policySet>"
      + " </definitions>";
    
    private static final String WS_POLICY2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
      + "<definitions xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\""
            + " targetNamespace=\"http://test\""
            + " xmlns:test=\"http://test\""
            + " xmlns:sca=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\">"
            + " "
            + " <policySet name=\"SecureWSPolicy\""
            + " provides=\"test:confidentiality\""
            + " appliesTo=\"sca:binding.ws\""
            + " xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\""
            + " xmlns:sp=\"http://schemas.xmlsoap.org/ws/2002/12/secext\""
            + " xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\""            
            + " xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">"
            + " <wsp:Policy>"
            + "    <wsp:ExactlyOne>"
            + "       <wsp:All>"
            + "          <tuscany:tuscanyWSPolicyAssertion anAttribute=\"jim\"/>"            
            + "          <sp:SecurityToken>"
            + "             <sp:TokenType>sp:X509v3</sp:TokenType>"
            + "          </sp:SecurityToken>"
            + "          <sp:UsernameToken />"
            + "           <sp:SignedParts />"
            + "          <sp:EncryptedParts>"
            + "             <sp:Body />"
            + "          </sp:EncryptedParts>"
            + "          <sp:TransportBinding>"
            + "             <sp:IncludeTimeStamp />"
            + "          </sp:TransportBinding>"
            + "        </wsp:All>"
            + "    </wsp:ExactlyOne>"
            + " </wsp:Policy>"
            + " </policySet>"
      + " </definitions>";    

    private XMLInputFactory inputFactory;

    @Override
    public void setUp() throws Exception {
        inputFactory = XMLInputFactory.newInstance();
    }

    public void testReadWsPolicy() throws Exception {
        // Set up the runtime
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        StAXArtifactProcessorExtensionPoint artifactExtensionPoint = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Object> extensibleStAXProcessor = new ExtensibleStAXArtifactProcessor(artifactExtensionPoint, inputFactory, outputFactory);
        artifactExtensionPoint.addArtifactProcessor(new TuscanyWSPolicyAssertionProcessor());
        
        StAXAttributeProcessorExtensionPoint attributeExtensionPoint = registry.getExtensionPoint(StAXAttributeProcessorExtensionPoint.class);
        StAXAttributeProcessor<Object> extensibleStAXAttributeProcessor = new ExtensibleStAXAttributeProcessor(attributeExtensionPoint, inputFactory, outputFactory);
        
        BuilderExtensionPoint builderExtensionPoint = registry.getExtensionPoint(BuilderExtensionPoint.class);
        
        StAXArtifactProcessor processor = artifactExtensionPoint.getProcessor(Definitions.class);
        
        Object artifact = null;
        
        // Read the first definitions string
        
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(WS_POLICY1));
        
        artifact = processor.read(reader, new ProcessorContext());
        assertNotNull(artifact);
        Assert.assertTrue(artifact instanceof Definitions);
        Definitions definitions1 = (Definitions) artifact;

        // Read the second definitions string
        
        reader = inputFactory.createXMLStreamReader(new StringReader(WS_POLICY2));

        artifact = processor.read(reader, new ProcessorContext());
        assertNotNull(artifact);
        Assert.assertTrue(artifact instanceof Definitions);
        Definitions definitions2 = (Definitions) artifact;  
        
        // compare the policies using the policy builder
        
        // create dummy endpoints and endpoint references
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        EndpointReference epr = assemblyFactory.createEndpointReference();
        Endpoint ep = assemblyFactory.createEndpoint();
        
        // add the ws polices we've just read to the epr/ep
        epr.getPolicySets().add(definitions1.getPolicySets().get(0));
        ep.getPolicySets().add(definitions1.getPolicySets().get(0));
        
        BuilderContext builderContext = new BuilderContext((Monitor)null);
        
        for (PolicyBuilder policyBuilder : builderExtensionPoint.getPolicyBuilders()) {
            System.out.println("PolicyBuilder: " + policyBuilder.toString());
            assertTrue(policyBuilder.build(epr, ep, builderContext));
        }
        
    }
}
