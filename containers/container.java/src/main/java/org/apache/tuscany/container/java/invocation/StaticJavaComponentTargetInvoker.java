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

/**
 * Caches component instances that do not need to be resolved for every invocation, e.g. an invocation originating from
 * a lesser scope intended for a target with a wider scope
 * 
 * @version $Rev$ $Date$
 */
public class StaticJavaComponentTargetInvoker extends AbstractJavaComponentInvoker {

    private Object instance;

    public StaticJavaComponentTargetInvoker(Method operation, Object instance) {
        super(operation);
        assert (instance != null) : "Instance cannot be null";
        this.instance = instance;
    }

    protected Object getInstance() {
        return instance;
    }

    public boolean isCacheable() {
        return true;
    }

    public Object clone() {
        StaticJavaComponentTargetInvoker invoker = (StaticJavaComponentTargetInvoker) super.clone();
        invoker.instance = null;
        return invoker;
    }
}
