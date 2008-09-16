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

package org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.AService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.AServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.BServiceBusinessException;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.CService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.DService;
import org.junit.Assert;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ConversationEndedException;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
@Scope("CONVERSATION")
public class AServiceImpl implements AService, AServiceCallback {

    @Context
    public ComponentContext context;

    @Reference
    protected ServiceReference<BService> b;

    @Reference
    protected DService d;

    public void testConversationStarted() {
        b.getService().setState("Some state");
        Assert.assertNotNull(b.getConversation().getConversationID());
    }

    public void testConversationStarted2() {
        ServiceReference<BService> ref = context.getServiceReference(BService.class, "b");
        ref.getService().setState("Some state");
        Assert.assertNotNull(ref.getConversation().getConversationID());
    }

    public void testConversationContinue() {

        b.getService().setState("Some state");
        Object id = b.getConversation().getConversationID();
        b.getService().setState("Some more state");
        Assert.assertEquals(id, b.getConversation().getConversationID());
    }

    public void testConversationContinue2() {

        b.getService().setState("Some state");
        d.continueConversation(b, b.getConversation().getConversationID());

    }

    public void testConversationContinue3() {

        String serializedState = "Serialized State";

        String filename = "target/Serialized-Reference.txt";
        b.getService().setState(serializedState);
        Object id = b.getConversation().getConversationID();
        writeReference(b, filename);
        d.continueConversation2(filename, id, serializedState);

    }

    // Utilities

    private void delayForSeconds(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);// millisecs
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

    private void writeReference(ServiceReference<BService> ref, String filename) {

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(ref);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void testConversationEnd() {
        String someState = "Some state";
        b.getService().setState(someState);
        b.getService().endConversation();
        Assert.assertNotSame(someState, b.getService().getState());
    }

    public void testConversationEnd2() {
        String someState = "Some state";
        b.getService().setState(someState);
        b.getService().endConversationViaCallback();
        Assert.assertNotSame(someState, b.getService().getState());
    }

    public void endConversation() {
        System.out.println("A-callback to end conversation");
    }

    public void testConversationEnd3() {
        String someState = "Some state";
        b.getService().setState(someState);
        delayForSeconds(2);
        try {
            b.getService().getState();
        } catch (ConversationEndedException e) {
            b.getConversation().end();// need to clean up to avoid exception
        }
        Assert.assertNotSame(someState, b.getService().getState());
    }

    public void testConversationEnd4() {
        String someState = "Some state";
        b.getService().setState(someState);
        b.getConversation().end();
        Assert.assertNotSame(someState, b.getService().getState());
    }

    public void testConversationEnd5() {
        String someState = "Some state";
        b.getService().setState(someState);
        try {
            b.getService().throwNonBusinessException();
        } catch (Error e) {
            // Expected
        }
        Assert.assertNotSame(someState, b.getService().getState());
    }

    public void testConversationEnd6() {
        String someState = "Some state";
        b.getService().setState(someState);
        Object id = b.getConversation().getConversationID();
        b.getService().endConversation();
        Assert.assertNotSame(someState, b.getService().getState());
        Assert.assertNotSame(id, b.getConversation().getConversationID());
    }

    public void testConversationEnd7() {
        b.getService().setState("Some state");
        b.getService().endConversation();
        Assert.assertNull(b.getConversationID());
    }

    public void testConversationEnd8() {
        String someState = "Some state";
        b.getService().setState(someState);
        delayForSeconds(2);
        b.getService().getState();
    }
    
    public void testConversationEnd9() {
        String someState = "Some state";
        b.getService().setState(someState);
        try {
            b.getService().throwBusinessException();
        } catch (BServiceBusinessException e) {
            // Expected
        }
        Assert.assertSame(someState, b.getService().getState());
    }    

}
