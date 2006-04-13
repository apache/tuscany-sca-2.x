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
package org.apache.tuscany.core.context;

/**
 * A runtime entity that manages an atomic (i.e. leaf-type) artifact.
 * 
 * @version $Rev$ $Date$
 */
public interface AtomicContext extends Context {

    /**
     * Returns whether a the context should be eagerly initialized
     */
    public boolean isEagerInit();

    /**
     * Notifies the context of an initialization event
     * @throws TargetException
     */
    public void init() throws TargetException;

    /**
     * Notifies the context of a destroy event
     * @throws TargetException
     */
    public void destroy() throws TargetException;

    /**
     * Returns whether a the context should be called back when its scope ends
     */
    public boolean isDestroyable();

    /**
     * Returns the target instance associated with the context. A target instance is the actual
     * object a request is dispatched to sans proxy invocation chain.
     * @throws TargetException
     */
    public Object getTargetInstance() throws TargetException;

}
