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
package org.apache.tuscany.core.scope;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.ScopedImplementationProvider;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.ScopeContainer;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainerTestCase<T, KEY> extends TestCase {
    protected IMocksControl control;
    protected ScopeContainer<KEY> scopeContainer;
    protected URI groupId;
    protected KEY contextId;
    protected RuntimeComponent component;
    protected ScopedImplementation implementation; 
    protected InstanceWrapper<T> wrapper;

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        control = EasyMock.createStrictControl();
        component = control.createMock(RuntimeComponent.class);
        wrapper = control.createMock(InstanceWrapper.class);
        implementation = control.createMock(ScopedImplementation.class);
        EasyMock.expect(component.getImplementation()).andReturn(implementation).anyTimes();
    }

    protected void preRegisterComponent() throws Exception {
        scopeContainer.start();
        EasyMock.expect(implementation.isEagerInit(component)).andStubReturn(false);
    }

    protected void expectCreateWrapper() throws Exception {
        EasyMock.expect(implementation.createInstanceWrapper(component)).andReturn(wrapper);
        wrapper.start();
    }
    
    protected static interface ScopedImplementation extends ScopedImplementationProvider, Implementation {
        
    }
    
    
}
