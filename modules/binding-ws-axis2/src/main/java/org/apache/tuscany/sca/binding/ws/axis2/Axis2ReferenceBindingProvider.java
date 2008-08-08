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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.ServiceRuntimeException;

public class Axis2ReferenceBindingProvider implements ReferenceBindingProvider {

    private WebServiceBinding wsBinding;
    private Axis2ServiceClient axisClient;

    public Axis2ReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding,
                                         ModelFactoryExtensionPoint modelFactories,
                                         List<PolicyHandlerTuple> policyHandlerClassnames,
                                         DataBindingExtensionPoint dataBindings) {

        MessageFactory messageFactory = modelFactories.getFactory(MessageFactory.class); 
        this.wsBinding = wsBinding;

        // A WSDL document should always be present in the binding
        if (wsBinding.getWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + component.getName() + "/" + reference.getName());
        }

        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract.getInterface() != null) {
            contract.getInterface().resetDataBinding(OMElement.class.getName());
        }

        axisClient = new Axis2ServiceClient(component, reference, wsBinding, messageFactory, policyHandlerClassnames);
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

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public Invoker createInvoker(Operation operation) {
        return axisClient.createInvoker(operation);
    }

}
