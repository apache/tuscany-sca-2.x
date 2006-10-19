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

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

public class CompositeService extends ServiceExtension {

    public CompositeService(String name,
                            Class<?> interfaze,
                            CompositeComponent parent,
                            WireService wireService) throws CoreRuntimeException {
        super(name, interfaze, parent, wireService);
    }

    /**
     * A service for a remote binding does not need a target invoker, but a bindless service does because it gets wired
     * localy from a reference (or from a parent service?!) We just reuse CompositeReferenceTargetInvoker for now since
     * it seems the target invoker we need does the same thing, if this is confirmed we should give it a common name
     * FIXME !!! Notice that this method is not defined in the SPI !!!
     */
    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new CompositeReferenceTargetInvoker(operation, inboundWire, outboundWire);
    }

    /**
     */
    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation) {
        return new CompositeReferenceCallbackTargetInvoker(operation, inboundWire);
    }

    public Object getServiceInstance() throws TargetException {
        return interfaze.cast(wireService.createProxy(outboundWire));
    }
}
