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

package org.apache.tuscany.sca.core.context;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceParameters {
    private String componentURI;
    private Object callbackID;
    private Object conversationID;
    /**
     * @return the callbackID
     */
    public Object getCallbackID() {
        return callbackID;
    }
    /**
     * @param callbackID the callbackID to set
     */
    public void setCallbackID(Object callbackID) {
        this.callbackID = callbackID;
    }
    /**
     * @return the conversationID
     */
    public Object getConversationID() {
        return conversationID;
    }
    /**
     * @param conversationID the conversationID to set
     */
    public void setConversationID(Object conversationID) {
        this.conversationID = conversationID;
    }
    /**
     * @return the componentURI
     */
    public String getComponentURI() {
        return componentURI;
    }
    /**
     * @param componentURI the componentURI to set
     */
    public void setComponentURI(String componentURI) {
        this.componentURI = componentURI;
    }
}
