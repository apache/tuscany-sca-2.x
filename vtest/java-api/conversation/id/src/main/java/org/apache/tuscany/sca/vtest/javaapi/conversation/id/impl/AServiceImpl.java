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

package org.apache.tuscany.sca.vtest.javaapi.conversation.id.impl;

import org.apache.tuscany.sca.vtest.javaapi.conversation.id.AService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.id.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.id.CService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.id.CustomConversationId;
import org.junit.Assert;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
@Scope("CONVERSATION")
public class AServiceImpl implements AService {

    @Reference
    protected ServiceReference<BService> b;
    
    @Reference
    protected ServiceReference<CService> c;

    public void testAnnotation() {
        b.getService().testAnnotation();  
    }

    public void testAnnotation2() {
        b.getService().testAnnotation2();     
    }

    public void testAnnotation3() {
        b.getService().testAnnotation3();        
    }

    public void testAnnotation4() {
        CustomConversationId id = new CustomConversationId (1, "One");
        c.setConversationID(id);
        c.getService().testAnnotation();        
    }

    public void testAnnotation5() {
        b.getService().getState();
        Assert.assertNotNull(b.getConversation().getConversationID());
//        Assert.assertNotNull(b.getConversationID());  
        
        CustomConversationId id = new CustomConversationId (1, "One");
        c.setConversationID(id);
        Assert.assertSame(id, c.getConversationID());
    }
}
