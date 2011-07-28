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
package org.apache.tuscany.sca.sample.comet;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.sca.binding.comet.runtime.callback.CometCallback;
import org.apache.tuscany.sca.binding.comet.runtime.callback.Status;
import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

@Service(ChatService.class)
@Scope("COMPOSITE")
public class ChatServiceImpl implements ChatService {

	@Context
	protected ComponentContext context;

	private CopyOnWriteArrayList<CometCallback> clients = new CopyOnWriteArrayList<CometCallback>();

	@Override
	public void postMessage(String user, String message) {
		for (CometCallback callback : new ArrayList<CometCallback>(clients)) {
			Status status = callback.sendMessage(user + ": " + message);
			if (status == Status.CLIENT_DISCONNECTED) {
				clients.remove(callback);
			}
		}
	}

	@Override
	public void register() {
		// saving the callback object during register() method so it will push data back to the client 
		// using the callback method defined for the register operation in the js when used
		clients.add(context.getRequestContext().<CometCallback> getCallback());
	}
}
