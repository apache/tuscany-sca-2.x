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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;

import org.osoa.sca.annotations.OneWay;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ComponentRuntimeException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.implementation.PojoAtomicComponent;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.WireObjectFactory;
import org.apache.tuscany.core.policy.async.AsyncMonitor;

/**
 * The runtime instantiation of Java component implementations
 *
 * @version $Rev$ $Date$
 */
public class JavaAtomicComponent<T> extends PojoAtomicComponent<T> {

    private WorkScheduler workScheduler;
    private AsyncMonitor monitor;

    public JavaAtomicComponent(String name, PojoConfiguration configuration, WorkScheduler scheduler,
                               AsyncMonitor monitor) {
        super(name, configuration);
        this.scope = configuration.getScopeContainer().getScope();
        this.workScheduler = scheduler;
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

    public T getServiceInstance() throws TargetException {
        if (serviceInterfaces.size() == 0) {
            return getTargetInstance();
        } else if (serviceInterfaces.size() == 1) {
            return getTargetInstance();
        } else {
            throw new TargetException("Component must have exactly one service");
        }
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        TargetInvoker targetInvoker;
        if (operation.getAnnotation(OneWay.class) != null) {
            if (workScheduler == null) {
                // TODO Make sure appropriate exception is thrown
                throw new ComponentRuntimeException("Need an instance of workScheduler");
            }
            //REVIEW we should set required as an autowire attribute and have the runtime perform this check
            if (monitor == null) {
                // TODO Make sure appropriate exception is thrown
                // throw new ComponentRuntimeException("Need an instance of monitor");
            }
            targetInvoker = new AsyncJavaTargetInvoker(operation, this, workScheduler, monitor);
        } else {
            targetInvoker = new JavaTargetInvoker(operation, this);
        }
        return targetInvoker;


    }

    protected ObjectFactory<?> createWireFactory(OutboundWire wire) {
        return new WireObjectFactory(wire, wireService);
    }
}
