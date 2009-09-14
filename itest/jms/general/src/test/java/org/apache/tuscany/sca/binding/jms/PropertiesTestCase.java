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
package org.apache.tuscany.sca.binding.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.jms.DeliveryMode;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class PropertiesTestCase {

    private static SCADomain scaDomain;

    @Before
    public void init() {
        scaDomain = SCADomain.newInstance("http://localhost", "/", "properties/properties.composite");
    }

    @Test
    public void testProps1() throws Exception {
        JMSClient client = scaDomain.getService(JMSClient.class, "ClientComponent");

        client.aClientMethod();

        // wait for up to 5 seconds but should wake up as soon as done
        synchronized(MsgServiceImpl.lock) {
            if (MsgServiceImpl.msg == null) {
                MsgServiceImpl.lock.wait(5000);
            }
        }
        assertNotNull(MsgServiceImpl.msg);

        assertEquals("myType", MsgServiceImpl.msg.getJMSType());
        assertEquals("xyz", MsgServiceImpl.msg.getJMSCorrelationID());
        assertEquals(DeliveryMode.PERSISTENT, MsgServiceImpl.msg.getJMSDeliveryMode());
        // assertEquals(3, MsgServiceImpl.msg.getJMSPriority()); // Doesn't seem to work with ActiveMQ
        assertEquals("myHeadP1", MsgServiceImpl.msg.getStringProperty("headP1"));
    }

    @Test
    public void testOp2() throws Exception {
        MsgClient client = scaDomain.getService(MsgClient.class, "ClientComponent");

        client.op2();

        // wait for up to 5 seconds but should wake up as soon as done
        synchronized(MsgServiceImpl.lock) {
            if (MsgServiceImpl.msg == null) {
                MsgServiceImpl.lock.wait(6000);
            }
        }
        assertNotNull(MsgServiceImpl.msg);

        assertEquals("op2Type", MsgServiceImpl.msg.getJMSType());
        assertEquals("op2CID", MsgServiceImpl.msg.getJMSCorrelationID());
        // assertEquals(DeliveryMode.NON_PERSISTENT, MsgServiceImpl.msg.getJMSDeliveryMode()); // Doesn't seem to work with ActiveMQ
        // assertEquals(3, MsgServiceImpl.msg.getJMSPriority()); // Doesn't seem to work with ActiveMQ
        assertEquals("myHeadP1", MsgServiceImpl.msg.getStringProperty("headP1"));
        assertEquals("foo", MsgServiceImpl.msg.getStringProperty("op2P2"));
        //operation properties are a service side thing
        //assertEquals("nativeOp2", MsgServiceImpl.msg.getStringProperty("scaOperationName"));
    }

    @After
    public void end() {
        if (scaDomain != null) {
            scaDomain.close();
        }
    }
}
