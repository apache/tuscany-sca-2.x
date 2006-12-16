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
package org.apache.tuscany.core.runtime;

import java.net.URL;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.builder.BuilderException;

import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.runtime.TuscanyRuntime;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractRuntime implements TuscanyRuntime {
    private URL systemScdl;
    private String applicationName;
    private URL applicationScdl;
    private ClassLoader hostClassLoader;
    private ClassLoader applicationClassLoader;
    private RuntimeInfo runtimeInfo;
    private MonitorFactory monitorFactory;

    public URL getSystemScdl() {
        return systemScdl;
    }

    public void setSystemScdl(URL systemScdl) {
        this.systemScdl = systemScdl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public URL getApplicationScdl() {
        return applicationScdl;
    }

    public void setApplicationScdl(URL applicationScdl) {
        this.applicationScdl = applicationScdl;
    }

    public ClassLoader getApplicationClassLoader() {
        return applicationClassLoader;
    }

    public void setApplicationClassLoader(ClassLoader applicationClassLoader) {
        this.applicationClassLoader = applicationClassLoader;
    }

    public ClassLoader getHostClassLoader() {
        return hostClassLoader;
    }

    public void setHostClassLoader(ClassLoader hostClassLoader) {
        this.hostClassLoader = hostClassLoader;
    }

    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    public void setMonitorFactory(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }

    public MonitorFactory createDefaultMonitorFactory() {
        return new NullMonitorFactory();
    }

    protected CompositeComponent deploySystemScdl(Deployer deployer,
                                                  CompositeComponent parent,
                                                  String name,
                                                  URL systemScdl,
                                                  ClassLoader systemClassLoader)
        throws LoaderException, BuilderException, ComponentException {

        SystemCompositeImplementation impl = new SystemCompositeImplementation();
        impl.setScdlLocation(systemScdl);
        impl.setClassLoader(systemClassLoader);
        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(name, impl);

        return (CompositeComponent) deployer.deploy(parent, definition);
    }

    protected CompositeComponent deployApplicationScdl(Deployer deployer,
                                                       CompositeComponent parent,
                                                       String name,
                                                       URL applicationScdl,
                                                       ClassLoader applicationClassLoader)
        throws LoaderException, BuilderException, ComponentException {

        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(applicationScdl);
        impl.setClassLoader(applicationClassLoader);
        ComponentDefinition<CompositeImplementation> definition =
            new ComponentDefinition<CompositeImplementation>(name, impl);

        return (CompositeComponent) deployer.deploy(parent, definition);
    }

}
