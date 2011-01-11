package org.apache.tuscany.sca.core.invocation;

import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;

public interface JDKAsyncResponseInvoker extends InvokerAsyncResponse {
	
	/**
	 * Registers an Async response, which provides an ID which identifies a given response
	 * and an object which can handle the response
	 * @param id - the ID
	 * @param responseHandler - the response handler object
	 */
	public void registerAsyncResponse( String id, Object responseHandler );

} // end interface JDKAsyncResponseInvoker
