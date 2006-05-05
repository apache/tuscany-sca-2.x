/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.TargetInvoker;

/**
 * Responsible for invoking an external service
 * 
 * @version $Rev$ $Date$
 */
public class ExternalServiceTargetInvoker implements TargetInvoker {

    private QualifiedName serviceName;

    private String esName;

    private Method method;

    private ScopeContext container;

    private ExternalServiceContext context;

    /**
     * Constructs a new ExternalWebServiceTargetInvoker.
     * 
     * @param container
     */
    public ExternalServiceTargetInvoker(QualifiedName serviceName, Method method, ScopeContext container) {
        assert serviceName != null : "No service name specified";
        assert method != null : "No method specified";
        assert container != null : "No scope container specified";
        this.serviceName = serviceName;
        this.esName = serviceName.getPartName();
        this.method = method;
        this.container = container;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        if (context == null) {
            Context iContext = container.getContext(esName);
            if (!(iContext instanceof ExternalServiceContext)) {
                TargetException te = new TargetException("Unexpected target context type");
                te.setIdentifier(iContext.getClass().getName());
                te.addContextName(iContext.getName());
                throw te;
            }
            context = (ExternalServiceContext) iContext;
        }

        ExternalServiceInvoker invoker = (ExternalServiceInvoker) context.getHandler();
        if (payload != null) {
            return doInvoke(invoker, (Object[]) payload);
        } else {
            return doInvoke(invoker, null);
        }
    }

    protected Object doInvoke(ExternalServiceInvoker invoker, Object[] args) {
        return invoker.invoke(method.getName(), args);
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

    public Object clone() throws CloneNotSupportedException {
        try {
            ExternalServiceTargetInvoker invoker = (ExternalServiceTargetInvoker) super.clone();
            invoker.container = container;
            invoker.context = this.context;
            invoker.esName = this.esName;
            invoker.method = this.method;
            invoker.serviceName = this.serviceName;
            return invoker;
        } catch (CloneNotSupportedException e) {
            // will not happen
            return null;
        }
    }

}
