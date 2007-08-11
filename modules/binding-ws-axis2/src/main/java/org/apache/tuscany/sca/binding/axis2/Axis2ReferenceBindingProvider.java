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

    private RuntimeComponentReference reference;
    private WebServiceBinding wsBinding;
    private Axis2ServiceClient axisClient;
    private Axis2ServiceProvider axisProvider;
    private WebServiceBinding callbackBinding;

    public Axis2ReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding,
                                         ServletHost servletHost,
                                         MessageFactory messageFactory) {

        this.reference = reference;
        this.wsBinding = wsBinding;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract().makeUnidirectional(wsBinding.isCallback());
            if ((contract instanceof JavaInterfaceContract)) {
                contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract);
            }
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding
        if (contract.getInterface() != null) {
            contract.getInterface().setDefaultDataBinding(OMElement.class.getName());
        }
        if (contract.getCallbackInterface() != null) {
            contract.getCallbackInterface().setDefaultDataBinding(OMElement.class.getName());
        }

        if (!wsBinding.isCallback()) {
            // this is a forward binding, so look for a matching callback binding
            if (reference.getCallback() != null) {
                for (Binding binding : reference.getCallback().getBindings()) {
                    if (binding instanceof WebServiceBinding) {
                        // set the first compatible callback binding
                        setCallbackBinding((WebServiceBinding)binding);
                        continue;
                    }
                }
            }
        } else {
            // this is a callback binding, so look for all matching forward binding
            for (Binding binding : reference.getBindings()) {
                if (reference.getBindingProvider(binding) instanceof Axis2ReferenceBindingProvider) {
                    // set all compatible forward binding providers for this reference
                    ((Axis2ReferenceBindingProvider)reference.getBindingProvider(binding))
                        .setCallbackBinding(wsBinding);
                }
            }
        }

        if (!wsBinding.isCallback()) {
            axisClient =
                new Axis2ServiceClient(component, reference, wsBinding, servletHost, messageFactory, callbackBinding);
        } else {
            // FIXME: need to support callbacks through self-references
            // For now, don't create a callback service provider for a self-reference
            // because this modifies the binding URI. This messes up the service callback
            // wires because the self-reference has the same binding object as the service.
            if (!reference.getName().startsWith("$self$.")) {
                axisProvider = new Axis2ServiceProvider(component, reference, wsBinding, servletHost, messageFactory);
            }
        }
    }

    protected void setCallbackBinding(WebServiceBinding callbackBinding) {
        if (this.callbackBinding == null) {
            this.callbackBinding = callbackBinding;
        }
    }

    public void start() {
        if (!wsBinding.isCallback()) {
            axisClient.start();
        } else {
            // FIXME: need to support callbacks through self-references
            if (!reference.getName().startsWith("$self$.")) {
                axisProvider.start();
            }
        }
    }

    public void stop() {
        if (!wsBinding.isCallback()) {
            axisClient.stop();
        } else {
            // FIXME: need to support callbacks through self-references
            if (!reference.getName().startsWith("$self$.")) {
                axisProvider.stop();
            }
        }
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
        if (wsBinding.isCallback()) {
            throw new RuntimeException("Cannot create invoker for a callback binding");
        }
        return axisClient.createInvoker(operation);
    }

}
