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
package org.apache.tuscany.sca.binding.ws.jaxws.ri;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.jaxws.JAXWSBindingInvoker;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Node;

public class JAXWSReferenceBindingProvider implements ReferenceBindingProvider {

    private javax.xml.soap.MessageFactory messageFactory;
    private WebServiceBinding wsBinding;

    public JAXWSReferenceBindingProvider(RuntimeEndpointReference endpointReference,
                                         FactoryExtensionPoint modelFactories,
                                         DataBindingExtensionPoint dataBindings) {

        this.messageFactory = modelFactories.getFactory(javax.xml.soap.MessageFactory.class);
        this.wsBinding = (WebServiceBinding) endpointReference.getBinding();

        // A WSDL document should always be present in the binding
        if (wsBinding.getGeneratedWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + endpointReference.getURI());
        }

        // Set to use the DOM data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract.getInterface() != null) {
            contract.getInterface().resetDataBinding(Node.class.getName());
        }
    }

    public void start() {
    }

    public void stop() {
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public Invoker createInvoker(Operation operation) {
        return new JAXWSBindingInvoker(operation, null, messageFactory, wsBinding);
    }

}
