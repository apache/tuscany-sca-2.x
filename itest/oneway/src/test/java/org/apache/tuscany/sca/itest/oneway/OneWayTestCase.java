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

package org.apache.tuscany.sca.itest.oneway;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.itest.oneway.impl.OneWayClientImpl;
import org.apache.tuscany.sca.itest.oneway.impl.OneWayServiceImpl;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OneWayTestCase {
    /**
     * Maximum period of time that we are prepared to wait for all the @OneWay
     * method calls to complete in milliseconds.
     */
    private static final int MAX_SLEEP_TIME = 10000;

    private SCADomain domain;

    @Before
    public void setUp() throws Exception {
        SCANode node = SCANodeFactory.newInstance().createSCANode(null, null);
        node.addContribution("mycontribution",
                             OneWayTestCase.class.getClassLoader().getResource("OneWayContribution/"));
        node.addToDomainLevelComposite(new QName("http://oneway", "OneWayITest"));
        node.start();
        domain = node.getDomain();
    }

    @After
    public void tearDown() throws Exception {
        if (domain != null) {
            domain.destroy();
        }
    }

    @Test
    public void testOneWay() throws Exception {
        OneWayClient client =
            domain.getService(OneWayClient.class, "OneWayClientComponent");
        try {

            int count = 100;

            for (int i = 0; i < 10; i++){
               // System.out.println("Test: doSomething " + count);
               // System.out.flush();
                client.doSomething(count);

                // TUSCANY-2192 - We need to sleep to allow the @OneWay method calls to complete.
                // Note: This can take different periods depending on the speed and load
                // on the computer where the test is being run.
                // This loop will wait for the required number of @OneWay method calls to
                // have taken place or MAX_SLEEP_TIME to have passed.
                long startSleep = System.currentTimeMillis();
                while (OneWayClientImpl.callCount != OneWayServiceImpl.callCount 
                        && System.currentTimeMillis() - startSleep < MAX_SLEEP_TIME) {
                    Thread.sleep(100);
                    // System.out.println("" + OneWayClientImpl.callCount + "," + OneWayServiceImpl.callCount);
                }

                System.out.println("Finished callCount = " + OneWayServiceImpl.callCount);

                Assert.assertEquals(OneWayClientImpl.callCount, OneWayServiceImpl.callCount);
            }
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.toString());
            ex.printStackTrace();
            throw ex;
        }
    }

}
