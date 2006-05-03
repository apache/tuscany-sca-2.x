/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.context.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.InstanceContextFactory;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.core.context.CoreRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class ModuleScopeManager extends ScopeManagerSupport {
    private final Map<InstanceContextFactory, InstanceContext> cache;

    public ModuleScopeManager() {
        cache = new ConcurrentHashMap<InstanceContextFactory, InstanceContext>();
    }

    public InstanceContext getInstance(InstanceContextFactory contextFactory) throws TargetException {
        synchronized (contextFactory) {
            InstanceContext instanceContext = cache.get(contextFactory);
            if (instanceContext == null) {
                instanceContext = contextFactory.createContext();
                instanceContext.start();
                cache.put(contextFactory, instanceContext);
            }
            return instanceContext;
        }
    }

    public void stop() throws CoreRuntimeException {
        setLifecycleState(STOPPING);
        for (InstanceContext instanceContext : cache.values()) {
            instanceContext.stop();
        }
        setLifecycleState(STOPPED);
    }
}
