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
 * @param <GROUP> the component group id type
 */
public class JavaComponent<T,GROUP> extends PojoComponent<T,GROUP> {
    public JavaComponent(URI componentId,
                         InstanceFactoryProvider<T> instanceFactoryProvider,
                         ScopeContainer<GROUP, ?> scopeContainer,
                         GROUP groupId,
                         int initLevel,
                         long maxIdleTime,
                         long maxAge) {
        super(componentId, instanceFactoryProvider, scopeContainer, groupId, initLevel, maxIdleTime, maxAge);
    }

}
