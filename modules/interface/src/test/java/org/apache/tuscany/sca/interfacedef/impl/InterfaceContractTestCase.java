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

package org.apache.tuscany.sca.interfacedef.impl;


import junit.framework.Assert;

import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceContractTestCase {
    private InterfaceContract contract;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        contract = new MockInterfaceContract();
        Interface i1 = new MockInterface();
        contract.setInterface(i1);
        Operation op1 = new OperationImpl("op1");
        i1.getOperations().add(op1);
        Interface i2 = new MockInterface();
        contract.setCallbackInterface(i2);
        Operation callbackOp1 = new OperationImpl("callbackOp1");
        i2.getOperations().add(callbackOp1);
    }
    
    @Test
    public void testClone() throws Exception {
        InterfaceContract copy = (InterfaceContract) contract.clone();
        Assert.assertEquals(contract, copy);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    private static class MockInterfaceContract extends InterfaceContractImpl implements InterfaceContract {
    }

    private static class MockInterface extends InterfaceImpl implements Interface {
    }

}
