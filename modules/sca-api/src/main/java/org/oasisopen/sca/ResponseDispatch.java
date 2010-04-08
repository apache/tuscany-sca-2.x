/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

import java.util.Map;

/**
 * 
 * The following defines the ResponseDispatch interface, used to return a response
 * message asynchronously from a service implementation method.
 *
 * @param <T> the type of the Response message returned by the service implementation method
 */
public interface ResponseDispatch<T> {
	
   /**
	* Sends the response message from an asynchronous service method. 
	* This method can only be invoked once for a given ResponseDispatch object and cannot be invoked 
	* if sendFault has previously been invoked for the same ResponseDispatch object.
	* @param     res an instance of the response message returned by the service operation
	* @exception InvalidStateException if this method is called more than once for the same service
	*            operation.
	*/
   void sendResponse(T res);
   
   /**
    * Sends an exception as a fault from an asynchronous service method. 
    * This method can only be invoked once for a given ResponseDispatch object and cannot be invoked 
    * if sendResponse has previously been invoked for the same ResponseDispatch object.
    * @param     e an instance of an exception returned by the service operation
    * @exception InvalidStateException if this method is called more than once for the same service
	*            operation.
    */
   void sendFault(Throwable e);
   
   /**
    * Obtains the context object for the ResponseDispatch method
    * @return a Map which is is the context object for the ResponseDispatch object.
    * The invoker can update the context object with appropriate context information, prior to invoking 
    * either the sendResponse method or the sendFault method
    */
   Map<String, Object> getContext();
}

