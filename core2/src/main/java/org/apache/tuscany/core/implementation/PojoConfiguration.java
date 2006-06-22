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

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;

/**
 * Encapsulates confuration for a Java-based atomic component
 *
 * @version $Rev$ $Date$
 */
public class PojoConfiguration {

    private CompositeComponent<?> parent;
    private ScopeContainer scopeContainer;
    private ObjectFactory<?> objectFactory;
    private boolean eagerInit;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private List<Injector> injectors = new ArrayList<Injector>();
    private WireService wireService;
    private Map<String, Member> members = new HashMap<String, Member>();
    private List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();

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

    public ObjectFactory<?> getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory<?> objectFactory) {
        this.objectFactory = objectFactory;
    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void setEagerInit(boolean eagerInit) {
        this.eagerInit = eagerInit;
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

    public List<Injector> getInjectors() {
        return injectors;
    }

    public void addInjectors(Injector injector) {
        injectors.add(injector);
    }

    public Map<String, Member> getMembers() {
        return members;
    }

    public void addMember(String name, Member member) {
        members.put(name, member);
    }

    public WireService getWireService() {
        return wireService;
    }

    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }
}
