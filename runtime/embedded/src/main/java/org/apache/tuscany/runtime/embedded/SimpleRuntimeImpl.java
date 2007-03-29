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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.api.annotation.LogLevel;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.implementation.PojoWorkContextTunnel;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.resolver.ResolutionException;

/**
 * @version $Rev$ $Date$
 */
public class SimpleRuntimeImpl extends AbstractRuntime<SimpleRuntimeInfo> implements SimpleRuntime {
    private ScopeContainer<URI> container;

    public SimpleRuntimeImpl(SimpleRuntimeInfo runtimeInfo) {
        super(SimpleRuntimeInfo.class);
        ClassLoader hostClassLoader = ClassLoader.getSystemClassLoader();
        setHostClassLoader(hostClassLoader);
        setApplicationScdl(runtimeInfo.getApplicationSCDL());
        setSystemScdl(runtimeInfo.getSystemSCDL());
        setRuntimeInfo(runtimeInfo);
    }

    public interface SimpleMonitor {
        @LogLevel("SEVERE")
        void runError(Exception e);
    }

    protected Collection<Component> deployExtension(Component parent,
                                                    URI name,
                                                    URL extensionSCDL,
                                                    ClassLoader systemClassLoader) throws LoaderException,
        BuilderException, ComponentException, ResolutionException {

        SystemCompositeImplementation impl = new SystemCompositeImplementation();
        impl.setScdlLocation(extensionSCDL);
        impl.setClassLoader(systemClassLoader);
        ComponentDefinition<SystemCompositeImplementation> definition = new ComponentDefinition<SystemCompositeImplementation>(
                                                                                                                               name,
                                                                                                                               impl);

        Collection<Component> components = getDeployer().deploy(parent, definition);
        for (Component component : components) {
            component.start();
        }
        return components;
    }

    /**
     * Create a facade composite to include all the system and extension SCDLs
     * @param urls
     * @return
     * @throws IOException
     */
    private URL merge(List<URL> urls) throws IOException {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.print("<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"");
        pw.println(" name=\"org.apache.tuscany.System\" autowire=\"true\">");
        int i = 0;
        for (URL scdl : urls) {
            pw.print("<include name=\"");
            pw.print("composite" + (i++) + "\"");
            pw.print(" scdlLocation=\"");
            pw.println(scdl.toString() + "\"/>");
        }
        pw.println("</composite>");
        sw.close();
        // System.out.println(sw.toString());
        return new URL(null, "sca:system.scdl", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return new URLConnection(u) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
                    }

                    @Override
                    public void connect() throws IOException {
                    }
                };
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Component start() throws Exception {
        
        // FIXME: [rfeng] This is a hack to bootstrap extensions
        SimpleRuntimeInfo runtimeInfo = getRuntimeInfo();
        List<URL> scdls = new ArrayList<URL>();
        scdls.add(runtimeInfo.getSystemSCDL());
        for (URL ext : runtimeInfo.getExtensionSCDLs()) {
            scdls.add(ext);
        }
        setSystemScdl(merge(scdls));
        // End of hack
        
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
