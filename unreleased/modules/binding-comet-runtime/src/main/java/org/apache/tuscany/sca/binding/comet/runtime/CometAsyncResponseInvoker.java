package org.apache.tuscany.sca.binding.comet.runtime;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

public class CometAsyncResponseInvoker implements InvokerAsyncResponse {

	private RuntimeEndpoint endpoint;

	public CometAsyncResponseInvoker(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void invokeAsyncResponse(Message msg) {
		System.out.println("In invokeAsyncResponse!");
	}

}
