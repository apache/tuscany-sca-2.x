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
package org.apache.tuscany.container.script;

import java.util.List;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.extension.ExecutionMonitor;

/**
 * Configuration holder for creating script components
 *
 * @version $Rev$ $Date$
 */
public class ComponentConfiguration {

    private String name;
    private ScriptInstanceFactory factory;
    private List<Class<?>> services;
    private CompositeComponent parent;
    private ScopeContainer scopeContainer;
    private WireService wireService;
    private WorkContext workContext;
    private WorkScheduler workScheduler;
    private ExecutionMonitor monitor;
    private int initLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScriptInstanceFactory getFactory() {
        return factory;
    }

    public void setFactory(ScriptInstanceFactory factory) {
        this.factory = factory;
    }

    public List<Class<?>> getServices() {
        return services;
    }

    public void setServices(List<Class<?>> services) {
        this.services = services;
    }

    public CompositeComponent getParent() {
        return parent;
    }

    public void setParent(CompositeComponent parent) {
        this.parent = parent;
    }

    public ScopeContainer getScopeContainer() {
        return scopeContainer;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
    }

    public WireService getWireService() {
        return wireService;
    }

    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    public WorkContext getWorkContext() {
        return workContext;
    }

    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    public WorkScheduler getWorkScheduler() {
        return workScheduler;
    }

    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    public ExecutionMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ExecutionMonitor monitor) {
        this.monitor = monitor;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
    }
}
