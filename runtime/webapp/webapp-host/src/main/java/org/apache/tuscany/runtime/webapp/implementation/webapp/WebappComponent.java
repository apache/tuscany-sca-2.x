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
package org.apache.tuscany.runtime.webapp.implementation.webapp;

import java.net.URI;
import java.util.Map;
import javax.servlet.ServletContext;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.core.wire.WireObjectFactory;
import org.apache.tuscany.runtime.webapp.Constants;

/**
 * @version $Rev$ $Date$
 */
public class WebappComponent extends AtomicComponentExtension {
    private final Map<String, ObjectFactory<?>> attributes;
    private final Map<String, Class<?>> referenceTypes;

    public WebappComponent(URI name,
                           WireService wireService,
                           WorkContext workContext,
                           WorkScheduler workScheduler,
                           ExecutionMonitor monitor,
                           Map<String, ObjectFactory<?>> attributes,
                           Map<String, Class<?>> referenceTypes) {
        super(name, wireService, workContext, workScheduler, monitor, 0, 0, 0);
        this.attributes = attributes;
        this.referenceTypes = referenceTypes;
    }

    protected void onReferenceWire(OutboundWire wire) {
        String name = wire.getSourceUri().getFragment();
        Class<?> type = referenceTypes.get(name);
        ObjectFactory<?> factory = createWireFactory(type, wire);
        attributes.put(name, factory);
    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, OutboundWire wire) {
        return new WireObjectFactory<B>(interfaze, wire, wireService);
    }

    public void bind(ServletContext servletContext) {
        servletContext.setAttribute(Constants.CONTEXT_ATTRIBUTE, getComponentContext());
        for (Map.Entry<String, ObjectFactory<?>> entry : attributes.entrySet()) {
            servletContext.setAttribute(entry.getKey(), entry.getValue().getInstance());
        }
    }

    public TargetInvoker createTargetInvoker(String targetName,
                                             Operation operation,
                                             InboundWire callbackWire) throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public Object getTargetInstance() throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }
}
