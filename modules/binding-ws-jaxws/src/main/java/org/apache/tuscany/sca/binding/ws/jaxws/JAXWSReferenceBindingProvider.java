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
package org.apache.tuscany.sca.binding.ws.jaxws;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Node;

public class JAXWSReferenceBindingProvider implements ReferenceBindingProvider {

    private javax.xml.soap.MessageFactory messageFactory;
    private WebServiceBinding wsBinding;
    private Dispatch<SOAPMessage> dispatch;

    public JAXWSReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding,
                                         FactoryExtensionPoint modelFactories,
                                         DataBindingExtensionPoint dataBindings) {

        this.messageFactory = modelFactories.getFactory(javax.xml.soap.MessageFactory.class);
        this.wsBinding = wsBinding;

        // A WSDL document should always be present in the binding
        if (wsBinding.getWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + component.getName() + "/" + reference.getName());
        }

        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract.getInterface() != null) {
            contract.getInterface().resetDataBinding(Node.class.getName());
        }

        Service service = Service.create(wsBinding.getServiceName());
        JAXBContext context = null;
        Dispatch<Object> dispath = service.createDispatch(new QName(wsBinding.getServiceName().getNamespaceURI(), wsBinding.getPortName()), context,Service.Mode.MESSAGE);
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
        return new JAXWSBindingInvoker(dispatch, null, null, messageFactory, wsBinding);
    }

}
