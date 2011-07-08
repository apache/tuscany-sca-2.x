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
import org.apache.tuscany.sca.binding.comet.runtime.callback.Status;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometSessionManager;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.core.invocation.impl.MessageImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.atmosphere.cpr.Broadcaster;

/**
 * Receives callback invocations and sends messages back to the browser.
 */
public class CometCallbackInvoker implements Invoker {

    protected Operation operation;
    protected EndpointReference endpoint;

    public CometCallbackInvoker(final Operation operation, final EndpointReference endpoint) {
        this.operation = operation;
        this.endpoint = endpoint;
    }

    /**
     * Sends message back to the browser client and reports connection status to
     * the runtime.
     * 
     * @param msg
     *            message to send to the browser
     * @return the connection status
     */
    @Override
    public Message invoke(final Message msg) {
        String sessionId = (String) msg.getHeaders().get(Constants.RELATES_TO);
        Broadcaster broadcaster = CometSessionManager.get(sessionId);
        Message response = new MessageImpl();
        if (broadcaster == null) {
            response.setBody(Status.CLIENT_DISCONNECTED);
        } else {
            String callbackMethod = msg.getTo().getURI();
            Object[] body = msg.getBody();
            broadcaster.broadcast(callbackMethod + "($.secureEvalJSON('" + JSONUtil.encodeResponse(body[0]) + "'))");
            response.setBody(Status.OK);
        }
        return response;
    }

}
