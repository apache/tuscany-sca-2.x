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

import org.apache.tuscany.core.system.context.SystemCompositeComponent;
import org.apache.tuscany.core.system.context.SystemCompositeComponentImpl;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;

/**
 * @version $Rev$ $Date$
 */
public class DefaultRuntime extends SystemCompositeComponentImpl<Void> implements RuntimeComponent<SystemCompositeComponent> {
    private final CompositeComponent rootComponent;
    private final SystemCompositeComponent systemContext;
    private final Deployer deployer;

    public DefaultRuntime(SystemCompositeComponent systemContext, CompositeComponent rootComponent) {
        super(ComponentNames.TUSCANY_RUNTIME, null, null);
        this.systemContext = systemContext;
        this.rootComponent = rootComponent;
        deployer = null;
    }

    public CompositeComponent getRootComponent() {
        return rootComponent;
    }

    public SystemCompositeComponent getSystemComponent() {
        return systemContext;
    }

    public Deployer getDeployer() {
        return deployer;
    }
}
