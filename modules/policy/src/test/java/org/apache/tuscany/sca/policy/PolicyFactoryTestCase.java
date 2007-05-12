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
package org.apache.tuscany.sca.policy;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Test building of policy model instances using the policy factory.
 * 
 * @version $Rev$ $Date$
 */
public class PolicyFactoryTestCase extends TestCase {

    PolicyFactory factory;

    public void setUp() throws Exception {
        factory = new DefaultPolicyFactory();
    }

    public void tearDown() throws Exception {
        factory = null;
    }

    public void testCreateIntent() {
        Intent intent = factory.createIntent();
        intent.setName(new QName("http://test", "reliability"));
        assertEquals(intent.getName(), new QName("http://test", "reliability"));
    }

    public void testCreatePolicySet() {
        PolicySet policySet = factory.createPolicySet();
        policySet.setName(new QName("http://test", "reliability"));
        assertEquals(policySet.getName(), new QName("http://test", "reliability"));
    }
}
