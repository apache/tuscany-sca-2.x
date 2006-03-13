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
package org.apache.tuscany.container.js.rhino;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

public class RhinoTargetInvoker implements TargetInvoker {

    private ScopeContext container;

    private String serviceName;

    private Method method;

    private RhinoScript target;

    public RhinoTargetInvoker(String serviceName, Method method, ScopeContext container) {
        assert (serviceName != null) : "No service name specified";
        assert (container != null) : "No scope container specified";
        assert (method != null) : "No method specified";
        this.serviceName = serviceName;
        this.container = container;
        this.method = method;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
    	RhinoScript rhinoScript = getRhinoScript();
    	Object response = rhinoScript.invoke(method.getName(), payload, method.getReturnType(), null);
    	return response;
    }

    protected RhinoScript getRhinoScript() {
        RhinoScript rhinoScript;
        if (cacheable) {
            if (target == null) {
                target = (RhinoScript) container.getContext(serviceName).getImplementationInstance();
            }
            rhinoScript = target;
        } else {
            rhinoScript = (RhinoScript) container.getContext(serviceName).getImplementationInstance();
        }
        return rhinoScript;
    }
    
    private boolean cacheable;

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean val) {
        cacheable = val;
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
        throw new IllegalStateException("This interceptor must be the last interceptor in an interceptor chain");
    }

    public Object clone() {
        try {
            RhinoTargetInvoker invoker = (RhinoTargetInvoker) super.clone();
            invoker.container = this.container;
            invoker.cacheable = this.cacheable;
            invoker.serviceName = this.serviceName;
            invoker.method = this.method;
            invoker.target = null;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }
}
