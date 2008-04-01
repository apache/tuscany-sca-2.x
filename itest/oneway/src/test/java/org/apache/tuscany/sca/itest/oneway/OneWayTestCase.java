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
    public void testOneWay() {
        OneWayClient client =
            domain.getService(OneWayClient.class, "OneWayClientComponent");
        try {

            int count = 100;

            for (int i = 0; i < 10; i++){
               // System.out.println("Test: doSomething " + count);
               // System.out.flush();
                client.doSomething(count);

                Thread.sleep(2000);

                System.out.println("Finished callCount = " + OneWayServiceImpl.callCount);

                Assert.assertEquals(OneWayClientImpl.callCount, OneWayServiceImpl.callCount);
            }
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.toString());
        }



    }

}
