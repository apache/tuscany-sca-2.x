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

import org.apache.tuscany.core.config.JavaIntrospectionHelper;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * A Map implementation that performs a lookup on a collection of methods by method name. This implementation is used to
 * map methods on one interface to compatible methods on another interface, for example, when flowing an wire from
 * a proxy injected on a source reference to a target service instance. 
 *
 * @version $Rev$ $Date$
 */
public class MethodHashMap<T extends InvocationConfiguration> extends HashMap<Method, T> {

    public MethodHashMap() {
        super();
    }

    public MethodHashMap(int size) {
        super(size);
    }

    /**
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public T get(Object key) {
        if (key instanceof Method) {
            Method m = (Method) key;
            //FIXME find a more efficient way to find a matching method
            Method closestMethod = JavaIntrospectionHelper.findClosestMatchingMethod(m.getName(), m.getParameterTypes(), super.keySet());
            return super.get(closestMethod);
        } else {
            throw new IllegalArgumentException("Key must be a method");
        }
    }

}
