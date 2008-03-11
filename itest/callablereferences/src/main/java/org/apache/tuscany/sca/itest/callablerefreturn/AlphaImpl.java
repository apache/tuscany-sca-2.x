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
package org.apache.tuscany.sca.itest.callablerefreturn;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Conversation;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class AlphaImpl implements Alpha {
    @Reference
    public Beta beta;

    @Context
    protected ComponentContext componentContext;

    public boolean run() {
        CallableReference<Gamma> gammaRef = null;
        try {
            Object conversationId1 = null;
            Object conversationId2 = null;

            // it is expected that this call returns a reference to Gamma that
            // reuses the established Conversation
            gammaRef = beta.getRef();

            // no Conversation exists
            Conversation con = gammaRef.getConversation();
            if (con == null) {
                System.out.println("Alpha: Conversation to gamma is null");
            } else {
                System.out
                        .println("Alpha: Conversation to gamma exists. conversationId="
                                + con.getConversationID());
                conversationId1 = con.getConversationID();
            }

            // this call should reuse a Conversation, but as none exists it
            // creates a new conversation
            gammaRef.getService().doSomething();
            con = gammaRef.getConversation();
            if (con == null) {
                System.out.println("Alpha: Conversation to gamma is null");
            } else {
                System.out
                        .println("Alpha: Conversation to gamma exists. conversationId="
                                + con.getConversationID());
                conversationId2 = con.getConversationID();
            }

            if ((conversationId1 == null) || (conversationId2 == null)) {
                return false;
            }

            boolean testPassed = conversationId1.equals(conversationId2);
            return testPassed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (gammaRef != null) {
                gammaRef.getService().stop();
            }
        }
    }

}
