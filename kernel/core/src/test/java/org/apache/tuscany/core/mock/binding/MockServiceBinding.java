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
package org.apache.tuscany.core.mock.binding;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class MockServiceBinding extends AbstractSCAObject implements ServiceBinding {
    private InboundWire inboundWire;
    private OutboundWire outboundWire;
    private ServiceContract<?> bindingServiceContract;


    public MockServiceBinding() {
        super("foo", null);
    }

    public QName getBindingType() {
        return null;
    }

    public void setService(Service service) {
    }

    public ServiceContract<?> getBindingServiceContract() {
        return bindingServiceContract;
    }

    public InboundWire getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire inboundWire) {
        this.inboundWire = inboundWire;
    }

    public OutboundWire getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire outboundWire) {
        this.outboundWire = outboundWire;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation)
        throws TargetInvokerCreationException {
        return null;
    }

    public Scope getScope() {
        return null;
    }
}
