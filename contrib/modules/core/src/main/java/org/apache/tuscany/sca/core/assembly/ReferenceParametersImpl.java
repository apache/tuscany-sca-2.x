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

package org.apache.tuscany.sca.core.assembly;

import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceParametersImpl implements ReferenceParameters {
    private Object callbackID;
    private Object conversationID;
    private EndpointReference callbackReference;
    private Object callbackObjectID;
    
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
     * @see org.apache.tuscany.sca.runtime.ReferenceParameters#getCallbackReference()
     */
    public EndpointReference getCallbackReference() {
        return callbackReference;
    }
    /**
     * @see org.apache.tuscany.sca.runtime.ReferenceParameters#setCallback(java.lang.Object)
     */
    public void setCallbackReference(EndpointReference callback) {
        this.callbackReference = callback;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the callbackObjectID
     */
    public Object getCallbackObjectID() {
        return callbackObjectID;
    }
    /**
     * @param callbackObjectID the callbackObjectID to set
     */
    public void setCallbackObjectID(Object callbackObjectID) {
        this.callbackObjectID = callbackObjectID;
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((callbackID == null) ? 0 : callbackID.hashCode());
        result = prime * result + ((callbackObjectID == null) ? 0 : callbackObjectID.hashCode());
        result = prime * result + ((callbackReference == null) ? 0 : callbackReference.hashCode());
        result = prime * result + ((conversationID == null) ? 0 : conversationID.hashCode());
        return result;
    }
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ReferenceParametersImpl))
            return false;
        final ReferenceParametersImpl other = (ReferenceParametersImpl)obj;
        if (callbackID == null) {
            if (other.callbackID != null)
                return false;
        } else if (!callbackID.equals(other.callbackID))
            return false;
        if (callbackObjectID == null) {
            if (other.callbackObjectID != null)
                return false;
        } else if (!callbackObjectID.equals(other.callbackObjectID))
            return false;
        if (callbackReference == null) {
            if (other.callbackReference != null)
                return false;
        } else if (!callbackReference.equals(other.callbackReference))
            return false;
        if (conversationID == null) {
            if (other.conversationID != null)
                return false;
        } else if (!conversationID.equals(other.conversationID))
            return false;
        return true;
    }
}
