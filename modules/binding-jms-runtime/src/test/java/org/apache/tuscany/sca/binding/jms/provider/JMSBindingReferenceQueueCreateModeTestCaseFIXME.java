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
package org.apache.tuscany.sca.binding.jms.provider;

import junit.framework.Assert;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.junit.Test;

/**
 * This unit test tests various combinations of the JMS Binding create modes for both request and response queues.
 * <p>
 * The SCA JMS Binding specification lists 3 create modes:
 * <ul>
 * <li>always - the JMS queue is always created. It is an error if the queue already exists
 * <li>ifnotexist - the JMS queue is created if it does not exist. It is not an error if the queue already exists
 * <li>never - the JMS queue is never created. It is an error if the queue does not exist
 * </ul>
 * See the SCA JMS Binding specification for more information.
 *
 * @version $Rev$ $Date$
 */
public class JMSBindingReferenceQueueCreateModeTestCaseFIXME {

    /**
     * Test creating a request queue in "never" mode where the queue does not exist. We are expecting an exception
     */
    @Test
    public void testRequestCreateNeverQueueNotExist() {
        String requestCreateMode = "never";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = false;
        boolean expectingRequestException = true;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a request queue in "never" mode where the queue exists. We are expecting this to work
     */
    @Test
    public void testRequestCreateNeverQueueExists() {
        String requestCreateMode = "never";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = true;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a request queue in "ifnotexist" mode where the queue does not exist. We are expecting this to work
     */
    @Test
    public void testRequestCreateIfNotExistQueueNotExist() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = false;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a request queue in "ifnotexist" mode where the queue exists. We are expecting this to work
     */
    @Test
    public void testRequestCreateIfNotExistQueueExist() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = true;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a request queue in "always" mode where the queue does not exist. We are expecting this to work
     */
    @Test
    public void testRequestCreateAlwaysQueueNotExist() {
        String requestCreateMode = "always";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = false;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a request queue in "always" mode where the queue exists. We are expecting an exception
     */
    @Test
    public void testRequestCreateAlwaysQueueExists() {
        String requestCreateMode = "always";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = true;
        boolean expectingRequestException = true;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a response queue in "never" mode where the queue does not exist. We are expecting an exception
     */
    @Test
    public void testResponseCreateNeverQueueNotExist() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "never";
        boolean preCreateQueue = false;
        boolean expectingRequestException = false;
        boolean expectingResponseException = true;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a response queue in "never" mode where the queue exists. We are expecting this to work
     */
    @Test
    public void testResponseCreateNeverQueueExists() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "never";
        boolean preCreateQueue = true;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a response queue in "ifnotexist" mode where the queue does not exist. We are expecting this to work
     */
    @Test
    public void testResponseCreateIfNotExistQueueNotExist() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = false;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a response queue in "ifnotexist" mode where the queue not exists. We are expecting this to work
     */
    @Test
    public void testResponseCreateIfNotExistQueueExist() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "ifnotexist";
        boolean preCreateQueue = true;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a response queue in "always" mode where the queue does not exist. We are expecting this to work
     */
    @Test
    public void testResponseCreateAlwaysQueueNotExist() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "always";
        boolean preCreateQueue = false;
        boolean expectingRequestException = false;
        boolean expectingResponseException = false;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * Test creating a response queue in "always" mode where the queue exists. We are expecting an exception
     */
    @Test
    public void testResponseCreateAlwaysQueueExists() {
        String requestCreateMode = "ifnotexist";
        String responseCreateMode = "always";
        boolean preCreateQueue = true;
        boolean expectingRequestException = false;
        boolean expectingResponseException = true;

        doTestCase(requestCreateMode,
                   responseCreateMode,
                   preCreateQueue,
                   expectingRequestException,
                   expectingResponseException);
    }

    /**
     * This is the main test method for the various test scenarios for the JMS Binding.
     * 
     * @param requestCreateMode The required create mode for the request destination queue
     * @param responseCreateMode The required create mode for the response destination queue
     * @param preCreateQueue Whether the queue should be pre-created.
     * @param expectingRequestException true if we are expecting an exception because the request queue configuration is
     *            invalid; false otherwise
     * @param expectingResponseException true if we are expecting an exception because the request queue configuration
     *            is invalid; false otherwise
     */
    private void doTestCase(String requestCreateMode,
                            String responseCreateMode,
                            boolean preCreateQueue,
                            boolean expectingRequestException,
                            boolean expectingResponseException) {
        String requestDestinationName = "SomeRequestDestination";
        String responseDestinationName = "SomeResponseDestination";
        String jmsBindingName = "MyJMSBinding";

        // Create a JMS Binding with the required test parameters
        JMSBinding jmsBinding = new JMSBinding();
        jmsBinding.setDestinationCreate(requestCreateMode);
        jmsBinding.setResponseDestinationCreate(responseCreateMode);
        if (preCreateQueue) {
//            jmsBinding.setJmsResourceFactoryName(new JMSResourceFactory(null, null, null));
        } else {
//            jmsBinding.setJmsResourceFactoryName(MockJMSResourceFactoryQueueNotExist.class.getName());
        }
        jmsBinding.setDestinationName(requestDestinationName);
        jmsBinding.setResponseDestinationName(responseDestinationName);
        jmsBinding.setName(jmsBindingName);

        // Create the operation
        Operation operation = new OperationImpl();
        operation.setName("OperationName");

        // Try and create the JMS Binding Invoker for the JMS Binding
        try {
            new JMSBindingInvoker(jmsBinding, operation, null, null);

            // Check whether we were expecting an exception
            if (expectingRequestException || expectingResponseException) {
                // We were expecting an exception
                Assert.fail("This binding should have failed as it is invalid");
            }
        } catch (JMSBindingException ex) {
            // Were we expecting an exception
            if (!expectingRequestException && !expectingResponseException) {
                // No we were not expecting an exception
                Assert.fail("Unexpected exception of " + ex);
            }

            // Validate that the expected exception has the text we expect
            if (expectingRequestException) {
                Assert.assertTrue(ex.getMessage().indexOf("JMS Destination") != -1);
                Assert.assertTrue(ex.getMessage().indexOf(requestCreateMode) != -1);
                Assert.assertTrue(ex.getMessage().indexOf(requestDestinationName) != -1);
            } else if (expectingResponseException) {
                Assert.assertTrue(ex.getMessage().indexOf("JMS Response Destination") != -1);
                Assert.assertTrue(ex.getMessage().indexOf(responseCreateMode) != -1);
                Assert.assertTrue(ex.getMessage().indexOf(responseDestinationName) != -1);
            }
            Assert.assertTrue(ex.getMessage().indexOf("registering binding " + jmsBindingName + " invoker") != -1);
        }
    }
}
