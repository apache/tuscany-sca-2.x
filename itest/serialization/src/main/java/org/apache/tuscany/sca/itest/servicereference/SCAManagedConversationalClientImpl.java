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
package org.apache.tuscany.sca.itest.servicereference;

import org.apache.tuscany.sca.itest.servicereference.utils.ServiceReferenceUtils;
import org.junit.Assert;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * This is a test client that is used to test Serializing and Deserializing
 * ServiceReferences within a SCA Application
 * 
 * @version $Rev$ $Date$
 */
@Service(SCAManagedConversationalClient.class)
@Scope("CONVERSATION")
public class SCAManagedConversationalClientImpl implements SCAManagedConversationalClient, ConversationalServiceCallback {

    /**
     * Injected reference to the ConversationalService.
     */
    @Reference(name = "conversationalService")
    protected ServiceReference<ConversationalService> conversationalServiceRef;

    /**
     * Injected reference to the ConversationalService.
     */
    @Reference(name = "nestedConversationalService")
    protected ServiceReference<ConversationalService> nestedConversationalServiceRef;

    /**
     * This is the message that we sent to the callback.
     */
    private String messageSentToCallback;

    /**
     * Tests Serializing a Conversational ServiceReference.
     * 
     * @throws Exception Test failed
     */
    public void testSerializeConversationalServiceReference() throws Exception {
        doTestSerializeConversationalServiceReference(conversationalServiceRef);
    }

    /**
     * Tests Serializing a Nested Conversational ServiceReference.
     * 
     * @throws Exception Test failed
     */
    public void testSerializeNestedConversationalServiceReference() throws Exception {
        doTestSerializeConversationalServiceReference(nestedConversationalServiceRef);
    }

    /**
     * Test Serializing a Conversational ServiceReference.
     * 
     * @param aServiceRef The Reference to Serialize
     * @throws Exception Test failed.
     */
    private void doTestSerializeConversationalServiceReference(ServiceReference<ConversationalService> aServiceRef) throws Exception {
        Assert.assertNotNull(aServiceRef);

        ConversationalService service = aServiceRef.getService();
        Object origConvID = service.getConversationID();
        Assert.assertNotNull(origConvID);

        // Serialize the ServiceReference
        byte[] serializedSR = ServiceReferenceUtils.serialize(aServiceRef);
        Assert.assertNotNull(serializedSR);

        // Deserialize the ServiceReference
        ServiceReference<?> deserializedSR = ServiceReferenceUtils.deserializeServiceReference(serializedSR);
        Assert.assertNotNull(deserializedSR);
        ServiceReference<ConversationalService> regotServiceRef = (ServiceReference<ConversationalService>) deserializedSR;
        Assert.assertNotNull(regotServiceRef);

        // Use the ServiceReference to access the Service.
        ConversationalService regotService = regotServiceRef.getService();
        Assert.assertNotNull(regotService);
        Object regotConvID = regotService.getConversationID();
        Assert.assertNotNull(regotConvID);

        // Make sure we have the same Conversation ID
        Assert.assertEquals(origConvID, regotConvID);
    }


    /**
     * Tests Serializing a Callback to a Conversational Service as managed
     * SCA code.
     * 
     * @throws Exception Test failed
     */
    public void testSerializeCallbackToConversationalServiceInsideSCA() throws Exception {
        Assert.assertNotNull(conversationalServiceRef);

        ConversationalService service = conversationalServiceRef.getService();
        messageSentToCallback = "A message for the callback " + System.currentTimeMillis();
        service.triggerCallback(messageSentToCallback);
    }
    
    /**
     * Simple callback method.
     * 
     * @param msg The call back message
     */
    public void callback(String msg) {
        System.out.println("Stateless Callback with message " + msg);

        // Make sure we received the message we expected
        Assert.assertEquals(messageSentToCallback, msg);
    }
}
