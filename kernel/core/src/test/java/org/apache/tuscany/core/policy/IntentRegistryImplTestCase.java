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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.model.Intent;
import org.apache.tuscany.spi.model.IntentName;
import org.apache.tuscany.spi.policy.IntentRegistry;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class IntentRegistryImplTestCase extends TestCase {
    private static final QName WS_BINDING = new QName(SCA_NS, "binding.ws");
    private static final QName JMS_BINDING = new QName(SCA_NS, "binding.jms");
    private IntentRegistry intentReg;

    @Override
    protected void setUp() throws Exception {
        intentReg = new IntentRegistryImpl();

        Intent bodyintent = new Intent(new IntentName("sec.confidentiality/message/body"), "test");
        bodyintent.addAppliedArtifacts(WS_BINDING);
        bodyintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(bodyintent);

        Intent headintent = new Intent(new IntentName("sec.confidentiality/message/head"), "test");
        headintent.addAppliedArtifacts(WS_BINDING);
        headintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(headintent);

        Intent confidentialityintent = new Intent(new IntentName("sec.confidentiality"), "test");
        confidentialityintent.addAppliedArtifacts(WS_BINDING);
        confidentialityintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(confidentialityintent);

        Intent messageintent = new Intent(new IntentName("sec.confidentiality/message"), null);
        messageintent.addAppliedArtifacts(WS_BINDING);
        messageintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(messageintent);
    }

    @Override
    protected void tearDown() throws Exception {
        intentReg = null;
    }

//    public void testGetQualifiedIntent() {
//        List<IntentName> intentNameList = new ArrayList<IntentName>();
//        intentReg.get
//        //intentReg.getConcretIntents(intentNameList, artifact)
//    }

    public void testGetConcretIntents() {
        Intent messageintent = new Intent(new IntentName("sec.confidentiality/transport"), null);
        messageintent.addAppliedArtifacts(WS_BINDING);
        messageintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(messageintent);

        Intent allintent = new Intent(new IntentName("sec.confidentiality/all"), null);
        allintent.addAppliedArtifacts(WS_BINDING);
        allintent.addRequriedIntents(new IntentName("sec.confidentiality/message"));
        allintent.addRequriedIntents(new IntentName("sec.confidentiality/transport"));


        intentReg.register(allintent);
        List<IntentName> intents = new ArrayList<IntentName>();
        intents.add(new IntentName("sec.confidentiality/all"));
        Collection<IntentName> concreteIntents = intentReg.inlineProfileIntent(intents, WS_BINDING);
        assertEquals(2, concreteIntents.size());
        assertTrue(concreteIntents.contains(new IntentName("sec.confidentiality/message")));
        assertTrue(concreteIntents.contains(new IntentName("sec.confidentiality/transport")));
        //fail("Not yet implemented");
    }

    public void testGetQualifiedIntents() {
        IntentName message = new IntentName("sec.confidentiality/message");
        Collection<IntentName> qualifiedIntents = intentReg.getQualifiedIntents(message, JMS_BINDING);
        assertEquals(2, qualifiedIntents.size());
        assertTrue(qualifiedIntents.contains(new IntentName("sec.confidentiality/message/body")));
        assertTrue(qualifiedIntents.contains(new IntentName("sec.confidentiality/message/head")));
        assertFalse(qualifiedIntents.contains(new IntentName("sec.confidentiality/message")));
        assertFalse(qualifiedIntents.contains(new IntentName("sec.confidentiality")));
    }

    public void testIsApplicable() {
        assertTrue(intentReg.isApplicable(new IntentName("sec.confidentiality/message"), WS_BINDING));
        assertFalse(intentReg.isApplicable(new IntentName("sec.confidentiality/transport"), WS_BINDING));
        assertFalse(intentReg.isApplicable(new IntentName("test.confidentiality/transport"), WS_BINDING));
    }

    public void testRegister() {
        Intent messageintent = new Intent(new IntentName("sec.confidentiality/transport"), null);
        messageintent.addAppliedArtifacts(WS_BINDING);
        messageintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(messageintent);
        assertTrue(intentReg.isApplicable(new IntentName("sec.confidentiality/transport"), WS_BINDING));
        assertTrue(intentReg.isApplicable(new IntentName("sec.confidentiality/transport"), JMS_BINDING));

    }

    public void testIsQualifiedIntent() {
        Intent messageintent = new Intent(new IntentName("sec.confidentiality/transport"), null);
        messageintent.addAppliedArtifacts(WS_BINDING);
        messageintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(messageintent);
        Intent allintent = new Intent(new IntentName("sec.confidentiality/all"), null);
        allintent.addAppliedArtifacts(WS_BINDING);

        assertTrue(intentReg.isQualifiedIntent(new IntentName("sec.confidentiality/transport")));
        assertTrue(intentReg.isQualifiedIntent(new IntentName("sec.confidentiality/message/body")));
        assertTrue(intentReg.isQualifiedIntent(new IntentName("sec.confidentiality/message/body")));
        assertFalse(intentReg.isQualifiedIntent(new IntentName("sec.confidentiality/message")));
        assertFalse(intentReg.isQualifiedIntent(new IntentName("sec.confidentiality")));
    }

    public void testUnRegister() {
        Intent messageintent = new Intent(new IntentName("sec.confidentiality/transport"), null);
        messageintent.addAppliedArtifacts(WS_BINDING);
        messageintent.addAppliedArtifacts(JMS_BINDING);
        intentReg.register(messageintent);
        intentReg.unRegister(messageintent);
        assertFalse(intentReg.isApplicable(new IntentName("sec.confidentiality/transport"), WS_BINDING));
        assertFalse(intentReg.isApplicable(new IntentName("sec.confidentiality/transport"), JMS_BINDING));

    }

}
