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
package org.apache.tuscany.core.implementation;

import java.lang.reflect.Member;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ProxyService;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.component.InstanceFactory;

/**
 * Encapsulates confuration for a Java-based atomic component
 *
 * @version $Rev$ $Date$
 */
public class PojoConfiguration {
    private URI name;
    private InstanceFactory<?> instanceFactory2;
    private PojoObjectFactory<?> instanceFactory;
    private List<String> constructorParamNames = new ArrayList<String>();
    private List<Class<?>> constructorParamTypes = new ArrayList<Class<?>>();
    private int initLevel;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private List<Injector> propertyInjectors = new ArrayList<Injector>();
    private Map<String, Member> referenceSites = new HashMap<String, Member>();
    private Map<String, Member> propertySites = new HashMap<String, Member>();
    private Map<String, Member> resourceSites = new HashMap<String, Member>();
    private Map<String, Member> callbackSites = new HashMap<String, Member>();
    private ProxyService proxyService;
    private WorkContext workContext;
    private long maxIdleTime = -1;
    private long maxAge = -1;
    private Class implementationClass;
    private URI groupId;

    public URI getName() {
        return name;
    }

    public void setName(URI name) {
        this.name = name;
    }

    @Deprecated
    public PojoObjectFactory<?> getInstanceFactory() {
        return instanceFactory;
    }

    @Deprecated
    public void setInstanceFactory(PojoObjectFactory<?> objectFactory) {
        this.instanceFactory = objectFactory;
    }

    public InstanceFactory<?> getInstanceFactory2() {
        return instanceFactory2;
    }

    public void setInstanceFactory2(InstanceFactory<?> instanceFactory2) {
        this.instanceFactory2 = instanceFactory2;
    }

    public List<String> getConstructorParamNames() {
        return constructorParamNames;
    }

    public void setConstructorParamNames(List<String> names) {
        constructorParamNames = names;
    }

    public void addConstructorParamName(String name) {
        constructorParamNames.add(name);
    }

    public List<Class<?>> getConstructorParamTypes() {
        return constructorParamTypes;
    }

    public void setConstructorParamTypes(List<Class<?>> constructorParamTypes) {
        this.constructorParamTypes = constructorParamTypes;
    }

    public void addConstructorParamType(Class<?> type) {
        constructorParamTypes.add(type);
    }

    public URI getGroupId() {
        return groupId;
    }

    public void setGroupId(URI groupId) {
        this.groupId = groupId;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public EventInvoker<Object> getInitInvoker() {
        return initInvoker;
    }

    public void setInitInvoker(EventInvoker<Object> initInvoker) {
        this.initInvoker = initInvoker;
    }

    public EventInvoker<Object> getDestroyInvoker() {
        return destroyInvoker;
    }

    public void setDestroyInvoker(EventInvoker<Object> destroyInvoker) {
        this.destroyInvoker = destroyInvoker;
    }

    public List<Injector> getPropertyInjectors() {
        return propertyInjectors;
    }

    public void addPropertyInjector(Injector injector) {
        propertyInjectors.add(injector);
    }

    public Map<String, Member> getReferenceSite() {
        return referenceSites;
    }

    public void addReferenceSite(String name, Member member) {
        referenceSites.put(name, member);
    }

    public Map<String, Member> getResourceSites() {
        return resourceSites;
    }

    public void addResourceSite(String name, Member member) {
        resourceSites.put(name, member);
    }

    public Map<String, Member> getCallbackSites() {
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

    public ProxyService getProxyService() {
        return proxyService;
    }

    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public WorkContext getWorkContext() {
        return workContext;
    }

    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    public Class getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class implementationClass) {
        this.implementationClass = implementationClass;
    }
}
