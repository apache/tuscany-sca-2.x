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

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.Qualifier;
import org.junit.Before;
import org.junit.Test;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class ReadDocumentTestCase {

    private ModelResolver resolver;
    private StAXArtifactProcessor<Object> staxProcessor;
    private ProcessorContext context;
    
    private static final QName elementToProcess =
        new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "implementationType");

    private Map<QName, Intent> intentTable = new Hashtable<QName, Intent>();
    private Map<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
    private Map<QName, BindingType> bindingTypesTable = new Hashtable<QName, BindingType>();
    private Map<QName, ImplementationType> implTypesTable = new Hashtable<QName, ImplementationType>();
    private static final String scaNamespace = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    private static final String namespace = "http://test";

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

    @Before
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        resolver = new DefaultModelResolver();
        context = new ProcessorContext(extensionPoints);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());

        URL url = getClass().getResource("test_definitions.xml");
        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
        reader.next();

        //position on the right element qname to get processed
        while (reader.hasNext()) {
            reader.next();
            int event = reader.getEventType();
            if (event == START_ELEMENT && reader.getName().equals(elementToProcess)) {
                break;
            }
        }
        while (true) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    Object artifact = staxProcessor.read(reader, context);
                    if (artifact instanceof PolicySet) {
                        PolicySet policySet = (PolicySet)artifact;
                        policySet.setName(new QName(namespace, policySet.getName().getLocalPart()));
                        policySetTable.put(policySet.getName(), policySet);
                    } else if (artifact instanceof Intent) {
                        Intent intent = (Intent)artifact;
                        intent.setName(new QName(namespace, intent.getName().getLocalPart()));
                        intentTable.put(intent.getName(), intent);
                        for (Intent i : intent.getQualifiedIntents()) {
                            i.setName(new QName(namespace, i.getName().getLocalPart()));
                            intentTable.put(i.getName(), i);
                            resolver.addModel(i, context);
                        }
                    } else if (artifact instanceof BindingType) {
                        BindingType bindingType = (BindingType)artifact;
                        bindingTypesTable.put(bindingType.getType(), bindingType);
                    } else if (artifact instanceof ImplementationType) {
                        ImplementationType implType = (ImplementationType)artifact;
                        implTypesTable.put(implType.getType(), implType);
                    }

                    if (artifact != null) {
                        resolver.addModel(artifact, context);
                    }

                    break;
                }
            }
            if (reader.hasNext()) {
                reader.next();
            } else {
                break;
            }
        }
        urlStream.close();
    }

    @Test
    public void testReadSCADefinitions() throws Exception {
        assertNotNull(intentTable.get(confidentiality));
        assertNotNull(intentTable.get(messageProtection));
        assertNotNull(intentTable.get(confidentiality_transport));
        assertTrue(intentTable.get(confidentiality).getDescription().length() > 0);

        assertNotNull(policySetTable.get(secureReliablePolicy));
        assertTrue(policySetTable.get(secureReliablePolicy).getProvidedIntents().size() == 2);
        assertTrue(policySetTable.get(secureReliablePolicy).getPolicies().size() == 2);

        assertNotNull(policySetTable.get(secureMessagingPolicies));
        assertEquals(2, policySetTable.get(secureMessagingPolicies).getIntentMaps().get(0).getQualifiers().get(0).getPolicies().size());

        assertEquals(bindingTypesTable.size(), 1);
        assertNotNull(bindingTypesTable.get(wsBinding));
        assertEquals(implTypesTable.size(), 1);
        assertNotNull(implTypesTable.get(javaImpl));
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
    public void testResolution() throws Exception {
        assertTrue(!intentTable.get(messageProtection).getRequiredIntents().isEmpty());
        Intent profileIntent = intentTable.get(new QName(namespace, "messageProtection"));
        assertNull(profileIntent.getRequiredIntents().get(0).getDescription());

        QName confidentiality_transport = new QName(namespace, "confidentiality.transport");
        assertTrue(intentTable.get(confidentiality_transport) instanceof Intent);
        Intent qualifiedIntent = (Intent)intentTable.get(new QName(namespace, "confidentiality.transport"));
        assertNull(qualifiedIntent.getDescription());
        assertNotNull(qualifiedIntent.getQualifiableIntent().getDescription());

        PolicySet secureReliablePolicySet = policySetTable.get(secureReliablePolicy);
        PolicySet secureMessagingPolicySet = policySetTable.get(secureMessagingPolicies);
        PolicySet securityPolicySet = policySetTable.get(securityPolicy);

        assertEquals(secureReliablePolicySet.getProvidedIntents().get(1).getName(), integrity);
        assertNull(secureReliablePolicySet.getProvidedIntents().get(1).getDescription());
        assertTrue(secureMessagingPolicySet.isUnresolved());
        assertEquals(2, getNumberOfQualifiedPolicies(securityPolicySet));

        //testing to ensure that inclusion of referred policy sets has not happened
        PolicySet basicAuthMsgProtSecurityPolicySet = policySetTable.get(basicAuthMsgProtSecurity);
        assertTrue(basicAuthMsgProtSecurityPolicySet.getPolicies().isEmpty());
        assertTrue(basicAuthMsgProtSecurityPolicySet.getIntentMaps().isEmpty());

        ExtensionType wsBindingType = bindingTypesTable.get(wsBinding);
        assertNull(wsBindingType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNull(wsBindingType.getMayProvidedIntents().get(0).getDescription());

        ExtensionType javaImplType = implTypesTable.get(javaImpl);
        assertNull(javaImplType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNull(javaImplType.getMayProvidedIntents().get(0).getDescription());

        List<Intent> intents = new ArrayList<Intent>(intentTable.values());

        for (Intent intent : intents) {
            staxProcessor.resolve(intent, resolver, context);
        }

        for (PolicySet policySet : policySetTable.values()) {
            if (policySet.getReferencedPolicySets().isEmpty())
                staxProcessor.resolve(policySet, resolver, context);
        }

        for (PolicySet policySet : policySetTable.values()) {
            if (!policySet.getReferencedPolicySets().isEmpty())
                staxProcessor.resolve(policySet, resolver, context);
        }

        for (ExtensionType bindingType : bindingTypesTable.values()) {
            staxProcessor.resolve(bindingType, resolver, context);
        }

        for (ExtensionType implType : implTypesTable.values()) {
            staxProcessor.resolve(implType, resolver, context);
        }

        //testing if policy intents have been linked have property been linked up 
        assertNotNull(profileIntent.getRequiredIntents().get(0).getDescription());
        assertNotNull(qualifiedIntent.getQualifiableIntent().getDescription());
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
        assertFalse(basicAuthMsgProtSecurityPolicySet.getIntentMaps().isEmpty());
        assertTrue(isRealizedBy(basicAuthMsgProtSecurityPolicySet, intentTable.get(confidentiality_transport)));

        assertNotNull(wsBindingType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNotNull(wsBindingType.getMayProvidedIntents().get(0).getDescription());

        assertNotNull(javaImplType.getAlwaysProvidedIntents().get(0).getDescription());
        assertNotNull(javaImplType.getMayProvidedIntents().get(0).getDescription());
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
}
