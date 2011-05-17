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
package sample;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class HelloworldTestCase {

    static Node node;

    @Test
    public void testSayHello() throws NoSuchDomainException, NoSuchServiceException {
    	System.setProperty("domainURI", "uri:default?wka=127.0.0.1:7654");
        HelloworldSCAClient.main(new String[0]);
    }

    @BeforeClass
    public static void start() throws ContributionReadException, ActivationException, ValidationException {
        node = TuscanyRuntime.newInstance().createNode("uri:default?bind=127.0.0.1:7654");
        String curi = node.installContribution(null, "../helloworld/target/classes", null, null);
        node.startDeployables(curi);
    }

    @AfterClass
    public static void stop() {
        if (node != null) {
            node.stop();
        }
    }

}
