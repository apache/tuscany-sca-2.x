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

package org.apache.tuscany.sca.itest;

import junit.framework.Assert;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This is the client implementation for the wires tests 
 */
@Service(WireClient.class)
public class WireClientImpl implements WireClient {
    /**
     * This is our injected reference to the WireService
     */
    @Reference
    protected WireService aWireService;

    /**
     * This tests the wire reference
     */
    public void runTests() {
        // Make sure the wire has injected a reference
        Assert.assertNotNull(aWireService);
        
        // Test the injected reference
        String msg = aWireService.sayHello("MCC");
        
        // Validate the response
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello MCC", msg);
    }
}
