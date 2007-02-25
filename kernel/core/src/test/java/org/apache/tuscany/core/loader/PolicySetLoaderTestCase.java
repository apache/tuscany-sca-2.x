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
package org.apache.tuscany.core.loader;

import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.model.IntentMap;
import org.apache.tuscany.spi.model.PolicySet;
import org.apache.tuscany.spi.model.Qualifier;

import junit.framework.TestCase;

public class PolicySetLoaderTestCase extends TestCase {
    private static final QName POLICYSET = new QName(SCA_NS, "policySet");

    public void testLoader() throws Exception {
        PolicySetLoader loader = new PolicySetLoader(null);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(this.getClass().getResourceAsStream("TestPolicy.scdl"));
        while (true) {
            int state = reader.next();
            if (START_ELEMENT == state && reader.getName().equals(POLICYSET)) {
                break;
            }
        }
        PolicySet policySet = loader.load(null, reader, null);
        assertNotNull(policySet);
        assertEquals(2, policySet.getAppliedArtifacts().size());
        assertTrue(policySet.getAppliedArtifacts().contains(new QName(SCA_NS, "binding.ws")));
        assertTrue(policySet.getAppliedArtifacts().contains(new QName(SCA_NS, "binding.jms")));
        Collection<IntentMap> intentMaps = policySet.getIntentMaps();
        assertEquals(1, intentMaps.size());
        IntentMap intentMap = intentMaps.iterator().next();
        assertEquals("transport", intentMap.getDefaultProvideIntent());
        assertTrue(intentMap.getProvideIntents().contains("sec.confidentiality"));
        Collection<Qualifier> qualifiers = intentMap.getQualifiers();
        assertEquals(2, qualifiers.size());
        Iterator qit = qualifiers.iterator();
        Qualifier qualifier1 = (Qualifier) qit.next();
        assertEquals(2, qualifier1.getWsPolicyAttachments().size());
        assertEquals("transport", qualifier1.getName());
        Qualifier qualifier2 = (Qualifier) qit.next();
        assertEquals("message", qualifier2.getName());
        IntentMap messageMap = qualifier2.getIntentMap();
        assertEquals(2, messageMap.getQualifiers().size());

    }
}
