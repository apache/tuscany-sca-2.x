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
package org.apache.tuscany.runtime.embedded;

import static org.apache.tuscany.runtime.embedded.SimpleRuntimeInfo.DEFAULT_COMPOSITE;

import java.net.URI;
import java.util.Collection;

import org.apache.tuscany.api.annotation.LogLevel;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.implementation.PojoWorkContextTunnel;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $Rev$ $Date$
 */
public class SimpleRuntimeImpl extends AbstractRuntime<SimpleRuntimeInfo> implements SimpleRuntime {
    private ScopeContainer<URI> container;

    public SimpleRuntimeImpl(SimpleRuntimeInfo runtimeInfo) {
        super(SimpleRuntimeInfo.class);
        ClassLoader hostClassLoader = runtimeInfo.getClassLoader();
        setHostClassLoader(hostClassLoader);
        setApplicationScdl(runtimeInfo.getApplicationSCDL());
        setSystemScdl(runtimeInfo.getSystemSCDL());
        setRuntimeInfo(runtimeInfo);
    }

    public interface SimpleMonitor {
        @LogLevel("SEVERE")
        void runError(Exception e);
    }

    @SuppressWarnings("unchecked")
    public Component start() throws Exception {
        
        initialize();

        ScopeRegistry scopeRegistry = getScopeRegistry();
        container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);

        // int i = 0;
        // for (URL ext : runtimeInfo.getExtensionSCDLs()) {
        // URI uri = URI.create("/extensions/extension" + (i++));
        // deployExtension(null, uri, ext, runtimeInfo.getClassLoader());
        // }

        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(getApplicationScdl());
        impl.setClassLoader(runtimeInfo.getClassLoader());

        ComponentDefinition<CompositeImplementation> definition = new ComponentDefinition<CompositeImplementation>(
                                                                                                                   DEFAULT_COMPOSITE,
                                                                                                                   impl);
        Collection<Component> components = getDeployer().deploy(null, definition);
        for (Component component : components) {
            component.start();
        }
        container.startContext(DEFAULT_COMPOSITE, DEFAULT_COMPOSITE);
        getWorkContext().setIdentifier(Scope.COMPOSITE, DEFAULT_COMPOSITE);
        WorkContext workContext = new SimpleWorkContext();
        workContext.setIdentifier(Scope.COMPOSITE, DEFAULT_COMPOSITE);
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        return getComponentManager().getComponent(definition.getUri());
    }

    @SuppressWarnings("deprecation")
    public <T> T getSystemService(Class<T> type, String name) throws TargetResolutionException {
        SCAObject child = getComponentManager().getComponent(URI.create(name));
        if (child == null) {
            return null;
        }
        AtomicComponent service = (AtomicComponent)child;
        return type.cast(service.getTargetInstance());
    }

    @Override
    public void destroy() {
        container.stopContext(DEFAULT_COMPOSITE);
        getWorkContext().setIdentifier(Scope.COMPOSITE, null);
        super.destroy();
    }

}
