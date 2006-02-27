/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.mock.binding.foo;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

/**
 * Responsible for invoking a mock transport binding client configured for an external service
 * 
 * @version $Rev$ $Date$
 */
public class FooESTargetInvoker implements TargetInvoker {

    private String name;

    private ScopeContext container;

    private ExternalServiceContext context;

    public FooESTargetInvoker(String esName, ScopeContext container) {
        assert (esName != null) : "No external service name specified";
        assert (container != null) : "No scope container specified";
        name = esName;
        this.container = container;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        if (context == null) {
            InstanceContext iContext = container.getContext(name);
            if (!(iContext instanceof ExternalServiceContext)) {
                TargetException te = new TargetException("Unexpected target context type");
                te.setIdentifier(iContext.getClass().getName());
                te.addContextName(iContext.getName());
                throw te;
            }
            context = (ExternalServiceContext) iContext;
        }
        FooClient client = (FooClient) context.getImplementationInstance(true);
        if (payload != null) {
            return client.invoke(payload);
        } else {
            return client.invoke(null);
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

}
