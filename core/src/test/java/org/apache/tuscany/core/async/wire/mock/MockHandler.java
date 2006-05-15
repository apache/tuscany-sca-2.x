/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.async.wire.mock;

import org.apache.tuscany.core.wire.MessageHandler;
import org.apache.tuscany.core.message.Message;

/**
 *
 */
public class MockHandler implements MessageHandler {

    private int count =0;
    
    public boolean processMessage(Message message) {
        //System.out.println("Invoking handler");
        count++;
        return true;
    }
    
    public int getCount(){
        return count;
    }
}
