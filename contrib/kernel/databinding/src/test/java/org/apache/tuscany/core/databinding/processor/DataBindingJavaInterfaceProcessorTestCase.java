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

import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.api.annotation.DataType;
import org.apache.tuscany.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.databinding.impl.DataBindingRegistryImpl;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
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
    public final void testVisitInterface() throws InvalidServiceContractException {
        DataBindingRegistry registry = new DataBindingRegistryImpl();
        DataBindingJavaInterfaceProcessor processor = new DataBindingJavaInterfaceProcessor(registry);
        JavaServiceContract<?> contract = new JavaServiceContract(MockInterface.class);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        Operation<Type> operation = new Operation<Type>("call", null, null, null, false, null, NO_CONVERSATION);
        Operation<Type> operation1 = new Operation<Type>("call1", null, null, null, false, null, NO_CONVERSATION);
        operations.put("call", operation);
        operations.put("call1", operation1);
        contract.setOperations(operations);
        contract.setRemotable(true);
        processor.visitInterface(MockInterface.class, null, contract);
        Assert.assertEquals("org.w3c.dom.Node", contract.getDataBinding());
        Assert.assertEquals("org.w3c.dom.Node", contract.getOperations().get("call").getDataBinding());
        Assert.assertEquals("xml:string", contract.getOperations().get("call1").getDataBinding());
    }

    @DataType(name = "org.w3c.dom.Node")
    @Remotable
    public static interface MockInterface {
        Node call(Node msg);

        @DataType(name = "xml:string")
        String call1(String msg);
    }

}
