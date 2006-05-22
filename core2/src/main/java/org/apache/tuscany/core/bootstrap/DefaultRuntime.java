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
package org.apache.tuscany.core.bootstrap;

import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.spi.bootstrap.RuntimeContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.deployer.Deployer;

/**
 * @version $Rev$ $Date$
 */
public class DefaultRuntime extends SystemCompositeContextImpl<Void> implements RuntimeContext<SystemCompositeContext> {
    private final CompositeContext rootContext;
    private final SystemCompositeContext systemContext;
    private final Deployer deployer;

    public DefaultRuntime(SystemCompositeContext systemContext, CompositeContext rootContext) {
        this.systemContext = systemContext;
        this.rootContext = rootContext;
        deployer = null;
    }

    public CompositeContext getRootContext() {
        return rootContext;
    }

    public SystemCompositeContext getSystemContext() {
        return systemContext;
    }

    public Deployer getDeployer() {
        return deployer;
    }
}
