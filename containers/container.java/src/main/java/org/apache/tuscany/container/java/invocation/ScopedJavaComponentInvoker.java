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
package org.apache.tuscany.container.java.invocation;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.TargetException;

/**
 * Uses a scope container to resolve an implementation instance based on the current thread context
 *
 * @version $Rev$ $Date$
 */
public class ScopedJavaComponentInvoker extends AbstractJavaComponentInvoker {

    private AtomicContext container;
    private Object target;
    public boolean cacheable;


    /**
     * Creates a new invoker
     *
     * @param operation    the operation the invoker is associated with
     * @param scopeContext the scope context the component is resolved in
     */
    public ScopedJavaComponentInvoker(Method operation, AtomicContext scopeContext) {
        super(operation);
        assert (scopeContext != null) : "No scope scopeContext specified";
        this.container = scopeContext;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected Object getInstance() throws TargetException {
        if (!cacheable) {
            return container.getTargetInstance();
        } else {
            if (target == null) {
                target = container.getTargetInstance();
            }
            return target;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        ScopedJavaComponentInvoker invoker = (ScopedJavaComponentInvoker) super.clone();
        invoker.target = null;
        invoker.cacheable = this.cacheable;
        invoker.container = this.container;
        return invoker;
    }
}
