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

import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.TargetInvoker;

import java.lang.reflect.InvocationTargetException;

/**
 * Responsible for invoking a mock transport binding client configured for an external service over a wire
 *
 * @version $Rev$ $Date$
 */
public class FooExternalServiceTargetInvoker implements TargetInvoker {

    private String name;
    private ExternalServiceContext context;

    public FooExternalServiceTargetInvoker(String esName) {
        assert (esName != null) : "No external service name specified";
        name = esName; // name is not used; it is included for illustration
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        FooClient client = new FooClient();
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

    public Object clone() throws CloneNotSupportedException {
        try {
            FooExternalServiceTargetInvoker invoker = (FooExternalServiceTargetInvoker) super.clone();
            invoker.context = this.context;
            invoker.name = this.name;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }

}
