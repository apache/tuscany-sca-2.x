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
package org.apache.tuscany.container.groovy;

import groovy.lang.GroovyObject;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Encapsulates confuration for a Groovy-based atomic component
 *
 * @version $Rev$ $Date$
 */
public class GroovyConfiguration {

    private CompositeComponent parent;
    private int initLevel;
    private Map<String, Member> referenceSites = new HashMap<String, Member>();
    private Map<String, Member> propertySites = new HashMap<String, Member>();
    private Map<String, Member> callbackSites = new HashMap<String, Member>();
    private List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
    private WireService wireService;
    private WorkContext workContext;
    private String name;
    private Class<? extends GroovyObject> groovyClass;
    private List<Class<?>> services;
    private ExecutionMonitor monitor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends GroovyObject> getGroovyClass() {
        return groovyClass;
    }

    public void setGroovyClass(Class<? extends GroovyObject> groovyClass) {
        this.groovyClass = groovyClass;
    }

    public CompositeComponent getParent() {
        return parent;
    }

    public void setParent(CompositeComponent parent) {
        this.parent = parent;
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public void addServiceInterface(Class<?> serviceInterface) {
        serviceInterfaces.add(serviceInterface);
    }

    public int getInitLevel() {
        return initLevel;
    }

    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
    }

    public List<Class<?>> getServices() {
        return services;
    }

    public void setServices(List<Class<?>> services) {
        this.services = services;
    }

    public Map<String, Member> getReferenceSite() {
        return referenceSites;
    }

    public void addReferenceSite(String name, Member member) {
        referenceSites.put(name, member);
    }

    public Map<String, Member> getCallbackSite() {
        return callbackSites;
    }

    public void addCallbackSite(String name, Member member) {
        callbackSites.put(name, member);
    }

    public Map<String, Member> getPropertySites() {
        return propertySites;
    }

    public void addPropertySite(String name, Member member) {
        propertySites.put(name, member);
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

    public ExecutionMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ExecutionMonitor monitor) {
        this.monitor = monitor;
    }
}
