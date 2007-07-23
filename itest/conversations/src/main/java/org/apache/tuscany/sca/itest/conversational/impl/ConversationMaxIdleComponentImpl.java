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

import org.apache.tuscany.sca.itest.conversational.ConversationalService;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(ConversationalService.class)
@Scope("CONVERSATION")
@ConversationAttributes(maxIdleTime="1 seconds")
public class ConversationMaxIdleComponentImpl implements ConversationalService {

    private int count;

    public void businessException() throws Exception {   
    }
    
    public void businessExceptionCallback() throws Exception {   
    }    
    
    public void destroy() {
    }

    public String endConversation() {
        return null;
    }

    public String endConversationCallback() {
        return null;
    }

    public void incrementCount() {
    }

    public void incrementCountCallback() {
    }

    public void init() {
    }

    public void initializeCount(int count) {
        this.count = count;
    }

    public void initializeCountCallback(int count) {
    }

    public int retrieveCount() {
        return count;
    }

    public int retrieveCountCallback() {
        return 0;
    }

}
