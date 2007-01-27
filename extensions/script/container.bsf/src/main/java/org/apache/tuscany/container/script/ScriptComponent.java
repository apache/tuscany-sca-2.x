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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * A component implementation for script languages.
 *
 * @version $Rev$ $Date$
 */
public class ScriptComponent extends AtomicComponentExtension {
    private ScriptInstanceFactory factory;

    public ScriptComponent(ComponentConfiguration config) {
        super(config.getName(),
            config.getParent(),
            config.getWireService(),
            config.getWorkContext(),
            config.getWorkScheduler(),
            config.getMonitor(),
            config.getInitLevel());
        this.factory = config.getFactory();
        this.scope = config.getScopeContainer().getScope();
    }

    @SuppressWarnings("unchecked")
    public Object createInstance() throws ObjectCreationException {
        return factory.getInstance(); //(serviceBindings, context);
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
        Method[] methods = operation.getServiceContract().getInterfaceClass().getMethods();
        Method method = findMethod(operation, methods);
        return new ScriptTargetInvoker(method.getName(), this);
    }

    @SuppressWarnings({"unchecked"})
    protected void onReferenceWire(OutboundWire wire) {
        Class<?> clazz = wire.getServiceContract().getInterfaceClass();
        factory.addContextObjectFactory(wire.getReferenceName(), new WireObjectFactory(clazz, wire, wireService));
    }

    public Object getTargetInstance() throws TargetResolutionException {
        return scopeContainer.getInstance(this);
    }

}
