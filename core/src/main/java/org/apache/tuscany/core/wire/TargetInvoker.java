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

import java.lang.reflect.InvocationTargetException;

/**
 * Implementations are responsible for resolving a target and performing the actual wire on it, for example, a
 * service component implementation instance or an external service client.
 * 
 * @version $Rev$ $Date$
 */
public interface TargetInvoker extends Interceptor, Cloneable{

    /**
     * Responsible for invoking an operation on a target with the given payload
     * 
     * @param payload the parameters of the target operation or null
     * @throws InvocationTargetException if the target operation itself throws an exception. The root cause will be set
     *         to that exception
     */
    public Object invokeTarget(Object payload) throws InvocationTargetException;

    /**
     * Determines whether the proxy can be cached on the client/source side
     */
    public boolean isCacheable();
    
    public Object clone() throws CloneNotSupportedException;
}
