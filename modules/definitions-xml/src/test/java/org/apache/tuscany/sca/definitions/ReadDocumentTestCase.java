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

package org.apache.tuscany.sca.definitions;

import java.net.URI;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 551296 $ $Date: 2007-06-28 01:18:35 +0530 (Thu, 28 Jun 2007) $
 */
public class ReadDocumentTestCase extends TestCase {

    private URLArtifactProcessor<SCADefinitions> policyDefinitionsProcessor = null;
    private SCADefinitions definitions;
    Map<QName, Intent> intentTable = new Hashtable<QName, Intent>();
    Map<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
    Map<QName, IntentAttachPointType> bindingTypesTable = new Hashtable<QName, IntentAttachPointType>();
    Map<QName, IntentAttachPointType> implTypesTable = new Hashtable<QName, IntentAttachPointType>();
    public static final String scaNamespace = "http://www.osoa.org/xmlns/sca/1.0";
    public static final String namespace = "http://test";
    
    private static final QName secureWsPolicy = new QName(namespace, "SecureWSPolicy");
    private static final QName confidentiality = new QName(namespace, "confidentiality");
    private static final QName integrity = new QName(namespace, "integrity");
    private static final QName messageProtection = new QName(namespace, "messageProtection");
    private static final QName confidentiality_transport = new QName(namespace, "confidentiality.transport");   
    private static final QName confidentiality_message = new QName(namespace, "confidentiality.message");
    private static final QName secureReliablePolicy = new QName(namespace, "SecureReliablePolicy");
    private static final QName secureMessagingPolicies = new QName(namespace, "SecureMessagingPolicies");
    private static final QName securityPolicy = new QName(namespace, "SecurityPolicy");
    private static final QName basicAuthMsgProtSecurity = new QName(namespace, "BasicAuthMsgProtSecurity");
    private static final QName wsBinding = new QName(scaNamespace, "binding.ws");
    private static final QName javaImpl = new QName(scaNamespace, "implementation.java");
    

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Create StAX processors
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());
        
        URLArtifactProcessorExtensionPoint documentProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        policyDefinitionsProcessor = documentProcessors.getProcessor(SCADefinitions.class); 
        
        URL url = getClass().getResource("test_definitions.xml");
        URI uri = URI.create("test_definitions.xml");
        definitions = policyDefinitionsProcessor.read(null, uri, url);
        
        for ( Intent intent : definitions.getPolicyIntents() ) {
            intentTable.put(intent.getName(), intent);
        }
        
        for ( PolicySet policySet : definitions.getPolicySets() ) {
            policySetTable.put(policySet.getName(), policySet);
        }
        
        for ( IntentAttachPointType bindingType : definitions.getBindingTypes() ) {
            bindingTypesTable.put(bindingType.getName(), bindingType);
        }
        
        for ( IntentAttachPointType implType : definitions.getImplementationTypes() ) {
            implTypesTable.put(implType.getName(), implType);
        }
    }

    public void testReadSCADefinitions() throws Exception {
        assertNotNull(definitions);
        
        assertNotNull(intentTable.get(confidentiality));
        assertNotNull(intentTable.get(messageProtection));
        assertNotNull(intentTable.get(confidentiality_transport));
        assertTrue(intentTable.get(confidentiality).getDescription().length() > 0 );
        
        assertNotNull(policySetTable.get(secureReliablePolicy));
        assertTrue(policySetTable.get(secureReliablePolicy).getProvidedIntents().size() == 2);
        assertTrue(policySetTable.get(secureReliablePolicy).getPolicies().size() == 2);
        
        assertNotNull(policySetTable.get(secureMessagingPolicies));
        assertEquals(policySetTable.get(secureMessagingPolicies).getMappedPolicies().size(), 3);
        //assertTrue(policySetTable.get(secureWsPolicy).getPolicies().get(0) instanceof org.apache.neethi.Policy);
        
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
        
        ModelResolver resolver = new DefaultModelResolver();
        policyDefinitionsProcessor.resolve(definitions, resolver);
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
