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
package org.apache.tuscany.core.invocation;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.tuscany.core.config.JavaIntrospectionHelper;

/**
 * A HashMap keyed by method
 */
public class MethodHashMap extends HashMap {
    
    /**
     * Constructs a new MethodHashMap.
     */
    public MethodHashMap() {
        super();
    }
    
    /**
     * Constructs a new MethodHashMap.
     */
    public MethodHashMap(int size) {
        super(size);
    }
    
    /**
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public Object get(Object key) {
        Method method=(Method)key;
        //FIXME find a more efficient way to find a matching method
        Method closestMethod=JavaIntrospectionHelper.findClosestMatchingMethod(method.getName(), method.getParameterTypes(), super.keySet());
        return super.get(closestMethod);
    }

}
