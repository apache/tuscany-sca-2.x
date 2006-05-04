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
package org.apache.tuscany.spi.context;

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.model.Scope;
import sun.security.jca.GetInstance;


/**
 * Manages the lifecycle and visibility of instances associated with a set of scoped {@link Context}s.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 * @see Context
 */
public interface ScopeContext<S, T extends Context> extends Lifecycle, RuntimeEventListener {

    public Scope getScope();

    public void register(S key, T context);

    /**
     * Returns an instance associated with the current context
     *
     * @throws TargetException
     */
    public Object getInstance(T context) throws TargetException;


    /**
     *
     * @throws TargetException
     */
    public InstanceContext getInstanceContext(T context) throws TargetException;


}