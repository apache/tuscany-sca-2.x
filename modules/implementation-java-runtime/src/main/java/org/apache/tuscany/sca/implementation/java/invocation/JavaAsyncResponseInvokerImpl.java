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
package org.apache.tuscany.sca.implementation.java.invocation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tuscany.sca.core.invocation.AsyncFaultWrapper;
import org.apache.tuscany.sca.core.invocation.AsyncResponseHandler;
import org.apache.tuscany.sca.core.invocation.JDKAsyncResponseInvoker;
import org.apache.tuscany.sca.invocation.Message;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Class which handles the asynchronous response message from an async service back to the client Java component
 * 
 * This class provides a registration function which permits the reference invoking code to register the Future
 * which is used to return the response to the Java component code
 */
public class JavaAsyncResponseInvokerImpl implements JDKAsyncResponseInvoker {
	
    // Map used to link between async requests and async responses
    private ConcurrentMap<String, Object> asyncMessageMap;

	public JavaAsyncResponseInvokerImpl() {

	    asyncMessageMap = new ConcurrentHashMap<String, Object>();
	} // end constructor
	
	/**
	 * Deal with the asynchronous response message
	 * @param msg - the response message
	 * 
	 * The response message must contain a RELATES_TO id, which is used to identify the Future that represents
	 * the operation yet to complete.  The Future is then completed with the content of the message.
	 * The Future either calls back to the application code, or it is expected that the application is polling
	 * the Future for its completion.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void invokeAsyncResponse(Message msg) {
		// Obtain the Message ID for this message
		String relatesID = getMessageRelatesID( msg );
		if( relatesID == null ) 
			throw new ServiceRuntimeException("JavaAsyncResponseInvoker - response message has no RELATES_TO id");
		
		// Look up the response object & remove it from the Map
		Object responseHandler = asyncMessageMap.remove(relatesID);
		
		if( responseHandler == null ) 
			throw new ServiceRuntimeException("JavaAsyncResponseInvoker - no Future matches the RELATES_TO id: " + relatesID);
		
		// Invoke the response handler with the content of the message 
		// - in the case of a Java implementation, the response handler is a Future...
		AsyncResponseHandler future = (AsyncResponseHandler) responseHandler;
		
		Object payload = msg.getBody();
		Object response;
		if( payload == null ) {
			System.out.println("Returned response message was null");
		} else {
            if (payload.getClass().isArray()) {
                response = ((Object[])payload)[0];
            } else {
                response = payload;
            } // end if
            if( response.getClass().equals(AsyncFaultWrapper.class)) {
            	future.setWrappedFault((AsyncFaultWrapper) response );
            } else if ( response instanceof Throwable ) {
            	future.setFault( (Throwable)response );
            } else {
            	future.setResponse(response);
            } // end if
		} // end if

	} // end method invokeAsyncResponse
	
	/**
	 * Registers an Async response, which provides an ID which identifies a given response
	 * and an object which can handle the response
	 * @param id - the ID
	 * @param responseHandler - the response handler object
	 */
	public void registerAsyncResponse( String id, Object responseHandler ) {
		// Add the ID/response handler mapping into the table
		if( id != null && responseHandler != null ) asyncMessageMap.put(id, responseHandler);
	} // end method registerAsyncResponse

	/**
	 * Returns the registered async response for a given ID
	 * @param id - the ID
	 * @return responseHandler - the response handler object
	 */
	public Object getAsyncResponse( String id ) {
	    return asyncMessageMap.get(id);
	}
	
	/**
	 * Extracts the RELATES_TO header from the message
	 * @param msg - the Tuscany message
	 * @return - the value of the RELATES_TO header as a String
	 */
	private String getMessageRelatesID( Message msg ) {
		return (String)msg.getHeaders().get("RELATES_TO");
	} // end method getMessageRelatesID

} // end class JavaAsyncResponseInvoker
