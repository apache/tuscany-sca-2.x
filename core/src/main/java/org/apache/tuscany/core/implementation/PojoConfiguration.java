/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;

/**
 * Encapsulates confuration for a Java-based atomic component
 *
 * @version $Rev$ $Date$
 */
public class PojoConfiguration {

    private CompositeComponent<?> parent;
    private ScopeContainer scopeContainer;
    private PojoObjectFactory<?> instanceFactory;
    private List<String> constructorParamNames = new ArrayList<String>();
    private int initLevel;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private List<Injector> propertyInjectors = new ArrayList<Injector>();
    private Map<String, Member> referenceSites = new HashMap<String, Member>();
    private Map<String, Member> propertySites = new HashMap<String, Member>();
    private Map<String, Member> callbackSites = new HashMap<String, Member>();
    private List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
    private WireService wireService;
    private WorkContext workContext;

    public CompositeComponent<?> getParent() {
        return parent;
    }

    public void setParent(CompositeComponent<?> parent) {
        this.parent = parent;
    }

    public ScopeContainer getScopeContainer() {
        return scopeContainer;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public void addServiceInterface(Class<?> serviceInterface) {
        serviceInterfaces.add(serviceInterface);
    }

    public PojoObjectFactory<?> getInstanceFactory() {
        return instanceFactory;
    }

    public void setInstanceFactory(PojoObjectFactory<?> objectFactory) {
        this.instanceFactory = objectFactory;
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

    public int getInitLevel() {
        return initLevel;
    }

    public void setInitLevel(int initLevel) {
        this.initLevel = initLevel;
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

}
