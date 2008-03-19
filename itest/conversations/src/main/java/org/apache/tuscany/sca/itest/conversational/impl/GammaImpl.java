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

import java.util.ArrayList;

import org.apache.tuscany.sca.itest.Record;
import org.apache.tuscany.sca.itest.conversational.Gamma;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */

@Service(Gamma.class)
@Scope("CONVERSATION")
public class GammaImpl implements Gamma {
    @ConversationID
    public String conversationId;

    private ArrayList<Record> list;
    
    public void start(int param) {
        list = new ArrayList<Record>();
        fillList(param, conversationId);
    }

    public boolean hasNext() {
        return !list.isEmpty();
    }

    public Record next() {
        return list.remove(0);
    }

    public void stop() {
        list = null;
    }
    
    private void fillList(int param, String cid) {
        for (int i = 0; i < param; i++) {
            Record record = new Record();
            record.id = "id_" + i;
            record.conversationId = cid;
            list.add(record);
        }
    }
}
