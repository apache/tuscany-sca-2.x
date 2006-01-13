/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.binding;

import java.lang.reflect.Method;

import org.apache.tuscany.core.message.Message;

/**
 * Represents a processing context for a request comming through a a binding
 * 
 * @version $Rev$ $Date$
 */
public interface MessageContext {

    /**
     * The raw payload as it comes through the binding transport 
     */
    public Object getPayload();

    /**
     * Returns a marhsalled representation of the payload
     */
    public Message getMessage();

    /**
     * Sets a message to be used in marhsalling the payload
     */
    public void setMessage(Message msg);
    
    public Method getTargetMethod();
    
    public void setTargetMethod(Method m);
}
