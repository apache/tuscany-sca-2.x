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

package org.apache.tuscany.sca.test.corba;

import static org.junit.Assert.fail;

import org.apache.tuscany.sca.host.corba.jse.tns.TnsDefaultCorbaHost;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.test.corba.types.ScenarioFive;
import org.apache.tuscany.sca.test.corba.types.ScenarioFiveComponent;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 * Tests usage of TNS JSE Corba host
 */
public class ScenarioFiveTestCase {

    @Test
    public void test_providedNameServer() {
        TestCorbaHost.setCorbaHost(new TnsDefaultCorbaHost());
        try {
            // just make sure we can obtain and use the reference with success
            SCADomain domain = SCADomain.newInstance("ScenarioFive.composite");
            ScenarioFive scenarioFive =
                domain.getService(ScenarioFiveComponent.class, "ScenarioFive").getScenarioFive();
            scenarioFive.doNothing();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
