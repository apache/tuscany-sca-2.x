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
package org.apache.tuscany.spi.extension;

import java.util.List;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public abstract class SystemAtomicComponentExtension extends AtomicComponentExtension implements SystemAtomicComponent {
    protected List<Class<?>> interfazes;

    public SystemAtomicComponentExtension(String name,
                                          CompositeComponent parent,
                                          ScopeContainer scopeContainer,
                                          int initLevel) {
        this(name, null, parent, scopeContainer, initLevel);
    }

    public SystemAtomicComponentExtension(String name,
                                          List<Class<?>> serviceInterfaces,
                                          CompositeComponent parent,
                                          ScopeContainer scopeContainer,
                                          int initLevel) {
        super(name, parent, scopeContainer, null, null, null, initLevel);
        this.interfazes = serviceInterfaces;
    }

    public Object getServiceInstance() throws TargetException {
        return getTargetInstance();
    }

    public Object getServiceInstance(String name) throws TargetException {
        throw new UnsupportedOperationException();
    }

    public List<Class<?>> getServiceInterfaces() {
        return interfazes;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        throw new UnsupportedOperationException();
    }

    public boolean isSystem() {
        return true;
    }

}
