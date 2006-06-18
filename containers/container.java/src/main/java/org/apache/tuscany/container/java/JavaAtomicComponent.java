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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.component.PojoAtomicComponent;

/**
 * The runtime instantiation of Java component implementations
 *
 * @version $Rev$ $Date$
 */
public class JavaAtomicComponent<T> extends PojoAtomicComponent<T> {

    public JavaAtomicComponent(String name, PojoConfiguration configuration) {
        super(name, configuration);
        this.scope = configuration.getScopeContainer().getScope();
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
        return new JavaTargetInvoker(operation, this);
    }


}
