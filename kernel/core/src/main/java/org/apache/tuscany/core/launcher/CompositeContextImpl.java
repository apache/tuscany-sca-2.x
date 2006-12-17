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

import org.osoa.sca.CompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetException;


public class CompositeContextImpl extends SCA implements CompositeContext {
    protected final CompositeComponent composite;

    public CompositeContextImpl(final CompositeComponent composite) {
        this.composite = composite;
    }

    public void start() {
        setCompositeContext(this);
    }

    public void stop() {
        setCompositeContext(null);
    }

    public ServiceReference createServiceReferenceForSession(Object arg0) {
        return null;
    }

    public ServiceReference createServiceReferenceForSession(Object arg0, String arg1) {
        return null;
    }

    public String getCompositeName() {
        return null;
    }

    public String getCompositeURI() {
        return null;
    }

    public RequestContext getRequestContext() {
        return null;
    }

    public <T> T locateService(Class<T> serviceInterface, String serviceName) throws ServiceRuntimeException {
        try {
            return composite.locateService(serviceInterface, serviceName);
        } catch (TargetException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public ServiceReference newSession(String arg0) {
        return null;
    }

    public ServiceReference newSession(String arg0, Object arg1) {
        return null;
    }

}
