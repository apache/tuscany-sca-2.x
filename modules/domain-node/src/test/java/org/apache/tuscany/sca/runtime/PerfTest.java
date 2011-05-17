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
package org.apache.tuscany.sca.runtime;

import java.util.Properties;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

import sample.Helloworld;

public class PerfTest {

    @Test
    public void testStopStart() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Properties config = new Properties();
        config.setProperty(RuntimeProperties.QUIET_LOGGING, "true");
        Node node = TuscanyRuntime.newInstance(config).createNode();
        node.installContribution(null, "src/test/resources/sample-helloworld.jar", null, null);
        
        validate(node);

        int count = 3000;
        long start = System.currentTimeMillis();
        for (int i=0; i<count; i++) {
            node.startComposite("sample-helloworld", "helloworld.composite");
            node.stopComposite("sample-helloworld", "helloworld.composite");
        }
        long total = System.currentTimeMillis() - start;
        System.out.println(count + " = " + total + " = " + total / (double)count);

        // test it still works
        validate(node);
    }

    private void validate(Node node) throws ActivationException, ValidationException, NoSuchServiceException, ContributionReadException {
        node.startComposite("sample-helloworld", "helloworld.composite");
        
        Helloworld helloworldService = node.getService(Helloworld.class, "HelloworldComponent");
        Assert.assertEquals("Hello petra", helloworldService.sayHello("petra"));
        
        node.stopComposite("sample-helloworld", "helloworld.composite");
        try {
            node.getService(Helloworld.class, "HelloworldComponent");
            Assert.fail();
        } catch (NoSuchServiceException e) {
            // expected as there is no deployables
        }
    }

}
