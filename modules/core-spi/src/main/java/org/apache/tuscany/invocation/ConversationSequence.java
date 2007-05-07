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

package org.apache.tuscany.invocation;

//FIXME remove this
public interface ConversationSequence {
    /* indicates that no conversational sequence is associated with the message */
    short NONE = 0;
    /* indicates that the message initiates a conversation */
    short START = 1;
    /* indicates that the message continues a conversation */
    short CONTINUE = 2;
    /* indicates that the message ends a conversation */
    short END = 3;
}
