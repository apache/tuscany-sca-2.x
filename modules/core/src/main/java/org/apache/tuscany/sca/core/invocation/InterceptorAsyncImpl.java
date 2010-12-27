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
import org.apache.tuscany.sca.invocation.InvokerAsyncRequest;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;

/**
 * A base class that holds the mechanics for representing 
 * chained interceptors and for driving processing up and 
 * down the chain. 
 *
 */
public abstract class InterceptorAsyncImpl implements InterceptorAsync {
    
    protected Invoker next;
    protected InvokerAsyncResponse previous;
    
    public Invoker getNext() {
        return (Invoker)next;
    }
    
    public void setNext(Invoker next) {
        this.next = next;
    }
    
    public InvokerAsyncResponse getPrevious() {
        return previous;
    }
    
    public void setPrevious(InvokerAsyncResponse previous) {
        this.previous = previous;
    }
    
    public Message invoke(Message msg) {
        msg = processRequest(msg);
        Message resultMsg = getNext().invoke(msg);
        resultMsg = processResponse(resultMsg);
        return resultMsg;
    }
    
    public void invokeAsyncRequest(Message msg) throws Throwable {
    	try{ 
	        msg = processRequest(msg);
	        InvokerAsyncRequest theNext = (InvokerAsyncRequest)getNext();
	        if( theNext != null ) theNext.invokeAsyncRequest(msg);
	        postProcessRequest(msg);
    	} catch (Throwable e) {
    		postProcessRequest(msg, e);
    	} // end try
    } // end method invokeAsyncRequest
    
    public void invokeAsyncResponse(Message msg) {
        msg = processResponse(msg);
        InvokerAsyncResponse thePrevious = (InvokerAsyncResponse)getPrevious();
        if (thePrevious != null ) thePrevious.invokeAsyncResponse(msg);
    } // end method invokeAsyncResponse

    /**
     * Basic null version of postProcessRequest - subclasses should override for any required
     * real processing 
     */
    public Message postProcessRequest(Message msg) {
    	// Default processing is to do nothing
    	return msg;
    } // end method postProcessRequest

    /**
     * Basic null version of postProcessRequest - subclasses should override for any required
     * real processing 
     * @throws Throwable 
     */
    public Message postProcessRequest(Message msg, Throwable e) throws Throwable {
    	// Default processing is to rethrow the exception
    	throw e;
    } // end method postProcessRequest

    
    /**
     * A testing method while I use the local SCA binding wire to look 
     * at how the async response path works. This allows me to detect the
     * point where the reference wire turns into the service with in the
     * optimized case
     * 
     * @return
     */
    public boolean isLocalSCABIndingInvoker() {
        return false;
    }
}
