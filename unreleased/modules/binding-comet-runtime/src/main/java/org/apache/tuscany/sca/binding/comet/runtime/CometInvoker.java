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

package org.apache.tuscany.sca.binding.comet.runtime;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.comet.runtime.handler.CometBindingHandler;
import org.apache.tuscany.sca.core.invocation.impl.MessageImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

public class CometInvoker implements Invoker {

	protected Operation operation;
	protected EndpointReference endpoint;

	public CometInvoker(final Operation operation, final EndpointReference endpoint) {
		this.operation = operation;
		this.endpoint = endpoint;
	}

	@Override
	public Message invoke(final Message msg) {
		String operation = msg.getOperation().getName();
		CometMessageContext context = msg.getBindingContext();
		CometBindingHandler handler = context.getCometHandler();
		Message message = new MessageImpl();
		if (operation.equals("sendResponse")) {
			String callbackMethod = context.getCallbackMethod();
			Object[] body = msg.getBody();
			handler.respondToClient(callbackMethod, body[0]);
		} else if (operation.equals("isClientConnected")) {
			message.setBody(handler.isClientConnected());
		}
		return message;
	}

}
