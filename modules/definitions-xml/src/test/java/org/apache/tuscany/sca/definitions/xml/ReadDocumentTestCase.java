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

package org.apache.tuscany.sca.definitions.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.Qualifier;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class ReadDocumentTestCase {

    private static URLArtifactProcessor<Definitions> policyDefinitionsProcessor = null;
    private static Definitions definitions;
    private static Map<QName, Intent> intentTable = new Hashtable<QName, Intent>();
    private static Map<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
    private static Map<QName, BindingType> bindingTypesTable = new Hashtable<QName, BindingType>();
    private static Map<QName, ImplementationType> implTypesTable = new Hashtable<QName, ImplementationType>();
    public static final String scaNamespace = "http://docs.oasis-open.org/ns/opencsa/sca/200903";
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

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();

        // Create StAX processors
        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());

        URLArtifactProcessorExtensionPoint documentProcessors =
            extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        policyDefinitionsProcessor = documentProcessors.getProcessor(Definitions.class);

        URL url = ReadDocumentTestCase.class.getResource("test_definitions.xml");
        URI uri = URI.create("test_definitions.xml");
        definitions = policyDefinitionsProcessor.read(null, uri, url);

        for (Intent intent : definitions.getIntents()) {
            intentTable.put(intent.getName(), intent);
            for (Intent i : intent.getQualifiedIntents()) {
                intentTable.put(i.getName(), i);
            }
        }

        for (PolicySet policySet : definitions.getPolicySets()) {
            policySetTable.put(policySet.getName(), policySet);
        }

        for (BindingType bindingType : definitions.getBindingTypes()) {
            bindingTypesTable.put(bindingType.getType(), bindingType);
        }

        for (ImplementationType implType : definitions.getImplementationTypes()) {
            implTypesTable.put(implType.getType(), implType);
        }
    }

    @Test
    public void testReadSCADefinitions() throws Exception {
        assertNotNull(definitions);

        assertNotNull(intentTable.get(confidentiality));
        assertNotNull(intentTable.get(messageProtection));
        assertNotNull(intentTable.get(confidentiality_transport));
        assertTrue(intentTable.get(confidentiality).getDescription().length() > 0);

        assertNotNull(policySetTable.get(secureReliablePolicy));
        assertTrue(policySetTable.get(secureReliablePolicy).getProvidedIntents().size() == 2);
        assertTrue(policySetTable.get(secureReliablePolicy).getPolicies().size() == 2);

        assertNotNull(policySetTable.get(secureMessagingPolicies));
        assertEquals(2, policySetTable.get(secureMessagingPolicies).getIntentMaps().get(0).getQualifiers().size());
        //assertTrue(policySetTable.get(secureWsPolicy).getPolicies().get(0) instanceof org.apache.neethi.Policy);

        assertEquals(bindingTypesTable.size(), 1);
        assertNotNull(bindingTypesTable.get(wsBinding));
        assertEquals(implTypesTable.size(), 1);
        assertNotNull(implTypesTable.get(javaImpl));
    }

    private boolean isRealizedBy(PolicySet policySet, Intent intent) {
        if (intent.getName().getLocalPart().indexOf('.') == -1) {
            return policySet.getProvidedIntents().contains(intent);
        }
        for (IntentMap map : policySet.getIntentMaps()) {
            for (Qualifier q : map.getQualifiers()) {
                if (q.getIntent().equals(intent)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private int getNumberOfQualifiedPolicies(PolicySet policySet) {
        int count = 0;
        for(IntentMap intentMap: policySet.getIntentMaps()) {
            for(Qualifier q: intentMap.getQualifiers()) {
                count += q.getPolicies().size();
            }
        }
        return count;
    }

    @Test
    public void testResolveSCADefinitions() throws Exception {
        Intent i1 = intentTable.get(messageProtection);
        assertTrue(!i1.getRequiredIntents().isEmpty());
        assertNull(i1.getRequiredIntents().get(0).getDescription());

        QName confidentiality_transport = new QName(namespace, "confidentiality.transport");
        Intent i2 = intentTable.get(confidentiality_transport);
        assertNotNull(i2.getQualifiableIntent());

        PolicySet secureReliablePolicySet = policySetTable.get(secureReliablePolicy);
        PolicySet secureMessagingPolicySet = policySetTable.get(secureMessagingPolicies);
        PolicySet securityPolicySet = policySetTable.get(securityPolicy);

        assertEquals(secureReliablePolicySet.getProvidedIntents().get(1).getName(), integrity);
        assertNull(secureReliablePolicySet.getProvidedIntents().get(1).getDescription());
        assertTrue(secureMessagingPolicySet.isUnresolved());
        assertEquals(3, getNumberOfQualifiedPolicies(securityPolicySet));

        //testing to ensure that inclusion of referred policy sets has not happened
        PolicySet basicAuthMsgProtSecurityPolicySet = policySetTable.get(basicAuthMsgProtSecurity);
        assertTrue(basicAuthMsgProtSecurityPolicySet.getPolicies().isEmpty());
        assertTrue(basicAuthMsgProtSecurityPolicySet.getIntentMaps().isEmpty());

        BindingType wsBindingType = bindingTypesTable.get(wsBinding);
        assertNull(wsBindingType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNull(wsBindingType.getMayProvidedIntents().get(0).getDescription());

        ImplementationType javaImplType = implTypesTable.get(javaImpl);
        assertNull(javaImplType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNull(javaImplType.getMayProvidedIntents().get(0).getDescription());

        ModelResolver resolver = new DefaultModelResolver();
        policyDefinitionsProcessor.resolve(definitions, resolver);
        //builder.build(scaDefinitions);

        //testing if policy intents have been linked have property been linked up 
        assertNotNull(i1.getRequiredIntents().get(0).getDescription());
        // assertNotNull(i2.getQualifiableIntent().getDescription());
        assertEquals(secureReliablePolicySet.getProvidedIntents().get(1).getName(), integrity);
        assertNotNull(secureReliablePolicySet.getProvidedIntents().get(1).getDescription());

        //testing if policysets have been properly linked up with intents
        assertFalse(secureMessagingPolicySet.isUnresolved());
        assertTrue(isRealizedBy(secureMessagingPolicySet, intentTable.get(confidentiality)));
        assertTrue(isRealizedBy(secureMessagingPolicySet, intentTable.get(confidentiality_transport)));

        //testing if intent maps have been properly mapped to policies
        assertFalse(securityPolicySet.isUnresolved());
        assertTrue(isRealizedBy(securityPolicySet, intentTable.get(confidentiality)));
        assertTrue(isRealizedBy(securityPolicySet, intentTable.get(confidentiality_message)));

        //testing for inclusion of referred policysets
        assertFalse(basicAuthMsgProtSecurityPolicySet.getPolicies().isEmpty());
        assertFalse(basicAuthMsgProtSecurityPolicySet.getIntentMaps().get(0).getQualifiers().isEmpty());
        assertTrue(isRealizedBy(basicAuthMsgProtSecurityPolicySet, intentTable.get(confidentiality_transport)));

        assertNotNull(wsBindingType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNotNull(wsBindingType.getMayProvidedIntents().get(0).getDescription());

        assertNotNull(javaImplType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNotNull(javaImplType.getMayProvidedIntents().get(0).getDescription());
    }
}
