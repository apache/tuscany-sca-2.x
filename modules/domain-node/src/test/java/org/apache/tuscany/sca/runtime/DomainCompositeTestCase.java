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

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

public class DomainCompositeTestCase {

    @Test
    public void localOnlyDomain() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("DomainCompositeTestCase");
        testIt(node);
    }

    @Test
    public void distributedDomain() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException {
        Node node = TuscanyRuntime.newInstance().createNode("uri:DomainCompositeTestCase");
        testIt(node);
    }

    private void testIt(Node node) throws ContributionReadException, ActivationException, ValidationException {
        node.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null);
        node.startComposite("helloworld", "helloworld.composite");
        
        Composite dc = node.getDomainComposite();
        Assert.assertEquals("DomainCompositeTestCase", dc.getName().getLocalPart());
        Assert.assertEquals(1, dc.getIncludes().size());
        Composite c = dc.getIncludes().get(0);
        Assert.assertEquals("helloworld", c.getName().getLocalPart());
        
//        String s = node.getDomainLevelCompositeAsString();
//        System.out.println(s);
//        Assert.assertTrue(s.contains(":helloworld\""));

        node.stopComposite("helloworld", "helloworld.composite");
        Assert.assertEquals(0, node.getDomainComposite().getIncludes().size());
    }

}
