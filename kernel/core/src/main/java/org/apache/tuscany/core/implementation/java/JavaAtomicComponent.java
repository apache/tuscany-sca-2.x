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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.core.implementation.PojoAtomicComponent;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.wire.WireObjectFactory;

/**
 * The runtime instantiation of Java component implementations
 *
 * @version $Rev$ $Date$
 */
public class JavaAtomicComponent extends PojoAtomicComponent {

    public JavaAtomicComponent(PojoConfiguration configuration) {
        super(configuration);
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation)
        throws TargetInvokerCreationException {
        Method[] methods;
        Class callbackClass = null;
        if (operation.isCallback()) {
            callbackClass = operation.getServiceContract().getCallbackClass();
            methods = callbackClass.getMethods();

        } else {
            methods = implementationClass.getMethods();
        }
        Method method = findMethod(operation, methods);
        if (method == null) {
            throw new TargetMethodNotFoundException(operation);
        }
        return new JavaTargetInvoker(method, this, workContext);
    }

    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, Wire wire) {
        return new WireObjectFactory<B>(interfaze, wire, proxyService);
    }
}
