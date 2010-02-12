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
package org.apache.tuscany.sca.binding.ws.axis2.transport;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Use an Axis2 OperationClient to invoke a remote web service
 *
 * @version $Rev$ $Date$
 */
public class TransportReferenceInterceptor implements Interceptor {
    
    private Invoker next;    
    
    public TransportReferenceInterceptor() {
    }
    
    public Message invoke(Message msg) {
        try {
            Object resp = null; 
            
            if (msg.getOperation().isNonBlocking()) {
                resp = invokeTargetOneWay(msg);
            } else {
                resp = invokeTarget(msg);
            }
            
            msg.setBody(resp);
        } catch (AxisFault e) {
            if (e.getDetail() != null ) {
                FaultException f = new FaultException(e.getMessage(), e.getDetail(), e);
                f.setFaultName(e.getDetail().getQName());
                msg.setFaultBody(f);
            } else {
                msg.setFaultBody(e);
            }
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }

        return msg;
    }

    protected Object invokeTarget(Message msg) throws AxisFault {
        final OperationClient operationClient = msg.getBindingContext();

        // ensure connections are tracked so that they can be closed by the reference binding
        MessageContext requestMC = operationClient.getMessageContext("Out");
        requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
        requestMC.getOptions().setTimeOutInMilliSeconds(240000L); 
              
        
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws AxisFault {
                    operationClient.execute(true);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            operationClient.complete(requestMC);
            throw (AxisFault)e.getException();
        }

        MessageContext responseMC = operationClient.getMessageContext("In");
                
        OMElement response = responseMC.getEnvelope().getBody().getFirstElement();

        // FIXME: [rfeng] We have to pay performance penalty to build the complete OM as the operationClient.complete() will
        // release the underlying HTTP connection. 
        // Force the response to be populated, see https://issues.apache.org/jira/browse/TUSCANY-1541
        if (response != null) {
            response.build();
        }

        operationClient.complete(requestMC);

        return response;
    }
    
    protected Object invokeTargetOneWay(Message msg) throws AxisFault {
        OperationClient operationClient = msg.getBindingContext();

        // ensure connections are tracked so that they can be closed by the reference binding
        MessageContext requestMC = operationClient.getMessageContext("Out");
        //requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
        Options opt = requestMC.getOptions();
        opt.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
        opt.setUseSeparateListener(true);
        opt.setProperty(HTTPConstants.AUTO_RELEASE_CONNECTION,Boolean.TRUE);        

        operationClient.execute(false);

        // REVIEW it seems ok to return null
        return null;
    }
    
    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }    
}
