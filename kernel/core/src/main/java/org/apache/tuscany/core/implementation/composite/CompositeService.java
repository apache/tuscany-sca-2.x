/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.Method;

import org.apache.tuscany.core.injection.WireObjectFactory;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.idl.java.JavaIDLUtils;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

public class CompositeService<T> extends ServiceExtension<T> {
    
    private WorkContext workContext;

    public CompositeService(String name,
                       Class<T> interfaze,
                       CompositeComponent parent,
                       WireService wireService,
                       WorkContext workContext) throws CoreRuntimeException {
        super(name, interfaze, parent, wireService);
        this.workContext = workContext;
    }
    
    /**
     * A service for a remote binding does not need a target invoker, but a
     * bindless service does because it gets wired localy from a reference (or from
     * a parent service?!)
     * We just reuse CompositeReferenceTargetInvoker for now since it seems the target
     * invoker we need does the same thing, if this is confirmed we should give it
     * a common name
     * FIXME !!! Notice that this method is not defined in the SPI !!!
     */
    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        WireObjectFactory wireFactory = new WireObjectFactory(outboundWire, wireService);
        Method method = JavaIDLUtils.findMethod(operation, contract.getInterfaceClass().getMethods());
        return new CompositeReferenceTargetInvoker(method, inboundWire, wireFactory, workContext);
    }
    
    /**
     */
    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation) {
        Method method = JavaIDLUtils.findMethod(operation, contract.getCallbackClass().getMethods());
        return new CompositeReferenceCallbackTargetInvoker(method, contract, inboundWire, wireService, workContext);
    }
    
    public T getServiceInstance() throws TargetException {
        return wireService.createProxy(outboundWire);
    }
}
