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
package org.apache.tuscany.sca.binding.axis2;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

public class Axis2ReferenceBindingProvider implements ReferenceBindingProvider2 {

    private WebServiceBinding wsBinding;
    private Axis2ServiceClient axisClient;

    public Axis2ReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding,
                                         ServletHost servletHost,
                                         MessageFactory messageFactory) {

        this.wsBinding = wsBinding;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract().makeUnidirectional(false);
            if ((contract instanceof JavaInterfaceContract)) {
                contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract);
            }
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());

        // look for a matching callback binding
        WebServiceBinding callbackBinding = null;
        if (reference.getCallback() != null) {
            for (Binding binding : reference.getCallback().getBindings()) {
                if (binding instanceof WebServiceBinding) {
                    // set the first compatible callback binding
                    callbackBinding = (WebServiceBinding)binding;
                    continue;
                }
            }
        }

        axisClient =
            new Axis2ServiceClient(component, reference, wsBinding, servletHost, messageFactory, callbackBinding);
    }

    public void start() {
        axisClient.start();
    }

    public void stop() {
        axisClient.stop();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

    @Deprecated
    public Invoker createInvoker(Operation operation, boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        } else {
            return createInvoker(operation);
        }
    }

    public Invoker createInvoker(Operation operation) {
        return axisClient.createInvoker(operation);
    }

}
