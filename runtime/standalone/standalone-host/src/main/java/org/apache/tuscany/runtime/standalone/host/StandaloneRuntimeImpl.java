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
package org.apache.tuscany.runtime.standalone.host;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.apache.tuscany.api.annotation.LogLevel;
import org.apache.tuscany.core.monitor.JavaLoggingMonitorFactory;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.runtime.standalone.StandaloneRuntime;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.runtime.standalone.host.implementation.launched.Launched;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class StandaloneRuntimeImpl extends AbstractRuntime<StandaloneRuntimeInfo> implements StandaloneRuntime {
    JavaLoggingMonitorFactory monitorFactory;
    StandaloneMonitor monitor;

    public StandaloneRuntimeImpl() {
        super(StandaloneRuntimeInfo.class);
        monitorFactory = new JavaLoggingMonitorFactory();
        setMonitorFactory(monitorFactory);
        monitor = monitorFactory.getMonitor(StandaloneMonitor.class);
    }

    /**
     * Deploys the specified application SCDL and runs the lauched component within the deployed composite.
     *
     * @param applicationScdl        Application SCDL that implements the composite.
     * @param applicationClassLoader Classloader used to deploy the composite.
     * @param args                   Arguments to be passed to the lauched component.
     * @deprecated This is a hack for deployment and should be removed.
     */
    public int deployAndRun(URL applicationScdl, ClassLoader applicationClassLoader, String[] args) throws Exception {

        URI compositeUri = new URI("/test/composite");
        URI compositeBase = new URI("/test/composite/");

        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(applicationScdl);
        impl.setClassLoader(applicationClassLoader);

        ComponentDefinition<CompositeImplementation> definition =
            new ComponentDefinition<CompositeImplementation>(compositeUri, impl);
        try {
            Collection<Component> components = getDeployer().deploy(null, definition);
            for (Component component : components) {
                component.start();
            }
            ScopeRegistry scopeRegistry = getScopeRegistry();
            ScopeContainer<URI, URI> container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
            container.startContext(compositeUri, compositeUri);
            getWorkContext().setIdentifier(Scope.COMPOSITE, compositeUri);
            WorkContext workContext = new SimpleWorkContext();
            workContext.setIdentifier(Scope.COMPOSITE, compositeUri);
            try {
                return run(impl, args, compositeBase, workContext);
            } finally {
                container.stopContext(compositeUri);
                getWorkContext().setIdentifier(Scope.COMPOSITE, null);
            }
        } catch (Exception e) {
            monitor.runError(e);
        }
        return -1;

    }

    private int run(CompositeImplementation impl, String[] args, URI compositeUri, WorkContext workContext) throws Exception {
        CompositeComponentType<?, ?, ?> componentType = impl.getComponentType();
        Map<String, ComponentDefinition<? extends Implementation<?>>> components = componentType.getComponents();
        for (Map.Entry<String, ComponentDefinition<? extends Implementation<?>>> entry : components.entrySet()) {
            String name = entry.getKey();
            ComponentDefinition<? extends Implementation<?>> launchedDefinition = entry.getValue();
            Implementation implementation = launchedDefinition.getImplementation();
            if (implementation.getClass().isAssignableFrom(Launched.class)) {
                return run(compositeUri.resolve(name), implementation, args, workContext);
            }
        }
        return -1;
    }

    private int run(URI componentUri, Implementation implementation, String[] args, WorkContext workContext)
        throws TargetInvokerCreationException, InvocationTargetException {
        Launched launched = (Launched) implementation;
        PojoComponentType launchedType = launched.getComponentType();
        Map services = launchedType.getServices();
        JavaMappedService testService = (JavaMappedService) services.get("main");
        Operation<?> operation = testService.getServiceContract().getOperations().get("main");
        Component component = getComponentManager().getComponent(componentUri);
        TargetInvoker targetInvoker = component.createTargetInvoker("main", operation);
        Object result = targetInvoker.invokeTarget(new Object[]{args}, TargetInvoker.NONE, workContext);
        try {
            return int.class.cast(result);
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public interface StandaloneMonitor {
        @LogLevel("SEVERE")
        void runError(Exception e);
    }
}
