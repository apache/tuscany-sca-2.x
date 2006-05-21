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
package org.apache.tuscany.container.java.wire;

import java.lang.reflect.Method;

import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.wire.AbstractJavaTargetInvoker;
import org.apache.tuscany.spi.context.TargetException;

/**
 * Uses a scope context to resolve an implementation instance based on the current thread context
 *
 * @version $Rev$ $Date$
 */
public class JavaTargetInvoker extends AbstractJavaTargetInvoker {

    private JavaAtomicContext context;
    private Object target;
    public boolean cacheable;


    /**
     * Creates a new invoker
     *
     * @param operation the operation the invoker is associated with
     * @param context   the scope context the component is resolved in
     */
    public JavaTargetInvoker(Method operation, JavaAtomicContext context) {
        super(operation);
        assert (context != null) : "No atomic context specified";
        this.context = context;
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
            return context.getTargetInstance();
        } else {
            if (target == null) {
                target = context.getTargetInstance();
            }
            return target;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        JavaTargetInvoker invoker = (JavaTargetInvoker) super.clone();
        invoker.target = null;
        invoker.cacheable = this.cacheable;
        invoker.context = this.context;
        return invoker;
    }
}
