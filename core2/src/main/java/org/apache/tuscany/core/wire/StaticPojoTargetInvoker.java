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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;

/**
 * Caches component instances that do not need to be resolved for every wire, e.g. an wire originating from a lesser
 * scope intended for a target with a wider scope
 *
 * @version $Rev$ $Date$
 */
public class StaticPojoTargetInvoker extends PojoTargetInvoker {

    private Object instance;

    public StaticPojoTargetInvoker(Method operation, Object instance) {
        super(operation);
        assert instance != null : "Instance cannot be null";
        this.instance = instance;
    }

    protected Object getInstance() {
        return instance;
    }

    public StaticPojoTargetInvoker clone() throws CloneNotSupportedException {
        StaticPojoTargetInvoker invoker = (StaticPojoTargetInvoker) super.clone();
        invoker.instance = null;
        return invoker;
    }
}
