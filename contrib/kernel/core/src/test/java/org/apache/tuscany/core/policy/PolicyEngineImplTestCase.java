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
package org.apache.tuscany.core.policy;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.model.Intent;
import org.apache.tuscany.spi.model.IntentName;
import org.apache.tuscany.spi.model.PolicyModel;
import org.apache.tuscany.spi.model.PolicySet;
import org.apache.tuscany.spi.policy.IntentRegistry;
import org.apache.tuscany.spi.policy.PolicyEngine;
import org.apache.tuscany.spi.policy.PolicySetContainer;
import org.apache.tuscany.spi.policy.SCATypeManager;

import junit.framework.TestCase;
import org.apache.tuscany.core.loader.PolicySetLoader;

public class PolicyEngineImplTestCase extends TestCase {
    private static final QName POLICYSET = new QName(SCA_NS, "policySet");
    private static final QName WS_BINDING = new QName(SCA_NS, "binding.ws");
    private IntentRegistry intentReg;
    private PolicyEngine policyEngine;


    public void testgetPolicy() throws Exception {
        Collection<PolicyModel> policies =
            policyEngine.getPolicy(new IntentName[]{new IntentName("sec.authentication/cert")}, null, WS_BINDING);
        assertEquals(2, policies.size());
        policies =
            policyEngine.getPolicy(new IntentName[]{new IntentName("sec.authentication/basic")}, null, WS_BINDING);
        assertEquals(1, policies.size());

        //test for unqualified intent with default value on intentMap
        policies = policyEngine.getPolicy(new IntentName[]{new IntentName("sec.authentication")}, null, WS_BINDING);
        assertEquals(2, policies.size());
    }

    @Override
    protected void setUp() throws Exception {
        PolicySetLoader loader = new PolicySetLoader(null);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PolicySet.scdl");
        XMLStreamReader reader = factory.createXMLStreamReader(resourceAsStream);
        PolicySetContainerImpl psc = new PolicySetContainerImpl();
        while (true) {
            int state = reader.next();
            if (state == XMLStreamConstants.END_DOCUMENT) {
                break;
            }
            if (XMLStreamConstants.START_ELEMENT == state && reader.getName().equals(POLICYSET)) {
                psc.addPolicySet(loader.load(null, reader, null));
            }

        }
        resourceAsStream.close();
        intentReg = new IntentRegistryImpl();
        policyEngine = new PolicyEngineImpl(intentReg, psc, new SCATypeManagerImpl());

        Intent bodyintent = new Intent(new IntentName("sec.confidentiality/message/body"), "test");
        bodyintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(bodyintent);

        Intent allintent = new Intent(new IntentName("sec.confidentiality/message/all"), "test");
        allintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(allintent);

        Intent confidentialityintent = new Intent(new IntentName("sec.confidentiality"), "test");
        confidentialityintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(confidentialityintent);

        Intent messageintent = new Intent(new IntentName("sec.confidentiality/message"), null);
        messageintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(messageintent);

        Intent authintent = new Intent(new IntentName("sec.authentication"), null);
        authintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(authintent);

        Intent certintent = new Intent(new IntentName("sec.authentication/cert"), null);
        certintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(certintent);

        Intent basicintent = new Intent(new IntentName("sec.authentication/basic"), null);
        basicintent.addAppliedArtifacts(WS_BINDING);
        intentReg.register(basicintent);

    }

    private class PolicySetContainerImpl implements PolicySetContainer {

        private Map<QName, PolicySet> sets = new HashMap<QName, PolicySet>();

        public Collection<PolicySet> getAllPolicySet() {
            return sets.values();
        }

        public PolicySet getPolicySet(QName name) {
            return sets.get(name);
        }

        public void addPolicySet(PolicySet pset) {
            sets.put(pset.getName(), pset);
        }

    }

    private class SCATypeManagerImpl implements SCATypeManager {

        public boolean isTypeOf(QName subType, QName type) {
            return subType.equals(type);
        }

    }
}
