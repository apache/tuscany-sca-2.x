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

package org.apache.tuscany.sca.binding.comet.runtime.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.comet.runtime.CometComponentContext;
import org.apache.tuscany.sca.binding.comet.runtime.CometMessageContext;
import org.apache.tuscany.sca.binding.comet.runtime.ServletFactory;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointReferenceImpl;
import org.apache.tuscany.sca.core.invocation.impl.MessageImpl;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.jersey.SuspendResponse;

import com.google.gson.Gson;
import com.sun.jersey.spi.container.servlet.PerSession;

/**
 * Class serving calls coming for comet services and operations.
 */
@Path("/")
@Produces("text/html;charset=ISO-8859-1")
@PerSession
public class CometBindingHandler {

	/**
	 * The object used to suspend the response and send async responses back to
	 * client.
	 */
	private Broadcaster broadcaster;

	/**
	 * JSON converter.
	 */
	private Gson gson = new Gson();

	/**
	 * The underlying servlet context.
	 */
	@Context
	private ServletContext sc;

	@Context
	private HttpServletRequest request;

	private CometComponentContext context;

	/**
	 * Method called at comet connect time. This suspends the response and keeps
	 * the connection opened.
	 * 
	 * @return the suspended response
	 */
	@GET
	public SuspendResponse<String> connect() {
		System.out.println("-- connect -- Session Id: " + request.getSession().getId());
		if (broadcaster == null) {
			broadcaster = new DefaultBroadcaster();
			context = (CometComponentContext) sc.getAttribute(ServletFactory.COMET_COMPONENT_CONTEXT_KEY);
		}
		return new SuspendResponse.SuspendResponseBuilder<String>().broadcaster(this.broadcaster).outputComments(true)
				.build();
	}

	/**
	 * Method called on service calls.
	 * 
	 * @param service
	 *            service called
	 * @param method
	 *            operation called
	 * @param callbackMethod
	 *            the callback method from Javascript
	 * @param jsonData
	 *            arguments for the method sent as JSON array
	 * @return object used by the Broadcaster to send response through the
	 *         persisted connection
	 * @throws InvocationTargetException
	 *             if problems occur at service invocation
	 */
	@POST
	@Path("/{service}/{method}")
	@Broadcast
	public void handleRequest(@PathParam("service") final String service, @PathParam("method") final String method,
			@FormParam("callback") final String callbackMethod, @FormParam("params") final String jsonData)
			throws InvocationTargetException {
		System.out.println("-- handleRequest -- Session Id: " + request.getSession().getId());
		final String url = "/" + service + "/" + method;
		final RuntimeEndpoint wire = context.getEndpoint(url);
		final Operation operation = context.getOperation(url);

		final Object[] args = decodeJsonDataForOperation(jsonData, operation);
		Message msg = createMessageWithMockedCometReference(args, callbackMethod);
		Object response = wire.invoke(operation, args);
		System.out.println("Response: " + gson.toJson(response));
	}

	/**
	 * Convert request parameters from JSON to operation parameter types.
	 * 
	 * @param jsonData
	 * @param operation
	 * @return
	 */
	private Object[] decodeJsonDataForOperation(String jsonData, Operation operation) {
		Object[] args = new Object[operation.getInputType().getLogical().size()];
		final String[] json = this.parseArray(jsonData);
		int index = 0;
		// convert each argument to the corresponding class
		for (final DataType<?> dataType : operation.getInputType().getLogical()) {
			args[index] = this.gson.fromJson(json[index], dataType.getPhysical());
			index++;
		}
		return args;
	}

	/**
	 * Creates the message to be sent with a mocked EndpointReference in the
	 * 'from' field as the request comes from a browser (there is no actual
	 * comet reference running in a controlled environment).
	 * 
	 * @param args
	 * @param callbackMethod
	 * @return
	 */
	private Message createMessageWithMockedCometReference(Object[] args, String callbackMethod) {
		Message msg = new MessageImpl();
		msg.setBody(args);
		CometMessageContext messageContext = new CometMessageContext(this, callbackMethod);
		msg.setBindingContext(messageContext);
		EndpointReference re = new RuntimeEndpointReferenceImpl();
		re.setCallbackEndpoint(new RuntimeEndpointImpl());
		msg.setFrom(re);
		return msg;
	}

	/**
	 * Parse the JSON array containing the arguments for the method call in
	 * order to avoid converting JSON to Object[]. Converting each object
	 * separately to it's corresponding type avoids type mismatch problems at
	 * service invocation.
	 * 
	 * @param jsonArray
	 *            the JSON array
	 * @return an array of JSON formatted objects
	 */
	private String[] parseArray(final String jsonArray) {
		final List<String> objects = new ArrayList<String>();
		int bracketNum = 0;
		int parNum = 0;
		int startPos = 1;
		for (int i = 0; i < jsonArray.length(); i++) {
			switch (jsonArray.charAt(i)) {
			case '{':
				bracketNum++;
				break;
			case '}':
				bracketNum--;
				break;
			case '[':
				parNum++;
				break;
			case ']':
				parNum--;
				break;
			case ',':
				if ((bracketNum == 0) && (parNum == 1)) {
					objects.add(jsonArray.substring(startPos, i));
					startPos = i + 1;
				}
			}
		}
		// add last object
		objects.add(jsonArray.substring(startPos, jsonArray.length() - 1));
		return objects.toArray(new String[] {});
	}

	public void respondToClient(String callbackMethod, Object response) {
		broadcaster.broadcast(callbackMethod + "($.secureEvalJSON('" + this.gson.toJson(response) + "'))");
	}

	public boolean isClientConnected() {
		return !broadcaster.getAtmosphereResources().isEmpty();
	}

}
