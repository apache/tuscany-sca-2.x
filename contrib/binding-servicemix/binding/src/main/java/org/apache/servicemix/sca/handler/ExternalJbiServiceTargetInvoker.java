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
package org.apache.servicemix.sca.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

public class ExternalJbiServiceTargetInvoker implements TargetInvoker {

    private QualifiedName serviceName;
    private String esName;
    private Method method;
    private ScopeContext container;

    private ExternalServiceContext context;

    /**
     * Constructs a new ExternalJbiServiceTargetInvoker.
     * @param esName
     * @param container
     */
    public ExternalJbiServiceTargetInvoker(QualifiedName serviceName, Method method, ScopeContext container) {
        assert (serviceName != null) : "No service name specified";
        assert (method != null) : "No method specified";
        assert (container != null) : "No scope container specified";
        this.serviceName = serviceName;
        this.esName = serviceName.getPartName();
        this.method = method;
        this.container = container;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        if (context == null) {
            InstanceContext iContext = container.getContext(esName);
            if (!(iContext instanceof ExternalServiceContext)) {
                TargetException te = new TargetException("Unexpected target context type");
                te.setIdentifier(iContext.getClass().getName());
                te.addContextName(iContext.getName());
                throw te;
            }
            context = (ExternalServiceContext) iContext;
        }
        ExternalJbiServiceClient client = (ExternalJbiServiceClient) context.getImplementationInstance(true);
        if (payload != null) {
            return client.invoke(method, (Object[])payload);
        } else {
            return client.invoke(method, null);
        }
    }

    public boolean isCacheable() {
        return false;
    }

    public Message invoke(Message msg) {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    public void setNext(Interceptor next) {
        throw new UnsupportedOperationException();
    }

    public Object clone() {
        try {
            ExternalJbiServiceTargetInvoker invoker = (ExternalJbiServiceTargetInvoker) super.clone();
            invoker.container = container;
            invoker.context = this.context;
            invoker.esName = this.esName;
            invoker.method = this.method;
            invoker.serviceName = this.serviceName;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }  
    
}
