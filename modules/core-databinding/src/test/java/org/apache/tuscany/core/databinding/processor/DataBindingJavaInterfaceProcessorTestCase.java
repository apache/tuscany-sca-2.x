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

package org.apache.tuscany.core.databinding.processor;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.databinding.annotation.DataBinding;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.osoa.sca.annotations.Remotable;
import org.w3c.dom.Node;

/**
 * 
 */
public class DataBindingJavaInterfaceProcessorTestCase extends TestCase {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws InvalidServiceContractException
     */
    public final void testVisitInterface() throws InvalidInterfaceException {
        DataBindingExtensionPoint registry = new DefaultDataBindingExtensionPoint();
        DataBindingJavaInterfaceProcessor processor = new DataBindingJavaInterfaceProcessor(registry);
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        
        JavaInterface contract = javaFactory.createJavaInterface();
        contract.setJavaClass(MockInterface.class);
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(contract);
        Operation operation = new OperationImpl("call");
        Operation operation1 = new OperationImpl("call1");
        contract.getOperations().add(operation);
        contract.getOperations().add(operation1);
        contract.setRemotable(true);
        processor.visitInterface(contract);
        // Assert.assertEquals("org.w3c.dom.Node", contract.getDataBinding());
        // Assert.assertEquals("org.w3c.dom.Node",
        // contract.getOperations().get("call").getDataBinding());
        // Assert.assertEquals("xml:string",
        // contract.getOperations().get("call1").getDataBinding());
    }

    @DataBinding("org.w3c.dom.Node")
    @Remotable
    public static interface MockInterface {
        Node call(Node msg);

        @DataBinding("xml:string")
        String call1(String msg);
    }

}
