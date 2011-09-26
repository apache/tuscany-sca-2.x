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
package org.apache.tuscany.sca.impl;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.junit.Test;

/**
 * Tests that the contribution classloader is created after doing a validateContribution
 */
public class ContributionClassLoaderTestCase {

    @Test
    public void testInstallDeployable() throws ContributionReadException, ValidationException, ActivationException {
        Node node = TuscanyRuntime.newInstance().createNode();
        String curi = node.installContribution("src/test/resources/sample-helloworld.jar");
        Contribution contribution = node.getContribution(curi);
        Assert.assertNull(contribution.getClassLoader());
        node.validateContribution(curi);
        Assert.assertNotNull(contribution.getClassLoader());
    }
    
}
