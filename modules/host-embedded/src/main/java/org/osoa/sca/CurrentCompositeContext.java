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
package org.osoa.sca;

/**
 * @deprecated
 * 
 * Temporary here to help the bring up of samples and integration tests that
 * still use the 0.95 CompositeContext interface.
 *
 * @version $Rev$ $Date$
 */
public final class CurrentCompositeContext {
    private static final ThreadLocal<CompositeContext> CURRENT_COMPONENT =
        new InheritableThreadLocal<CompositeContext>();

    /**
     * Returns the current composite context associated with this thread.
     *
     * @return the current composite context
     */
    public static CompositeContext getContext() {
        return CURRENT_COMPONENT.get();
    }

    /**
     * Sets the composite context that is associated with this thread.
     *
     * @param context the context to associated with this thread; may be null
     * @return the context previously associated with this thread; may be null
     */
    public static CompositeContext setContext(CompositeContext context) {
        CompositeContext current = CURRENT_COMPONENT.get();
        CURRENT_COMPONENT.set(context);
        return current;
    }
}
