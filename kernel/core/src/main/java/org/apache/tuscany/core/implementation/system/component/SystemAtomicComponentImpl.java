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
package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.implementation.PojoAtomicComponent;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;

/**
 * Default implementation of a system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentImpl extends PojoAtomicComponent implements SystemAtomicComponent {

    public SystemAtomicComponentImpl(String name, PojoConfiguration configuration) {
        super(name, configuration);
        scope = Scope.MODULE;
    }

    public Object getServiceInstance(String name) throws TargetException {
        return getTargetInstance();
    }

    public Object getServiceInstance() throws TargetException {
        return getTargetInstance();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        return null;
    }

    public boolean isSystem() {
        return true;
    }

    protected ObjectFactory<?> createWireFactory(RuntimeWire wire) {
        assert wire instanceof SystemOutboundWire : "Wire must be an instance of " + SystemOutboundWire.class.getName();
        SystemOutboundWire systemWire = (SystemOutboundWire) wire;
        return new SystemWireObjectFactory(systemWire);
    }
}
