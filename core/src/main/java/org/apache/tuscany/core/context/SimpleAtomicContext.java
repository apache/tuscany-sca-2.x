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
package org.apache.tuscany.core.context;

import org.apache.tuscany.core.context.impl.AbstractContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.QualifiedName;

/**
 * @version $Rev$ $Date$
 */
public class SimpleAtomicContext extends AbstractContext implements AtomicContext {
    private final ScopeManager scopeManager;
    private final InstanceContextFactory instanceFactory;

    public SimpleAtomicContext(String name, ScopeManager scopeManager, InstanceContextFactory instanceFactory) {
        super(name);
        this.scopeManager = scopeManager;
        this.instanceFactory = instanceFactory;
    }

    public void start() throws CoreRuntimeException {
    }

    public void stop() throws CoreRuntimeException {
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getTargetInstance();
    }

    public Object getTargetInstance() throws TargetException {
        InstanceContext instanceContext = scopeManager.getInstance(instanceFactory);
        return instanceContext.getInstance();
    }

    public boolean isEagerInit() {
        throw new UnsupportedOperationException();
    }

    public void init() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public void destroy() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public boolean isDestroyable() {
        throw new UnsupportedOperationException();
    }
}
