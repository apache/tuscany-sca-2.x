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
package org.apache.tuscany.sca.interfacedef.java.impl;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.junit.Test;
import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.annotation.AsyncInvocation;
import org.oasisopen.sca.annotation.Remotable;

/**
 * This test case will test that a Component that has multiple Remotable interfaces
 * that contain methods with the same name will correctly select the right method.
 * 
 * @version $Rev: 826368 $ $Date: 2009-10-18 08:22:23 +0100 (Sun, 18 Oct 2009) $
 */
public class AsyncServiceIntefaceTestCase {

    /**
     * Test case that validates that a @Remotable interface with Overloaded operations
     * is detected.
     * 
     * This test case is for TUSCANY-2194
     * @throws InvalidInterfaceException 
     */
    @Test
    public void testAsyncIntrospection() throws InvalidInterfaceException
    {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory(registry);
        JavaInterfaceIntrospectorImpl introspector = new JavaInterfaceIntrospectorImpl(javaFactory);
        JavaInterfaceImpl javaInterface = new JavaInterfaceImpl();

        introspector.introspectInterface(javaInterface, AsyncServiceInterface.class);
        
        assertEquals(1, javaInterface.getOperations().size());
        assertEquals("anOperation",javaInterface.getOperations().get(0).getName());
        assertEquals(1, javaInterface.getOperations().get(0).getInputType().getLogical().size());
        assertEquals(String.class,javaInterface.getOperations().get(0).getInputType().getLogical().get(0).getGenericType());
        assertEquals(String.class,javaInterface.getOperations().get(0).getOutputType().getLogical().get(0).getGenericType());
    }

    @Remotable
    @AsyncInvocation
    private interface AsyncServiceInterface {
        void anOperationAsync(String s, ResponseDispatch<String> rd);
    }
}
