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
package org.apache.tuscany.sca.interfacedef.java.introspection.impl;

import junit.framework.TestCase;

import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.impl.PolicyJavaInterfaceVisitor;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.osoa.sca.annotations.PolicySets;
import org.osoa.sca.annotations.Requires;

/**
 * @version $Rev$ $Date$
 */
public class PolicyProcessorTestCase extends TestCase {
    private JavaInterfaceFactory factory = new DefaultJavaInterfaceFactory();
    private PolicyJavaInterfaceVisitor policyProcessor;

    public void testInterfaceLevel() throws Exception {
        JavaInterface type = factory.createJavaInterface(Interface1.class);
        policyProcessor.visitInterface(type);
        assertEquals(1, type.getRequiredIntents().size());
        assertEquals(1, type.getPolicySets().size());
    }

    public void testMethodLevel() throws Exception {
        JavaInterface type = factory.createJavaInterface(Interface2.class);
        policyProcessor.visitInterface(type);
        assertEquals(0, type.getRequiredIntents().size());
        assertEquals(1, type.getOperations().get(0).getRequiredIntents().size());
        assertEquals(1, type.getOperations().get(1).getRequiredIntents().size());
        assertEquals(0, type.getPolicySets().size());
        assertEquals(1, type.getOperations().get(0).getPolicySets().size());
        assertEquals(1, type.getOperations().get(1).getPolicySets().size());
    }

    public void testInterfaceAndMethodLevel() throws Exception {
        JavaInterface type = factory.createJavaInterface(Interface3.class);
        policyProcessor.visitInterface(type);
        assertEquals(1, type.getRequiredIntents().size());
        assertEquals(1, type.getOperations().get(0).getRequiredIntents().size());
        assertEquals(1, type.getOperations().get(1).getRequiredIntents().size());
        assertEquals(1, type.getPolicySets().size());
        assertEquals(1, type.getOperations().get(0).getPolicySets().size());
        assertEquals(1, type.getOperations().get(1).getPolicySets().size());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        policyProcessor = new PolicyJavaInterfaceVisitor(new DefaultPolicyFactory());
    }

    // @Remotable
    @Requires( {"transaction.global"})
    @PolicySets( {"{http://ns1}PS1"})
    private interface Interface1 {
        int method1();

        int method2();

        int method3();

        int method4();
    }

    private interface Interface2 {
        @Requires( {"transaction.global"})
        @PolicySets( {"{http://ns1}PS1"})
        int method1();

        @Requires( {"transaction.local"})
        @PolicySets( {"{http://ns1}PS2"})
        int method2();
    }

    @Requires( {"transaction.global.Interface6"})
    @PolicySets( {"{http://ns1}PS1"})
    private interface Interface3 {
        @Requires( {"transaction.global.Interface6.method1"})
        @PolicySets( {"{http://ns1}PS2"})
        int method1();

        @Requires( {"transaction.local.Interface6.method2"})
        @PolicySets( {"{http://ns1}PS3"})
        int method2();
    }

}
