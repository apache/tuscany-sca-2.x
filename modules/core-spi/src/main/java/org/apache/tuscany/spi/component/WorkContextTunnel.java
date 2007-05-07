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
package org.apache.tuscany.spi.component;

import java.net.URI;

import org.apache.tuscany.scope.Scope;


/**
 * Class for tunneling a WorkContext through the invocation of a user class.
 *
 * @version $Rev$ $Date$
 */
public final class WorkContextTunnel {

    private static final ThreadLocal<WorkContext> CONTEXT = new ThreadLocal<WorkContext>(){
        protected synchronized WorkContext initialValue() {
            // TODO: is this the correct way to initialize a new WorkContext?
            WorkContext workContext = new WorkContextImpl();
            workContext.setIdentifier(Scope.COMPOSITE, URI.create("sca://root.application/").resolve("default"));
            return workContext;
        }
    };

    private WorkContextTunnel() {
    }

    /**
     * Set the WorkContext for the current thread.
     * The current work context is returned and must be restored after the invocation is complete.
     * Typical usage would be:
     * <pre>
     *   WorkContext old = PojoWorkContextTunnel.setThreadWorkContext(newContext);
     *   try {
     *      ... invoke user code ...
     *   } finally {
     *     PojoWorkContextTunnel.setThreadWorkContext(old);
     *   }
     * </pre>
     * @param context
     * @return the current work context for the thread; this must be restored after the invocation is made
     */
    public static WorkContext setThreadWorkContext(WorkContext context) {
        WorkContext old = CONTEXT.get();
        CONTEXT.set(context);
        return old;
    }

    /**
     * Returns the WorkContext for the current thread.
     *
     * @return the WorkContext for the current thread
     */
    public static WorkContext getThreadWorkContext() {
        return CONTEXT.get();
    }
}
