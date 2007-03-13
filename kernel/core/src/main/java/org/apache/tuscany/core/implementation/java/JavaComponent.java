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

import java.net.URI;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.implementation.PojoComponent;
import org.apache.tuscany.spi.component.ScopeContainer;

/**
 * @version $Revision$ $Date$
 * @param <T> the implementation class for the defined component
 */
public class JavaComponent<T> extends PojoComponent<T> {
    public JavaComponent(URI componentId,
                         InstanceFactoryProvider<T> instanceFactoryProvider,
                         ScopeContainer scopeContainer,
                         int initLevel,
                         long maxIdleTime,
                         long maxAge) {
        super(componentId, instanceFactoryProvider, scopeContainer, initLevel, maxIdleTime, maxAge);
    }

/*
    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        List<String> params = operation.getParameters();
        Class<?>[] paramTypes = new Class<?>[params.size()];
        ClassLoader loader = implementationClass.getClassLoader();
        assert loader != null;
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            try {
                paramTypes[i] = loader.loadClass(param);
            } catch (ClassNotFoundException e) {
                throw new TypeNotFoundException(operation, e);
            }
        }
        Method method;
        try {
            method = implementationClass.getMethod(operation.getName(), paramTypes);
        } catch (NoSuchMethodException e) {
            throw new TargetMethodNotFoundException(operation);
        }
        return new JavaTargetInvoker(method, this, scopeContainer, workContext);
    }
*/
}
