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
package org.apache.tuscany.core.binding.local;

import java.net.URI;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.util.UriHelper;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Dispatches an invocation through a composite service or reference using the local binding
 *
 * @version $Rev$ $Date$
 */
public class LocalTargetInvoker extends AbstractLocalTargetInvoker {
    private InvocationChain chain;
    private URI fromAddress;
    private boolean contractHasCallback;

    public LocalTargetInvoker(Operation operation, Wire wire) {
        assert operation != null;
        chain = wire.getInvocationChains().get(operation);
        assert chain != null;
        if (wire.getSourceUri() != null) {
            fromAddress = URI.create(UriHelper.getBaseName(wire.getSourceUri()));
        }
        contractHasCallback = !wire.getCallbackInvocationChains().isEmpty();
    }

    @Override
    public LocalTargetInvoker clone() throws CloneNotSupportedException {
        return (LocalTargetInvoker) super.clone();
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            TargetInvoker invoker = chain.getTargetInvoker();
            assert invoker != null;
            // Pushing the from address only needs to happen in the outbound (forward) direction for callbacks
            if (contractHasCallback) {
                //JFM do we need this?
                msg.pushCallbackUri(fromAddress);
            }

            return invoke(chain, invoker, msg);
        } catch (Throwable e) {
            Message faultMsg = new MessageImpl();
            faultMsg.setBodyWithFault(e);
            return faultMsg;
        }
    }


    public boolean isOptimizable() {
        return true;
    }
}
