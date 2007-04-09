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
package org.apache.tuscany.spi.model;

import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ServiceContractTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testAddOperation() throws Exception {
        ServiceContract<Type> contract = new TestContract();
        Operation<Type> operation = new Operation<Type>("foo", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> ops = new HashMap<String, Operation<Type>>();
        ops.put("foo", operation);
        contract.setOperations(ops);
        assertEquals(contract, operation.getServiceContract());
        assertFalse(operation.isCallback());
    }

    public void testAddCallbackOperation() throws Exception {
        ServiceContract<Type> contract = new TestContract();
        Operation<Type> operation = new Operation<Type>("foo", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> ops = new HashMap<String, Operation<Type>>();
        ops.put("foo", operation);
        contract.setCallbackOperations(ops);
        assertEquals(contract, operation.getServiceContract());
        assertTrue(operation.isCallback());
    }
    
    @SuppressWarnings("unchecked")
    public void testClone() throws Exception {
        ServiceContract<Type> contract = new TestContract();
        Operation<Type> operation = new Operation<Type>("foo", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> ops = new HashMap<String, Operation<Type>>();
        ops.put("foo", operation);
        contract.setOperations(ops);        

        operation = new Operation<Type>("bar", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> callbackOps = new HashMap<String, Operation<Type>>();
        ops.put("bar", operation);
        contract.setCallbackOperations(callbackOps);
        
        ServiceContract<Type> copy = (ServiceContract<Type>) contract.clone();
        assertEquals(contract, copy);
    }


    private class TestContract extends ServiceContract<Type> {

    }
}
