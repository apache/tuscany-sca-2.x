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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorFactoryImpl;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.apache.tuscany.sca.policy.QualifiedIntent;
import org.apache.tuscany.sca.policy.impl.BindingTypeImpl;
import org.apache.tuscany.sca.policy.impl.ImplementationTypeImpl;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev: 551296 $ $Date: 2007-06-28 01:18:35 +0530 (Thu, 28 Jun 2007) $
 */
public class ReadDocumentTestCase extends TestCase {

    private ModelResolver resolver;
    private StAXArtifactProcessor<Object> staxProcessor;
    private Monitor monitor;
    
    private static final QName elementToProcess = new QName("http://www.osoa.org/xmlns/sca/1.0", "implementationType");
        
    private Map<QName, Intent> intentTable = new Hashtable<QName, Intent>();
    private Map<QName, PolicySet> policySetTable = new Hashtable<QName, PolicySet>();
    private Map<QName, IntentAttachPointType> bindingTypesTable = new Hashtable<QName, IntentAttachPointType>();
    private Map<QName, IntentAttachPointType> implTypesTable = new Hashtable<QName, IntentAttachPointType>();
    private static final String scaNamespace = "http://www.osoa.org/xmlns/sca/1.0";
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
    

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        resolver = new DefaultModelResolver();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = new DefaultMonitorFactoryImpl();  
        if (monitorFactory != null) {
            monitor = monitorFactory.createMonitor();
            utilities.addUtility(monitorFactory);
        }
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, monitor);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());
               
        URL url = getClass().getResource("test_definitions.xml");
        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
        reader.next();
        
      //position on the right element qname to get processed
        while(reader.hasNext()) {
        	reader.next();
        	int event = reader.getEventType();
        	if(event == START_ELEMENT && reader.getName().equals(elementToProcess)) {
        		break;
        	}
        }
        while ( true ) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    Object artifact = staxProcessor.read(reader);
                    if ( artifact instanceof PolicySet ) {
                        PolicySet policySet = (PolicySet)artifact;
                        policySet.setName(new QName(namespace, policySet.getName().getLocalPart()));
                        policySetTable.put(policySet.getName(), policySet);
                    } else if ( artifact instanceof Intent ) {
                        Intent intent = (Intent)artifact;
                        intent.setName(new QName(namespace, intent.getName().getLocalPart()));
                        if ( intent instanceof QualifiedIntent ) {
                            ((QualifiedIntent)intent).getQualifiableIntent().
                                    setName(new QName(namespace, 
                                                      ((QualifiedIntent)intent).getQualifiableIntent().getName().getLocalPart()));
                        }
                        intentTable.put(intent.getName(), intent);
                    } else if ( artifact instanceof BindingTypeImpl ) {
                        IntentAttachPointType bindingType = (IntentAttachPointType)artifact;
                        bindingTypesTable.put(bindingType.getName(), bindingType);
                    } else if ( artifact instanceof ImplementationTypeImpl ) {
                        IntentAttachPointType implType = (IntentAttachPointType)artifact;
                        implTypesTable.put(implType.getName(), implType);
                    }
                    
                    if ( artifact != null ) {
                        resolver.addModel(artifact);
                    }
                    
                    break;
                }
            }
            if ( reader.hasNext() ) {
                reader.next();
            } else {
                break;
            }
        }
        urlStream.close();
    }

    @Override
    public void tearDown() throws Exception {
    }

    public void testReadSCADefinitions() throws Exception {
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
    
    public void testResolution() throws Exception {
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
        
        List<Intent> simpleIntents = new ArrayList<Intent>();
        List<ProfileIntent> profileIntents = new ArrayList<ProfileIntent>();
        List<QualifiedIntent> qualifiedIntents = new ArrayList<QualifiedIntent>();
        
        for (Intent intent : intentTable.values()) {
            if (intent instanceof ProfileIntent)
                profileIntents.add((ProfileIntent)intent);
            else if (intent instanceof QualifiedIntent)
                qualifiedIntents.add((QualifiedIntent)intent);
            else simpleIntents.add(intent);
        }
        
        for (Intent intent : simpleIntents)
            staxProcessor.resolve(intent, resolver);
        
        for (ProfileIntent intent : profileIntents)
            staxProcessor.resolve(intent, resolver);
        
        for (QualifiedIntent intent : qualifiedIntents)
            staxProcessor.resolve(intent, resolver);
        
        for ( PolicySet policySet : policySetTable.values() ) {
            if (policySet.getReferencedPolicySets().isEmpty())
                staxProcessor.resolve(policySet, resolver);
        }
        
        for ( PolicySet policySet : policySetTable.values() ) {
            if (!policySet.getReferencedPolicySets().isEmpty())
                staxProcessor.resolve(policySet, resolver);
        }
        
        for (  IntentAttachPointType bindingType : bindingTypesTable.values() ) {
            staxProcessor.resolve(bindingType, resolver);
        }
        
        for ( IntentAttachPointType implType : implTypesTable.values() ) {
            staxProcessor.resolve(implType, resolver);
        }
        
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
