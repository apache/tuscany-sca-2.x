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
import org.apache.tuscany.sca.core.assembly.RuntimeComponentServiceImpl;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.junit.Test;

/**
 * This method tests various combinations of the JMS Binding create modes.
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
public class JMSBindingServiceQueueCreateModeTestCaseFIXME {
    /**
     * Test creating a queue in "never" mode where the queue does not exist. We are expecting an exception
     */
    @Test
    public void testCreateNeverQueueNotExist() {
        String createMode = "never";
        boolean preCreateQueue = false;
        boolean expectingException = true;

        doTestCase(createMode, preCreateQueue, expectingException);
    }

    /**
     * Test creating a queue in "never" mode where the queue exists. We are expecting this to work
     */
    @Test
    public void testCreateNeverQueueExist() {
        String createMode = "never";
        boolean preCreateQueue = true;
        boolean expectingException = false;

        doTestCase(createMode, preCreateQueue, expectingException);
    }

    /**
     * Test creating a queue in "ifnotexist" mode where the queue does not exist. We are expecting this to work
     */
    @Test
    public void testCreateIfNotExistQueueNotExist() {
        String createMode = "ifnotexist";
        boolean preCreateQueue = false;
        boolean expectingException = false;

        doTestCase(createMode, preCreateQueue, expectingException);
    }

    /**
     * Test creating a queue in "ifnotexist" mode where the queue exists. We are expecting this to work
     */
    @Test
    public void testCreateIfNotExistQueueExist() {
        String createMode = "ifnotexist";
        boolean preCreateQueue = true;
        boolean expectingException = false;

        doTestCase(createMode, preCreateQueue, expectingException);
    }

    /**
     * Test creating a queue in "always" mode where the queue does not exist. We are expecting this to work
     */
    @Test
    public void testCreateAlwaysQueueNotExist() {
        String createMode = "always";
        boolean preCreateQueue = false;
        boolean expectingException = false;

        doTestCase(createMode, preCreateQueue, expectingException);
    }

    /**
     * Test creating a queue in "always" mode where the queue exists. We are expecting an exception
     */
    @Test
    public void testCreateAlwaysQueueExist() {
        String createMode = "always";
        boolean preCreateQueue = true;
        boolean expectingException = true;

        doTestCase(createMode, preCreateQueue, expectingException);
    }

    /**
     * This is the main test method for the various test scenarios for the JMS Binding.
     * 
     * @param createMode The required create mode for the destination queue
     * @param preCreateQueue Whether the queue should be pre-created.
     * @param expectingException true if test should throw an exception
     */
    private void doTestCase(String createMode, boolean preCreateQueue, boolean expectingException) {
        String destinationName = "SomeDestination";
        String jmsBindingName = "MyJMSBinding";
        String serviceName = "MyServiceName";

        // Create a JMS Binding with the required test parameters
        JMSBinding jmsBinding = new JMSBinding();
        jmsBinding.setDestinationCreate(createMode);
//        if (preCreateQueue) {
//            jmsBinding.setJmsResourceFactoryName(MockJMSResourceFactoryQueueExist.class.getName());
//        } else {
//            jmsBinding.setJmsResourceFactoryName(MockJMSResourceFactoryQueueNotExist.class.getName());
//        }
        jmsBinding.setDestinationName(destinationName);
        jmsBinding.setName(jmsBindingName);

        RuntimeComponentService service = new RuntimeComponentServiceImpl();
        service.setName(serviceName);

        // Try and create the JMS Binding Service for the JMS Binding
        try {
            JMSBindingServiceBindingProvider jmsService =
                new JMSBindingServiceBindingProvider(null, service, jmsBinding, null);
            jmsService.start();

            // Check whether we were expecting an exception
            if (expectingException) {
                // We were expecting an exception
                Assert.fail("This binding should have failed as it is invalid");
            }
        } catch (JMSBindingException ex) {
            // Were we expecting an exception
            if (!expectingException) {
                ex.printStackTrace();
                // No we were not expecting an exception
                Assert.fail("Unexpected exception of " + ex);
            }

            // We should get a JMSBindingException
            Assert.assertTrue(ex.getMessage().indexOf("Error starting JMSServiceBinding") != -1);

            // Validate that the expected chained exception exception has the text we expect
            Assert.assertNotNull(ex.getCause());
            Assert.assertTrue(ex.getCause().getMessage().indexOf("JMS Destination") != -1);
            Assert.assertTrue(ex.getCause().getMessage().indexOf(createMode) != -1);
            Assert.assertTrue(ex.getCause().getMessage().indexOf(destinationName) != -1);
            Assert
                .assertTrue(ex.getCause().getMessage().indexOf("registering service " + serviceName + " listener") != -1);
        }
    }
}
