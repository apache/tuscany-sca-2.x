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

package org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes.B2Service;
import org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes.BService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes.CService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
public class AServiceImpl implements AService {

    @Reference
    protected BService b;
    
    @Reference
    protected B2Service b2;

    @Reference
    protected CService c;
    
    @Context 
    protected ComponentContext context;
    
    private void delayForSeconds(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);// millisecs
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

    public void testMaxAge() {

        String someState = "someState";
        b.setState(someState);
        delayForSeconds(2);
        b.setState(someState);

    }
    
    public void testMaxIdle() {

        String someState = "someState";
        b2.setState(someState);
        delayForSeconds(2);
        b2.setState(someState);

    }

    public void testSinglePrincipal() {
        
        b.setState("Some state");
        System.out.println("Calling c and passing reference to b");
        c.testSinglePricipal(context.getServiceReference(BService.class, "b"));
        
    }

}
