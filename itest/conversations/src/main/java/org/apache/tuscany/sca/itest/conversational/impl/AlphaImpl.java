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
package org.apache.tuscany.sca.itest.conversational.impl;

import org.apache.tuscany.sca.itest.Record;
import org.apache.tuscany.sca.itest.TestResult;
import org.apache.tuscany.sca.itest.conversational.Alpha;
import org.apache.tuscany.sca.itest.conversational.Beta;
import org.apache.tuscany.sca.itest.conversational.Gamma;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */

@Service(Alpha.class)
@Scope("COMPOSITE")
public class AlphaImpl implements Alpha {
    @Reference
    public Beta beta;
    
    @Context
    protected ComponentContext componentContext;
    
    public void run(int param) {
        CallableReference<Gamma> gammaRef = null;
        boolean testPassed = true;
        try {
            gammaRef = beta.getRef(param);
            while (gammaRef.getService().hasNext()) {
                Record record = gammaRef.getService().next();
                if (!record.conversationId.equals(gammaRef.getConversation().getConversationID())) {
                    // Record returned is not from this conversation.
                    testPassed = false;
                }
            }
        } catch (Exception ex) {
            testPassed = false;
            ex.printStackTrace();
        } finally {
            TestResult.updateCompleted();
            if (gammaRef != null) {
                TestResult.results.put(gammaRef.getConversation()
                        .getConversationID(), testPassed);
                gammaRef.getService().stop();
            }
        }
    }
}
