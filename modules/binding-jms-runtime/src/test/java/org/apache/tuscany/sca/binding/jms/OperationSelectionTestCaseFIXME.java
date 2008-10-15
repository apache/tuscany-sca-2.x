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
package org.apache.tuscany.sca.binding.jms;

import java.util.ArrayList;
import java.util.List;

import javax.jms.TextMessage;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.provider.JMSBindingListener;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactoryImpl;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * This unit test is used to ensure that a JMS Message delivered to a Component will select the correct operation based
 * on the details in section 1.5 of the JMS Binding specification.
 *
 * @version $Rev$ $Date$
 */
public class OperationSelectionTestCaseFIXME {
    /**
     * This test attempts to invoke a Service with a Single method where scaOperationName is not specified in the JMS
     * Message
     * <p>
     * Expected behaviour is that the single method will be invoked as scaOperationName is ignored
     * 
     * @throws Exception Failed
     */
    @Test
    public void testServiceWithOnlyOneOperationScaOperationNameNotSpecified() throws Exception {
        // Create the operation we should match
        final Operation expectedOperation = newOperation("myOperation");

        // Create the list of operations for the Service
        final List<Operation> operations = new ArrayList<Operation>();
        operations.add(expectedOperation);

        // The name of the Operation in the JMS Message - not specified
        final String scaOperationName = null;

        // Do the test
        doTestJMSBinding(expectedOperation, operations, scaOperationName);
    }

    /**
     * This test attempts to invoke a Service with a Single method where scaOperationName in the JMS Message matches the
     * method name on the Service
     * <p>
     * Expected behaviour is that the single method will be invoked as scaOperationName is ignored
     * 
     * @throws Exception Failed
     */
    @Test
    public void testServiceWithOnlyOneOperationScaOperationNameMatches() throws Exception {
        // Create the operation we should match
        final Operation expectedOperation = newOperation("myOperation");

        // Create the list of operations for the Service
        final List<Operation> operations = new ArrayList<Operation>();
        operations.add(expectedOperation);

        // The name of the Operation in the JMS Message - matches operation name
        final String scaOperationName = expectedOperation.getName();

        // Do the test
        doTestJMSBinding(expectedOperation, operations, scaOperationName);
    }

    /**
     * This test attempts to invoke a Service with a Single method where scaOperationName in the JMS Message is
     * different the method name on the Service
     * <p>
     * Expected behaviour is that the single method will be invoked as scaOperationName is ignored
     * 
     * @throws Exception Failed
     */
    @Test
    public void testServiceWithOnlyOneOperationScaOperationNameDifferent() throws Exception {
        // Create the operation we should match
        final Operation expectedOperation = newOperation("myOperation");

        // Create the list of operations for the Service
        final List<Operation> operations = new ArrayList<Operation>();
        operations.add(expectedOperation);

        // The name of the Operation in the JMS Message - different to operation name
        final String scaOperationName = "Does Not Match Opeation Name";

        // Do the test
        doTestJMSBinding(expectedOperation, operations, scaOperationName);
    }

    /**
     * This test attempts to invoke a Service with a multiple operations where scaOperationName specified in the JMS
     * Message matches an operation name
     * <p>
     * Expected behaviour is that the named method will be invoked.
     * 
     * @throws Exception Failed
     */
    @Test
    public void testServiceWithMultipleOperationsScaOperationNameSpecified() throws Exception {
        // Create the list of operations for the Service
        final List<Operation> operations = new ArrayList<Operation>();
        for (int i = 0; i < 5; i++) {
            operations.add(newOperation("operation" + i));
        }

        // Now try and invoke each operation
        for (Operation expectedOperation : operations) {
            // The name of the Operation in the JMS Message
            final String scaOperationName = expectedOperation.getName();

            // Do the test
            doTestJMSBinding(expectedOperation, operations, scaOperationName);
        }
    }

    /**
     * This test attempts to invoke a Service with a multiple operations where scaOperationName specified in the JMS
     * Message is not set so we invoke the onMessage() method
     * <p>
     * Expected behaviour is that the onMessage() method should be used instead
     * 
     * @throws Exception Failed
     */
    @Test
    public void testServiceWithMultipleOperationsScaOperationNotSpecified() throws Exception {
        // Create the list of operations for the Service
        final List<Operation> operations = new ArrayList<Operation>();
        for (int i = 0; i < 5; i++) {
            operations.add(newOperation("operation" + i));
        }

        // Add the onMessage operation to the Service Contract
        final Operation onMessageOperation = newOperation("onMessage");
        operations.add(onMessageOperation);

        // The name of the Operation in the JMS Message is not set so it will attempt
        // to invoke the onMessage() method
        final String scaOperationName = null;

        // Do the test
        doTestJMSBinding(onMessageOperation, operations, scaOperationName);
    }

    /**
     * This is the test method that will attempt to unit test invoking a Service with the specified operations using a
     * JMS Message with the specified scaOperationName to ensure that it invokes the expectedOperation
     * 
     * @param expectedOperation The Operation we are expecting to be invoked over JMS
     * @param operations The list of Operations supported by the Service
     * @param scaOperationName The value to set scaOperationName in the JMS Message
     * @throws Exception Failed
     */
    private void doTestJMSBinding(Operation expectedOperation, List<Operation> operations, String scaOperationName)
        throws Exception {
        // Create the test JMS Binding
        final JMSBinding jmsBinding = new JMSBinding();
        JMSResourceFactoryImpl jmsResourceFactory = null;

        // Extra information for the method we are invoking
        final String operationParams = "Hello";
        final Object operationReturnValue = "Operation Success";

        // Mock up the Service. Basically, it is going to call:
        // List<Operation> opList = service.getInterfaceContract().getInterface().getOperations();
        final InterfaceContract ifaceContract = EasyMock.createStrictMock(InterfaceContract.class);
        final RuntimeComponentService service = EasyMock.createStrictMock(RuntimeComponentService.class);
        final Interface iface = EasyMock.createStrictMock(Interface.class);
        EasyMock.expect(iface.getOperations()).andReturn(operations);
        EasyMock.expect(ifaceContract.getInterface()).andReturn(iface);
        EasyMock.expect(service.getInterfaceContract()).andReturn(ifaceContract);

        // Mock up getting and invoking the RuntimeWire. It is going to call:
        // service.getRuntimeWire(jmsBinding).invoke(operation, (Object[])requestPayload);
        final RuntimeWire runtimeWire = EasyMock.createStrictMock(RuntimeWire.class);
        EasyMock.expect(service.getRuntimeWire(jmsBinding)).andReturn(runtimeWire);
        EasyMock.expect(runtimeWire.invoke(expectedOperation, new Object[] {operationParams}))
            .andReturn(operationReturnValue);

        // Create the JMS Binding Listener
        final JMSBindingListener bindingListener = new JMSBindingListener(jmsBinding, jmsResourceFactory, service, null);

        // Simulate a message
        final TextMessage requestJMSMsg = EasyMock.createStrictMock(TextMessage.class);
        EasyMock.expect(requestJMSMsg.getStringProperty("scaOperationName")).andReturn(scaOperationName);
        EasyMock.expect(requestJMSMsg.getText()).andReturn(operationParams);
        EasyMock.expect(requestJMSMsg.getJMSReplyTo()).andReturn(null);

        // Lets put all the mocks into replay mode
        // EasyMock.replay(iface);
        EasyMock.replay(ifaceContract);
        EasyMock.replay(service);
        EasyMock.replay(requestJMSMsg);
        EasyMock.replay(runtimeWire);

        // Do the test
        bindingListener.onMessage(requestJMSMsg);

        // Verify our Mock objects
        // EasyMock.verify(iface);
        // EasyMock.verify(ifaceContract);
        // EasyMock.verify(service);
        // EasyMock.verify(requestJMSMsg);
        // EasyMock.verify(runtimeWire);
    }

    private static Operation newOperation(String name) {
        Operation operation = new OperationImpl();
        operation.setName(name);
        return operation;
    }
}
