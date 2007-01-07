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
package org.apache.tuscany.core.implementation.composite;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Base implementation of the {@link org.osoa.sca.CompositeContext}
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeContext extends SCA implements CompositeContext {
    protected final CompositeComponent composite;
    protected final WireService wireService;

    public AbstractCompositeContext(final CompositeComponent composite, final WireService wireService) {
        this.composite = composite;
        this.wireService = wireService;
    }

    public String getCompositeName() {
        return composite.getName();
    }

    public String getCompositeURI() {
        throw new UnsupportedOperationException();
    }

    public <T> T locateService(Class<T> serviceInterface, String serviceName) throws ServiceRuntimeException {
        String name = serviceInterface.getName();
        QualifiedName qName = new QualifiedName(serviceName);
        SCAObject child = composite.getChild(qName.getPartName());
        InboundWire wire = getInboundWire(child, name, qName.getPortName());
        if (wire.isOptimizable()
            && wire.getServiceContract().getInterfaceClass() != null
            && serviceInterface.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
            try {
                return serviceInterface.cast(wire.getTargetService());
            } catch (TargetResolutionException e) {
                throw new ServiceRuntimeException(e);
            }
        }
        return wireService.createProxy(serviceInterface, wire);
    }

    protected InboundWire getInboundWire(SCAObject child, String name, String serviceName) {
        InboundWire wire = null;
        if (child instanceof Component) {
            wire = ((Component) child).getInboundWire(name);
            if (wire == null) {
                String qName = serviceName + QualifiedName.NAME_SEPARATOR + name;
                throw new ServiceRuntimeException("Service not found [" + qName + "]");
            }
        } else if (child instanceof Service) {
            Service service = (Service) child;
            for (ServiceBinding binding : service.getServiceBindings()) {
                if (Wire.LOCAL_BINDING.equals(binding.getInboundWire().getBindingType())) {
                    wire = binding.getInboundWire();
                    break;
                }
            }
            if (wire == null) {
                throw new ServiceRuntimeException("Local binding for service not found [" + name + "]");
            }
        } else if (child instanceof ReferenceBinding) {
            wire = ((ReferenceBinding) child).getInboundWire();
        } else if (child == null) {
            throw new ServiceRuntimeException("Service not found [" + serviceName + "]");
        } else {
            throw new ServiceRuntimeException("Invalid service type [" + child.getClass().getName() + "]");
        }
        return wire;
    }

}
