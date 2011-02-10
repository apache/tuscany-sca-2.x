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
package org.apache.tuscany.sca.itest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.annotation.*;


/**
 * Java component implementation
 * 1 service with interface Service1AsyncServer
 * 0 references
 *
 * Async server implementation of the Service1 service
 *
 */
@Service(Service1AsyncServer.class)
public class Service1AsyncServerImpl implements Service1AsyncServer {
	
	private volatile ResponseDispatch<String> responseHandler = null;
	
	private volatile String tmpFilePath = null;
	
	@Property(required=true)
	public volatile String serviceName = "service1";
	
	/*
	public String operation1(String input) {
		return serviceName + " operation1 invoked";
	}
	*/

	public void operation1Async(String input, ResponseDispatch<String> handler) {
		// Store the ResponseDispatch object
		responseHandler = handler;
		
		serializeResponseHandler(responseHandler);
		
		// Now kick off processing on a separate thread that will dispatch the response some time after this
		// initial method returns...
		runResponseThread( input );
		
		// return
		return;
	} // end method operation1Async
	
	/**
	 * Serialize the response handler to a file
	 * @param responseHandler2
	 */
	private void serializeResponseHandler(
			ResponseDispatch<String> responseHandler2) {
		if( responseHandler2 instanceof Serializable ) {
			Serializable obj = (Serializable)responseHandler2;
	        FileOutputStream fos;
			try {
				File tmpFile = File.createTempFile("Async_Server", null);
				tmpFilePath = tmpFile.getCanonicalPath();
				fos = new FileOutputStream(tmpFile);
	            ObjectOutputStream oos = new ObjectOutputStream(fos);
			
	            oos.writeObject(obj);

	            oos.close();
		    } catch (FileNotFoundException e) {
		    } catch (IOException e) {
		    	e.printStackTrace();
		    } // end try
	 		
        } // end if
	} // end method serializeResponseHandler
	
	@SuppressWarnings("unchecked")
	public ResponseDispatch<String> deserializeResponseHandler() {
		try {
			if( tmpFilePath == null ) return null;
	        FileInputStream fis = new FileInputStream( tmpFilePath );
	        ObjectInputStream ois = new ObjectInputStream(fis);
	
	        ResponseDispatch<String> responseDispatch = (ResponseDispatch<String>) ois.readObject();
	        
	        ois.close();
	        return responseDispatch;
		} catch (Exception e) {
			e.printStackTrace();
		} // end try
 
		return null;
	} // end method deserializeResponseHandler

	/**
	 * Method used to run a separate thread, to invoke the ResponseDispatch
	 */
	private void runResponseThread( String input ) {
		
		int invocationCount = 2;	// # of threads to use
		long maxWaitTime = 5000;	// Max wait time for completion = 5sec
		
		// Run the tests using a ThreadPoolExecutor
		ThreadPoolExecutor theExecutor = new ThreadPoolExecutor( 	invocationCount, invocationCount,
													                maxWaitTime, TimeUnit.MILLISECONDS,
													                new ArrayBlockingQueue<Runnable>( invocationCount ) );
		

		
		// Perform the invocations on separate thread...
		theExecutor.execute( new separateThreadInvoker( input ) );

	} // end method runResponseThread

	/**
	 * An inner class which acts as a runnable task for invoking APIs on threads that are not processing
	 * either a service operation or a callback operation
	 */
	private class separateThreadInvoker implements Runnable {
		
		private long pauseTime = 1000;		// Pause interval to allow initiating thread to complete
		private String input;				// Input parameter
				
		public separateThreadInvoker( String input ) {
			super();
			this.input = input;
		} // end constructor

		public void run() {

			// Wait for a short time to ensure that the invoking thread has time to return
			try {
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {
				// Nothing to do here...
			} // end try
			
			ResponseDispatch<String> responseHandler2 = deserializeResponseHandler();
			if( responseHandler2 != null ) {
				responseHandler = responseHandler2;
			} // end if
			
			if( "exception".equals(input) ) {
				// Invoke the response dispatch object to return a an exception
				responseHandler.sendFault( new BusinessFault1(serviceName + " operation1 invoked asynchronously"));

			} else {
				// Invoke the response dispatch object to return a response
				responseHandler.sendResponse( serviceName + " operation1 invoked asynchronously");
			} // end if 
		} // end method run
		
	} // end class separateThreadInvoker

}
