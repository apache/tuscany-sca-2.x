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

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.annotation.Authentication;
import org.oasisopen.sca.annotation.Confidentiality;
import org.oasisopen.sca.annotation.PolicySets;
import org.oasisopen.sca.annotation.Requires;

/**
 * @version $Rev$ $Date$
 */
public class PolicyProcessorTestCase {
    private JavaInterfaceFactory factory;
    // private PolicyJavaInterfaceVisitor policyProcessor;

    @Test
    public void testInterfaceLevel() throws Exception {
        JavaInterface type = factory.createJavaInterface(Interface1.class);
        // policyProcessor.visitInterface(type);
        assertEquals(2, type.getRequiredIntents().size());
        assertEquals(1, type.getPolicySets().size());
    }

    @Test
    public void testMethodLevel() throws Exception {
        JavaInterface type = factory.createJavaInterface(Interface2.class);
        // policyProcessor.visitInterface(type);
        assertEquals(0, type.getRequiredIntents().size());
        assertEquals(3, type.getOperations().get(0).getRequiredIntents().size());
        assertEquals(1, type.getOperations().get(1).getRequiredIntents().size());
        assertEquals(0, type.getPolicySets().size());
        assertEquals(1, type.getOperations().get(0).getPolicySets().size());
        assertEquals(1, type.getOperations().get(1).getPolicySets().size());
    }

    @Test
    public void testInterfaceAndMethodLevel() throws Exception {
        JavaInterface type = factory.createJavaInterface(Interface3.class);
        // policyProcessor.visitInterface(type);
        assertEquals(2, type.getRequiredIntents().size());
        assertEquals(3, type.getOperations().get(0).getRequiredIntents().size());
        assertEquals(3, type.getOperations().get(1).getRequiredIntents().size());
        assertEquals(1, type.getPolicySets().size());
        assertEquals(2, type.getOperations().get(0).getPolicySets().size());
        assertEquals(2, type.getOperations().get(1).getPolicySets().size());
    }

    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        factory = new DefaultJavaInterfaceFactory(registry);
        // policyProcessor = new PolicyJavaInterfaceVisitor(registry);
    }

    // @Remotable
    @Requires( {"transaction.global"})
    @PolicySets( {"{http://ns1}PS1"})
    @Authentication
    private interface Interface1 {
        int method1();

        int method2();

        int method3();

        int method4();
    }

    private interface Interface2 {
        @Requires( {"transaction.global"})
        @Confidentiality({"message", "transport"})
        @PolicySets( {"{http://ns1}PS1"})
        int method1();

        @Requires( {"transaction.local"})
        @PolicySets( {"{http://ns1}PS2"})
        int method2();
    }

    @Requires( {"transaction.global.Interface6"})
    @PolicySets( {"{http://ns1}PS1"})
    @Authentication
    private interface Interface3 {
        @Requires( {"transaction.global.Interface6.method1"})
        @PolicySets( {"{http://ns1}PS2"})
        int method1();

        @Requires( {"transaction.local.Interface6.method2"})
        @PolicySets( {"{http://ns1}PS3"})
        int method2();
    }

}
