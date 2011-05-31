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

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometEndpointManager;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometOperationManager;
import org.apache.tuscany.sca.binding.comet.runtime.manager.CometSessionManager;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointReferenceImpl;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.core.invocation.impl.MessageImpl;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.atmosphere.jersey.SuspendResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Handles requests for comet services and for creating a persistent connection.
 */
@Path("/")
public class CometBindingHandler {

    /**
     * JSON converter
     */
    private static Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * Suspends the current HTTP connection.
     * 
     * @param sessionId
     *            session id to identify client
     * @return a response that is not committed, just flushed
     */
    @GET
    @Path("/connect")
    public SuspendResponse<String> connect(@QueryParam("sessionId") String sessionId) {
        Broadcaster broadcaster = CometSessionManager.get(sessionId);
        if (broadcaster == null) {
            broadcaster = new JerseyBroadcaster(sessionId);
            CometSessionManager.add(sessionId, broadcaster);
        }
        return new SuspendResponse.SuspendResponseBuilder<String>().broadcaster(broadcaster).outputComments(true)
                .build();
    }

    /**
     * Handles requests for service operations.
     * 
     * @param service
     *            the service to invoke
     * @param method
     *            the method to invoke
     * @param sessionId
     *            the client session id
     * @param callbackMethod
     *            the callbackMethod to invoke once a response is available
     * @param jsonData
     *            method arguments sent by the client in JSON format
     * @throws InvocationTargetException
     *             if a problem occurs while invoking the service implementation
     */
    @POST
    @Path("/{service}/{method}")
    public void handleRequest(@PathParam("service") String service, @PathParam("method") String method,
            @FormParam("sessionId") String sessionId, @FormParam("callbackMethod") String callbackMethod,
            @FormParam("params") String jsonData) throws InvocationTargetException {
        String url = "/" + service + "/" + method;
        RuntimeEndpoint wire = CometEndpointManager.get(url);
        Operation operation = CometOperationManager.get(url);

        final Object[] args = decodeJsonDataForOperation(jsonData, operation);
        Message msg = createMessageWithMockedCometReference(args, sessionId, callbackMethod);
        boolean isVoidReturnType = operation.getOutputType().getLogical().isEmpty();
        if (!isVoidReturnType) {
            Object response = wire.invoke(operation, args);
            Broadcaster broadcaster = CometSessionManager.get(sessionId);
            broadcaster.broadcast(callbackMethod + "($.secureEvalJSON('" + gson.toJson(response) + "'))");
            if (broadcaster.getAtmosphereResources().isEmpty()) {
                CometSessionManager.remove(sessionId);
            }
        } else {
            wire.invoke(operation, msg);
        }
    }

    /**
     * Creates a message with a mocked EndpointReference in the 'from' field to
     * simulate a comet reference (because requests are coming from browsers).
     * This is needed by the callback mechanism to have a source for the
     * request.
     * 
     * @param args
     *            arguments for the method invocation
     * @param sessionId
     *            the session id of the client
     * @param callbackMethod
     *            method to call once a response is available
     * @return an invocation message
     */
    private Message createMessageWithMockedCometReference(Object[] args, String sessionId, String callbackMethod) {
        Message msg = new MessageImpl();
        msg.getHeaders().put(Constants.MESSAGE_ID, sessionId);
        msg.setBody(args);
        EndpointReference re = new RuntimeEndpointReferenceImpl();
        RuntimeEndpointImpl callbackEndpoint = new RuntimeEndpointImpl();
        callbackEndpoint.setURI(callbackMethod);
        re.setCallbackEndpoint(callbackEndpoint);
        msg.setFrom(re);
        return msg;
    }

    /**
     * Convert request parameters from JSON to operation parameter types.
     * 
     * @param jsonData
     *            parameters in JSON array format
     * @param operation
     *            the operation to invoke
     * @return an array of objects
     */
    private Object[] decodeJsonDataForOperation(String jsonData, Operation operation) {
        Object[] args = new Object[operation.getInputType().getLogical().size()];
        final String[] json = this.parseArray(jsonData);
        int index = 0;
        for (final DataType<?> dataType : operation.getInputType().getLogical()) {
            args[index] = gson.fromJson(json[index], dataType.getPhysical());
            index++;
        }
        return args;
    }

    /**
     * Split the JSON array containing the arguments for the method call in
     * order to avoid converting JSON to Object[]. Converting each object
     * separately to it's corresponding type avoids type mismatch problems at
     * service invocation.
     * 
     * @param jsonArray
     *            the JSON array
     * @return an array of JSON formatted strings
     */
    private String[] parseArray(String jsonArray) {
        List<String> objects = new ArrayList<String>();
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
        objects.add(jsonArray.substring(startPos, jsonArray.length() - 1));
        return objects.toArray(new String[] {});
    }

}
