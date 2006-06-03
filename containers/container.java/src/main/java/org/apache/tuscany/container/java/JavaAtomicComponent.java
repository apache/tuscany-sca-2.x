/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.java;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.PojoAtomicComponent;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContext;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The runtime instantiation of Java component implementations
 *
 * @version $Rev$ $Date$
 */
public class JavaAtomicComponent<T> extends PojoAtomicComponent<T> {

    public JavaAtomicComponent(String name,
                               CompositeComponent<?> parent,
                               ScopeContext scopeContext,
                               List<Class<?>> serviceInterfaces,
                               ObjectFactory<?> objectFactory,
                               Scope scope,
                               boolean eagerInit,
                               EventInvoker<Object> initInvoker,
                               EventInvoker<Object> destroyInvoker,
                               List<Injector> injectors,
                               Map<String, Member> members,
                               WireService wireService) {
        super(name, parent, scopeContext, serviceInterfaces, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members, wireService);
        this.scope = scope;
    }

    public Object getServiceInstance(String name) throws TargetException {
        InboundWire<?> wire = serviceWires.get(name);
        if (wire == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        }
        return wireService.createProxy(wire);
    }

    public T getService() throws TargetException {
        if (serviceInterfaces.size() == 0) {
            return getTargetInstance();
        } else if (serviceInterfaces.size() == 1) {
            return getTargetInstance();
        } else {
            throw new TargetException("Component must have exactly one service");
        }
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return new JavaTargetInvoker(operation, this);
    }


}
