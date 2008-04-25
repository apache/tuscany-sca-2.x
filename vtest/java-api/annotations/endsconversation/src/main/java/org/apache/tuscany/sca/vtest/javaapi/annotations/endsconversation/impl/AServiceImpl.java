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

package org.apache.tuscany.sca.vtest.javaapi.annotations.endsconversation.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.endsconversation.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.endsconversation.BService;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.junit.Assert;

@Service(AService.class)
public class AServiceImpl implements AService {

    @Reference
    protected BService b;
    
    @Reference
    protected ServiceReference<BService> b2;
    
    private void delayForSeconds(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);// millisecs
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

    public void testAtEndsConversation() {
        String firstId;
        b.setState("SomeState");
        firstId = b.getConversationId();
        b.endConversation();
        b.setState("SomeState");// This should start a new conversation
        Assert.assertNotSame(b.getConversationId(), firstId); 
    }

    public void testSREndConversation() {
        String firstId;
        b2.getService().setState("SomeState");
        firstId = b2.getService().getConversationId();
        b2.getConversation().end();
        b2.getService().setState("SomeState");// This should start a new conversation
        Assert.assertNotSame(b2.getService().getConversationId(), firstId); 
    }

    public void testTimedEnd() {
       b.setState("someState");
       delayForSeconds(2);
       b.setState("someState"); // should fail w/ timeout   
    }

}
