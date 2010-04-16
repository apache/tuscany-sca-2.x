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

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Service.Mode;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@WebServiceProvider
@ServiceMode(Mode.MESSAGE)
public class JAXWSBindingProvider implements Provider<SOAPMessage> {
    private MessageFactory messageFactory;
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;
    private javax.xml.soap.MessageFactory soapMessageFactory;
    private SOAPFactory soapFactory;

    public JAXWSBindingProvider(RuntimeEndpoint endpoint,
                                       ServletHost servletHost,
                                       FactoryExtensionPoint modelFactories,
                                       DataBindingExtensionPoint dataBindings) {

        this.messageFactory = modelFactories.getFactory(MessageFactory.class);

        this.soapMessageFactory = modelFactories.getFactory(javax.xml.soap.MessageFactory.class);
        this.soapFactory = modelFactories.getFactory(SOAPFactory.class);

        // soapMessageFactory = javax.xml.soap.MessageFactory.newInstance();
        // soapFactory = SOAPFactory.newInstance();

        this.endpoint = endpoint;
        this.wsBinding = (WebServiceBinding)endpoint.getBinding();

        // A WSDL document should always be present in the binding
        if (wsBinding.getWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + endpoint.getURI());
        }

        // Set to use the DOM data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(Node.class.getName());
        
        // Can we safely assume there is only one port because you configure
        // a binding in the following ways: 
        // 1/ default             - one port generated = host domain : host port / structural path 
        // 2/ uri="absolute addr" - one port generated = host domain : uri port  / uri path
        // 3/ uri="relative addr" - one port generated = host domain : host port / structural path / relative path
        // 4/ wsdl.binding        - one port generated = host domain : host port / structural path 
        // 5/ wsdl.port           - one port generated = host domain : port port / port path
        // 6/ wsa:Address         - one port generated = host domain : address port / address path
        // 7/ 4 + 6               - as 6

        // TODO the binding URI will currently have been calculated during build
        // however we don't give the provider a chance to get in and effect the
        // calculation (see above comment). For now just fake the addition of binding 
        // specific processing by adding a root if it's not already present
        if (!wsBinding.getURI().startsWith("http://")) {
            wsBinding.setURI("http://localhost:8085" + wsBinding.getURI());
        }
        System.out.println("Service URI: " + wsBinding.getURI());
    }

    public void start() {
        // TODO - do we need this?
    }

    public void stop() {
        // TODO - do we need this?
    }

    public SOAPMessage invoke(SOAPMessage request) {
        try {
            // Assuming document-literal-wrapper style
            Node root = request.getSOAPBody().getFirstChild();
            String operationName = root.getLocalName();
            Operation operation = null;
            for (InvocationChain invocationChain : endpoint.getInvocationChains()) {
                if (operationName.equals(invocationChain.getSourceOperation().getName())) {
                    operation = invocationChain.getSourceOperation();
                    break;
                }
            }
            if (operation == null) {
                throw new SOAPException("Operation not found: " + operationName);
            }

            Message requestMsg = messageFactory.createMessage();
            Object[] body = new Object[]{root};
            requestMsg.setBody(body);
            requestMsg.setOperation(operation);
            Message responseMsg = endpoint.invoke(operation, requestMsg);
            SOAPMessage response = soapMessageFactory.createMessage();
            if (responseMsg.isFault()) {
                FaultException fe = responseMsg.getBody();
                SOAPFault fault = response.getSOAPBody().addFault(new QName(response.getSOAPBody().getNamespaceURI(), "Server"), "unknown");
                Detail d = fault.addDetail();
                DetailEntry de = d.addDetailEntry(fe.getFaultName());
                SOAPElement dece = de.addChildElement("message");
                if (fe.getMessage() != null) {
                    dece.addTextNode(fe.getMessage());
                }
            } else {
                Element element = responseMsg.getBody();
                response.getSOAPBody().addChildElement(soapFactory.createElement(element));
            }
            return response;
        } catch (SOAPException e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
