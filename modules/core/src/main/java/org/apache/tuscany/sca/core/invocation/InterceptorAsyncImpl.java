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

package org.apache.tuscany.sca.core.invocation;


import org.apache.tuscany.sca.invocation.InterceptorAsync;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsync;
import org.apache.tuscany.sca.invocation.Message;

/**
 * A base class that holds the mechanics for representing 
 * chained interceptors and for driving processing up and 
 * down the chain. 
 *
 */
public abstract class InterceptorAsyncImpl implements InterceptorAsync {
    
    protected InvokerAsync next;
    protected InvokerAsync previous;
    
    public Invoker getNext() {
        return (Invoker)next;
    }
    
    public void setNext(Invoker next) {
        this.next = (InvokerAsync)next;
    }
    
    public InvokerAsync getPrevious() {
        return previous;
    }
    
    public void setPrevious(InvokerAsync previous) {
        this.previous = previous;
    }
    
    public Message invoke(Message msg) {
        msg = processRequest(msg);
        Message resultMsg = getNext().invoke(msg);
        resultMsg = processResponse(resultMsg);
        return resultMsg;
    }
    
    public void invokeAsyncRequest(Message msg) {
        msg = processRequest(msg);
        ((InvokerAsync)getNext()).invokeAsyncRequest(msg);
    }
    
    public Message invokeAsyncResponse(Message msg) {
        msg = processResponse(msg);
        if (getPrevious() != null){
            return ((InvokerAsync)getPrevious()).invokeAsyncResponse(msg);
        }
        return msg;
    }
}
