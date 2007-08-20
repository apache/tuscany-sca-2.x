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

package org.apache.tuscany.sca.policy.xml;

import java.net.URI;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;
import org.apache.tuscany.sca.policy.SCADefinitions;
import org.apache.tuscany.sca.policy.impl.DefaultIntentAttachPointTypeFactoryImpl;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 551296 $ $Date: 2007-06-28 01:18:35 +0530 (Thu, 28 Jun 2007) $
 */
public class ReadDocumentTestCase extends TestCase {

    private ModelResolver resolver; 
    private SCADefinitionsDocumentProcessor scaDefnDocProcessor = null;
    private SCADefinitionsProcessor scaDefnProcessor = null;
    private SCADefinitions scaDefinitions;
    Map<QName, Intent> intentTable = new Hashtable<QName, Intent>();
    Map<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
    Map<QName, IntentAttachPointType> bindingTypesTable = new Hashtable<QName, IntentAttachPointType>();
    Map<QName, IntentAttachPointType> implTypesTable = new Hashtable<QName, IntentAttachPointType>();
    public static final String namespace = "http://www.osoa.org/xmlns/sca/1.0";
    
    private static final QName confidentiality = new QName(namespace, "confidentiality");
    private static final QName integrity = new QName(namespace, "integrity");
    private static final QName messageProtection = new QName(namespace, "messageProtection");
    private static final QName confidentiality_transport = new QName(namespace, "confidentiality.transport");   
    private static final QName confidentiality_message = new QName(namespace, "confidentiality.message");
    private static final QName secureReliablePolicy = new QName(namespace, "SecureReliablePolicy");
    private static final QName secureMessagingPolicies = new QName(namespace, "SecureMessagingPolicies");
    private static final QName securityPolicy = new QName(namespace, "SecurityPolicy");
    private static final QName basicAuthMsgProtSecurity = new QName(namespace, "BasicAuthMsgProtSecurity");
    private static final QName wsBinding = new QName(namespace, "binding.ws");
    private static final QName javaImpl = new QName(namespace, "implementation.java");
    

    @Override
    public void setUp() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        IntentAttachPointTypeFactory intentAttachPointFactory = new DefaultIntentAttachPointTypeFactoryImpl();
        this.resolver = new SCADefinitionsResolver();
        
        // Create Stax processors
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(new DefaultModelFactoryExtensionPoint());
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        
        scaDefnDocProcessor = new SCADefinitionsDocumentProcessor(staxProcessor, inputFactory);
        scaDefnProcessor = new SCADefinitionsProcessor(policyFactory, staxProcessor, resolver);
        
        staxProcessors.addArtifactProcessor(scaDefnProcessor);
        staxProcessors.addArtifactProcessor(new SimpleIntentProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ProfileIntentProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new QualifiedIntentProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new PolicySetProcessor(policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ImplementationTypeProcessor(policyFactory, intentAttachPointFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new BindingTypeProcessor(policyFactory, intentAttachPointFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new MockPolicyProcessor());
        
        URL url = getClass().getResource("definitions.xml");
        URI uri = URI.create("definitions.xml");
        scaDefinitions = scaDefnDocProcessor.read(null, uri, url);
        
        for ( Intent intent : scaDefinitions.getPolicyIntents() ) {
            intentTable.put(intent.getName(), intent);
        }
        
        for ( PolicySet policySet : scaDefinitions.getPolicySets() ) {
            policySetTable.put(policySet.getName(), policySet);
        }
        
        for ( IntentAttachPointType bindingType : scaDefinitions.getBindingTypes() ) {
            bindingTypesTable.put(bindingType.getName(), bindingType);
        }
        
        for ( IntentAttachPointType implType : scaDefinitions.getImplementationTypes() ) {
            implTypesTable.put(implType.getName(), implType);
        }
    }

    @Override
    public void tearDown() throws Exception {
        resolver = null;
        scaDefnDocProcessor = null;
        scaDefnProcessor = null;
    }

    public void testReadSCADefinitions() throws Exception {
        assertNotNull(scaDefinitions);
        
        assertNotNull(intentTable.get(confidentiality));
        assertNotNull(intentTable.get(messageProtection));
        assertNotNull(intentTable.get(confidentiality_transport));
        assertTrue(intentTable.get(confidentiality).getDescription().length() > 0 );
        
        assertNotNull(policySetTable.get(secureReliablePolicy));
        assertTrue(policySetTable.get(secureReliablePolicy).getProvidedIntents().size() == 2);
        assertTrue(policySetTable.get(secureReliablePolicy).getPolicies().size() == 2);
        
        assertNotNull(policySetTable.get(secureMessagingPolicies));
        assertEquals(policySetTable.get(secureMessagingPolicies).getMappedPolicies().size(), 3);
        
        assertEquals(bindingTypesTable.size(), 1);
        assertNotNull(bindingTypesTable.get(wsBinding));
        assertEquals(implTypesTable.size(), 1);
        assertNotNull(implTypesTable.get(javaImpl));
    }
    
    public void testResolveSCADefinitions() throws Exception {
        assertTrue(intentTable.get(messageProtection) instanceof ProfileIntent);
        ProfileIntent profileIntent = (ProfileIntent)intentTable.get(new QName(namespace, "messageProtection"));
        assertNull(profileIntent.getRequiredIntents().get(0).getDescription());
        
        QName confidentiality_transport = new QName(namespace, "confidentiality.transport"); 
        assertTrue(intentTable.get(confidentiality_transport) instanceof QualifiedIntent);
        QualifiedIntent qualifiedIntent = (QualifiedIntent)intentTable.get(new QName(namespace, "confidentiality.transport"));
        assertNull(qualifiedIntent.getQualifiableIntent().getDescription());
        
        PolicySet secureReliablePolicySet = policySetTable.get(secureReliablePolicy);
        PolicySet secureMessagingPolicySet = policySetTable.get(secureMessagingPolicies);
        PolicySet securityPolicySet = policySetTable.get(securityPolicy);
        
        assertEquals(secureReliablePolicySet.getProvidedIntents().get(1).getName(), integrity);
        assertNull(secureReliablePolicySet.getProvidedIntents().get(1).getDescription());
        assertTrue(secureMessagingPolicySet.isUnresolved());
        assertEquals(securityPolicySet.getMappedPolicies().size(), 5);
        
        //testing to ensure that inclusion of referred policy sets has not happened
        PolicySet basicAuthMsgProtSecurityPolicySet = policySetTable.get(basicAuthMsgProtSecurity);
        assertTrue(basicAuthMsgProtSecurityPolicySet.getPolicies().isEmpty());
        assertTrue(basicAuthMsgProtSecurityPolicySet.getMappedPolicies().isEmpty());
        
        IntentAttachPointType wsBindingType = bindingTypesTable.get(wsBinding);
        assertNull(wsBindingType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNull(wsBindingType.getMayProvideIntents().get(0).getDescription());
        
        IntentAttachPointType javaImplType = implTypesTable.get(javaImpl);
        assertNull(javaImplType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNull(javaImplType.getMayProvideIntents().get(0).getDescription());
        
        scaDefnDocProcessor.resolve(scaDefinitions, resolver);
        //builder.build(scaDefinitions);
        
        //testing if policy intents have been linked have property been linked up 
        assertNotNull(profileIntent.getRequiredIntents().get(0).getDescription());
        assertNotNull(qualifiedIntent.getQualifiableIntent().getDescription());
        assertEquals(secureReliablePolicySet.getProvidedIntents().get(1).getName(), integrity);
        assertNotNull(secureReliablePolicySet.getProvidedIntents().get(1).getDescription());
        
        //testing if policysets have been properly linked up with intents
        assertFalse(secureMessagingPolicySet.isUnresolved());
        assertNotNull(secureMessagingPolicySet.getMappedPolicies().get(intentTable.get(confidentiality)));
        assertNotNull(secureMessagingPolicySet.getMappedPolicies().get(intentTable.get(confidentiality_transport)));
        
        //testing if intent maps have been properly mapped to policies
        assertFalse(securityPolicySet.isUnresolved());
        assertNotNull(securityPolicySet.getMappedPolicies().get(intentTable.get(confidentiality)));
        assertNotNull(securityPolicySet.getMappedPolicies().get(intentTable.get(confidentiality_message)));
        
        //testing for inclusion of referred policysets
        assertFalse(basicAuthMsgProtSecurityPolicySet.getPolicies().isEmpty());
        assertFalse(basicAuthMsgProtSecurityPolicySet.getMappedPolicies().isEmpty());
        assertNotNull(basicAuthMsgProtSecurityPolicySet.getMappedPolicies().get(intentTable.get(confidentiality_transport)));
        
        assertNotNull(wsBindingType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNotNull(wsBindingType.getMayProvideIntents().get(0).getDescription());
        
        assertNotNull(javaImplType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNotNull(javaImplType.getMayProvideIntents().get(0).getDescription());
    }
}
