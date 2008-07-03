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
import org.osoa.sca.annotations.Service;

/**
 * This is a test client that is used to test Serializing and Deserializing
 * ServiceReferences within a SCA Application
 * 
 * @version $Date$ $Revision$
 */
@Service(SCAManagedClient.class)
public class SCAManagedClientImpl implements SCAManagedClient, StatelessServiceCallback {

    /**
     * Injected reference to the StatelessService
     */
    @Reference(name = "statelessService")
    protected ServiceReference<StatelessService> statelessServiceRef;

    /**
     * Injected reference to the Nested StatelessService.
     */
    @Reference(name = "nestedStatelessService")
    protected ServiceReference<StatelessService> nestedStatelessServiceRef;

    /**
     * Tests Serializing a Stateless ServiceReference
     * 
     * @throws Exception Test failed
     */
    public void testSerializeStatelessServiceReference() throws Exception {
        doTestSerializeStatelessServiceReference(statelessServiceRef);
    }
    
    /**
     * Tests Serializing a Nested Stateless ServiceReference.
     * 
     * @throws Exception Test failed
     */
    public void testSerializeNestedStatelessServiceReference() throws Exception {
        doTestSerializeStatelessServiceReference(nestedStatelessServiceRef);
    }

    /**
     * Tests Serializing a Stateless ServiceReference.
     * 
     * @throws Exception Test failed
     */
    private void doTestSerializeStatelessServiceReference(
            ServiceReference<StatelessService> aServiceRef) throws Exception {
        Assert.assertNotNull(aServiceRef);

        StatelessService service = aServiceRef.getService();
        service.getCurrentTime();
        
        // Serialize the ServiceReference
        byte[] serializedSR = ServiceReferenceUtils.serialize(aServiceRef);
        Assert.assertNotNull(serializedSR);

        // Deserialize the ServiceReference
        ServiceReference<?> deserializedSR = ServiceReferenceUtils.deserializeServiceReference(serializedSR);
        Assert.assertNotNull(deserializedSR);
        ServiceReference<StatelessService> regotServiceRef = (ServiceReference<StatelessService>) deserializedSR;
        Assert.assertNotNull(regotServiceRef);

        // Use the ServiceReference to access the Service.
        StatelessService regotService = regotServiceRef.getService();
        Assert.assertNotNull(regotService);
    }

    /**
     * Simple callback method
     * 
     * @param msg The call back message
     */
    public void callback(String msg) {
        System.out.println("Stateless Callback with message " + msg);
    }
    
    /**
     * Tests Serializing a Callback to a Stateless Service as managed
     * SCA code
     * 
     * @throws Exception Test failed
     */
    public void testSerializeCallbackToStatelessServiceInsideSCA() throws Exception {
        Assert.assertNotNull(statelessServiceRef);

        StatelessService service = statelessServiceRef.getService();
        String msg = "A message for the callback " + System.currentTimeMillis();
        service.triggerCallback(msg);
    }
}
