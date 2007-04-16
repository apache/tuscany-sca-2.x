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
package org.apache.tuscany.implementation.java.context;

import java.lang.reflect.Method;

import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.core.wire.WireObjectFactory;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * The runtime instantiation of Java component implementations
 * 
 * @version $Rev$ $Date$
 */
public class JavaAtomicComponent extends PojoAtomicComponent {
    private JavaPropertyValueObjectFactory propertyValueFactory;
    private DataBindingExtensionPoint dataBindingRegistry;

    public JavaAtomicComponent(PojoConfiguration configuration) {
        super(configuration);
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback)
        throws TargetInvokerCreationException {

        Class<?> implClass;
        if (isCallback) {
            implClass = ((JavaInterface)operation.getInterface()).getJavaClass();
        } else {
            implClass = configuration.getImplementationClass();
        }
        try {
            Method method = JavaInterfaceUtil.findMethod(implClass, operation);
            boolean passByValue = operation.getInterface().isRemotable() && (!configuration.getDefinition()
                                      .isAllowsPassByReference(method));

            TargetInvoker invoker = new JavaTargetInvoker(method, this, scopeContainer);
            if (passByValue) {
                return new PassByValueInvoker(dataBindingRegistry, operation, method, this, scopeContainer);
            } else {
                return invoker;
            }
        } catch (NoSuchMethodException e) {
            throw new TargetMethodNotFoundException(operation);
        }

    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, Wire wire) {
        return new WireObjectFactory<B>(interfaze, wire, proxyService);
    }

    protected ObjectFactory<?> createPropertyValueFactory(ComponentProperty property,
                                                          Object propertyValue,
                                                          Class javaType) {
        return propertyValueFactory.createValueFactory(property, propertyValue, javaType);
    }

    public void setPropertyValueFactory(JavaPropertyValueObjectFactory propertyValueFactory) {
        this.propertyValueFactory = propertyValueFactory;
    }

    public void setDataBindingRegistry(DataBindingExtensionPoint dataBindingRegistry) {
        this.dataBindingRegistry = dataBindingRegistry;
    }

}
