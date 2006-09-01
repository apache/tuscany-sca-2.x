/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.idl.java.JavaIDLUtils;

import org.apache.tuscany.core.injection.WireObjectFactory;

public class CompositeReference<T> extends ReferenceExtension<T> {

    public CompositeReference(String name,
                              CompositeComponent<?> parent,
                              WireService wireService,
                              ServiceContract contract) {
        super(name, (Class<T>) contract.getInterfaceClass(), parent, wireService);
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        WireObjectFactory wireFactory = new WireObjectFactory(outboundWire, wireService);
        Method method = JavaIDLUtils.findMethod(operation, contract.getInterfaceClass().getMethods());
        return new CompositeReferenceTargetInvoker(method, wireFactory);
    }
}
