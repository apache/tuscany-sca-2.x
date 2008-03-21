/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.implementation.bpel.impl;

import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Class for tunnelling a RuntimeComponent for invocation of a BPEL process
 * This is used for BPEL to invoke references
 * 
* @version $Rev$ $Date$
 */
public class ThreadRuntimeComponentContext {
    
    private static final ThreadLocal<RuntimeComponent> CONTEXT = new ThreadLocal<RuntimeComponent>();

    private ThreadRuntimeComponentContext() {
        
    }
    
    /**
     * Set the RuntimeComponentContext for the current thread.
     * The current runtime component context is returned and must be restored after the invocation is complete.
     * Typical usage would be:
     * <pre>
     *   RuntimeComponent old = ThreadRuntimeComponentContext.setRuntimeComponent(newContext);
     *   try {
     *      ... invoke user code ...
     *   } finally {
     *     ThreadRuntimeComponentContext.setThreadWorkContext(old);
     *   }
     * </pre>
     * @param context
     * @return the current work context for the thread; this must be restored after the invocation is made
     */
    public static RuntimeComponent setRuntimeComponent(RuntimeComponent runtimeComponent) {
        RuntimeComponent old = CONTEXT.get();
        CONTEXT.set(runtimeComponent);
        return old;
    }
    
    /**
     * Returns the RuntimeComponentContext for the current thread.
     *
     * @return the RuntimeComponentContext for the current thread
     */
    public static RuntimeComponent getRuntimeComponent() {
        return CONTEXT.get();
    }

}
