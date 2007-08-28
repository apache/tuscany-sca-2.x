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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.itest.conversational.ConversationalService;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Service;

@Service(ConversationalService.class)
public class ConversationalServiceStatelessScope implements ConversationalService {

    @ConversationID
    protected String cid;

    static Map<String, Integer> state = new HashMap<String, Integer>();
    
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

    public String endConversation() {
        state.remove(cid);
        return cid;
    }

    public String endConversationCallback() {
        // TODO Auto-generated method stub
        return null;
        
    }

    public void incrementCount() {
        state.put(cid, Integer.valueOf(state.get(cid)+1));
    }

    public void businessException() throws Exception {
        throw new Exception("Business Exception");
    }
    
    public void incrementCountCallback() {
        // TODO Auto-generated method stub
        
    }

    public void init() {
        // TODO Auto-generated method stub
        
    }

    public void initializeCount(int count) {
        state.put(cid, Integer.valueOf(count));
    }

    public void initializeCountCallback(int count) {
        // TODO Auto-generated method stub
        
    }
    
    public void businessExceptionCallback() throws Exception {
        throw new Exception("Business Exception");
    }    

    public int retrieveCount() {
        return state.get(cid);
    }

    public int retrieveCountCallback() {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
