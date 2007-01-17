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
package org.apache.tuscany.core.launcher;

import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.implementation.composite.AbstractCompositeContext;


/**
 * Default implementation of the {@link org.osoa.sca.CompositeContext} for non-managed code
 *
 * @version $Rev$ $Date$
 */
public class CompositeContextImpl extends AbstractCompositeContext {

    public CompositeContextImpl(final CompositeComponent composite, final WireService wireService) {
        super(composite, wireService);
    }

    public ServiceReference createServiceReferenceForSession(Object arg0) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    public RequestContext getRequestContext() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String arg0) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String arg0, Object arg1) {
        throw new UnsupportedOperationException();
    }
}
