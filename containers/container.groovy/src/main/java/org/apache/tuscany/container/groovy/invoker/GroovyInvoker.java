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
package org.apache.tuscany.container.groovy.invoker;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.container.groovy.context.GroovyAtomicContext;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Groovy target invoker.
 */
public class GroovyInvoker implements TargetInvoker, Cloneable {

    private GroovyAtomicContext context;
    private String serviceName;
    private String methodName;
    private boolean cacheable;

    /**
     * Initializes the invoker.
     *
     * @param serviceName Service name.
     * @param context     SCope context.
     * @param methodName  Method name.
     */
    public GroovyInvoker(String serviceName, String methodName, GroovyAtomicContext context) {
        this.context = context;
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * Invokes the target.
     */
    public Object invokeTarget(Object payload) throws InvocationTargetException {

        GroovyScript groovyScript = context.getScript();
        Object[] args = (Object[]) payload;
        try {
            return groovyScript.runScript(methodName, args);
        } catch (Exception ex) {
            throw new InvocationTargetException(ex);
        }
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

    public GroovyInvoker clone() throws CloneNotSupportedException {
        return (GroovyInvoker) super.clone();
    }


}
