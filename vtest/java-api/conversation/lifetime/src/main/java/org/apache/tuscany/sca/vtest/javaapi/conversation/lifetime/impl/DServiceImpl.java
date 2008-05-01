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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.DService;
import org.junit.Assert;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(DService.class)
@Scope("CONVERSATION")
public class DServiceImpl implements DService {

    String someState;

    public void setState(String someState) {
        this.someState = someState;
    }

    public String getState() {
        return someState;
    }

    public void continueConversation(ServiceReference<BService> b, Object convId) {

        b.getService().setState("Some more state");
        Assert.assertEquals(convId, b.getConversation().getConversationID());
    }

    public void continueConversation2(String filename, Object convId, String serializedState) {
        ServiceReference<BService> b = readReference(filename);
        Assert.assertEquals(serializedState, b.getService().getState());
        Assert.assertEquals(convId, b.getConversation().getConversationID());
    }

    // Utilities
    @SuppressWarnings("unchecked")
    private ServiceReference<BService> readReference(String filename) {
        ServiceReference<BService> b = null;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            ServiceReference<BService> readObject = (ServiceReference<BService>)in.readObject();
            b = readObject;
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
