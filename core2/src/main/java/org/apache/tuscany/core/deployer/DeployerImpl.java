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
package org.apache.tuscany.core.deployer;

import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.Loader;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.Implementation;

/**
 * Default implementation of Deployer.
 *
 * @version $Rev$ $Date$
 */
public class DeployerImpl implements Deployer {
    private Loader loader;
    private Builder builder;
    private Connector connector;

    @Autowire
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    @Autowire
    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    @Autowire
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public <I extends Implementation<?>> Context<?> deploy(CompositeContext<?> parent, Component<I> component) throws LoaderException {
        ScopeContext moduleScope = new ModuleScopeContext();
        DeploymentContext deploymentContext = new DeploymentContext(null, null, moduleScope);
        load(component, deploymentContext);
        Context<?> context = build(parent, component, deploymentContext);
        connect(context);
        parent.registerContext(context);
        return context;
    }

    /**
     * Load the component type information for the component being deployed.
     * For a typical deployment this will result in the SCDL definition being loaded.
     *
     * @param component         the component being deployed
     * @param deploymentContext the current deployment context
     */
    protected <I extends Implementation<?>> void load(Component<I> component, DeploymentContext deploymentContext) throws LoaderException {
        loader.loadComponentType(component.getImplementation(), deploymentContext);
    }

    /**
     * Build the runtime context for a loaded component.
     *
     * @param parent            the context that will be the parent of the new sub-context
     * @param component         the component being deployed
     * @param deploymentContext the current deployment context
     * @return the new runtime context
     */
    protected <I extends Implementation<?>> Context<?> build(CompositeContext<?> parent, Component<I> component, DeploymentContext deploymentContext) {
        return builder.build(parent, component, deploymentContext);
    }

    /**
     * Connect the context's source wires to other target wires within the scope of the parent.
     *
     * @param context the context to connect
     */
    protected void connect(Context<?> context) {
        connector.connect(context);
    }
}
