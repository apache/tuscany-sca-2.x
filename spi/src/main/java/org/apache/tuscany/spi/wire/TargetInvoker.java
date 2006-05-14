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
package org.apache.tuscany.spi.wire;

import java.lang.reflect.InvocationTargetException;

/**
 * Implementations are responsible for resolving a target and performing the actual invocation on it, for
 * example, a service component implementation instance or an external service client.
 *
 * @version $Rev: 395162 $ $Date: 2006-04-19 01:07:36 -0700 (Wed, 19 Apr 2006) $
 */
public interface TargetInvoker extends Cloneable {

    /**
     * Responsible for invoking an operation on a target with the given message
     */
    public Object invokeTarget(Object payload) throws InvocationTargetException;

    /**
     * Determines whether the proxy can be cached on the client/source side
     */
    public boolean isCacheable();

    /**
     * Sets whether the target service instance may be cached by the invoker. This is a possible optimization
     * when a wire is configured for a "down-scope" reference, i.e. a reference from a source of a shorter
     * lifetime to a source of greater lifetime.
     */
    public void setCacheable(boolean cacheable);

    /**
     * Implementations must support deep cloning
     */
    public Object clone() throws CloneNotSupportedException;
}
