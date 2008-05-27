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
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.java2wsdl.Java2WSDLHelper;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.xsd.XSDFactory;

public class Axis2ReferenceBindingProvider implements ReferenceBindingProvider {

    private WebServiceBinding wsBinding;
    private Axis2ServiceClient axisClient;

    public Axis2ReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding,
                                         ServletHost servletHost,
                                         ModelFactoryExtensionPoint modelFactories,
                                         Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames,
                                         DataBindingExtensionPoint dataBindings) {

        MessageFactory messageFactory = modelFactories.getFactory(MessageFactory.class); 
        WSDLFactory wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);
        this.wsBinding = wsBinding;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract().makeUnidirectional(false);
            if (contract instanceof JavaInterfaceContract) {
                ModelResolver resolver = component instanceof ResolverExtension ?
                                             ((ResolverExtension)component).getModelResolver() : null;
                contract = Java2WSDLHelper.createWSDLInterfaceContract(
                                   (JavaInterfaceContract)contract,
                                   Axis2ServiceBindingProvider.requiresSOAP12(wsBinding),
                                   resolver,
                                   dataBindings,
                                   wsdlFactory,
                                   xsdFactory);
            }
            wsBinding.setBindingInterfaceContract(contract);
        }
        
        // TODO - fix up the conversational flag and operation sequences in case the contract has come from WSDL
        // as we don't yet support requires="conversational" or sca:endConversation annotations
        // in WSDL interface descriptions (see section 1.5.4 of the Assembly Specification V1.0)
        if ( reference.getInterfaceContract().getInterface() != null && contract.getInterface() != null) {
            contract.getInterface().setConversational(reference.getInterfaceContract().getInterface().isConversational());
    
            for (Operation operation : contract.getInterface().getOperations()){
                Operation referenceOperation = null;
                
                for (Operation tmpOp : reference.getInterfaceContract().getInterface().getOperations()){
                    if ( operation.getName().equals(tmpOp.getName())) {
                        referenceOperation = tmpOp;
                        break;
                    }
                }
                
                if (referenceOperation != null ){
                    operation.setConversationSequence(referenceOperation.getConversationSequence());
                }
            }        
        }

        // Set to use the Axiom data binding
        if (contract.getInterface() != null) {
            contract.getInterface().resetDataBinding(OMElement.class.getName());
        }

        axisClient = new Axis2ServiceClient(component, reference, wsBinding, servletHost, messageFactory, policyHandlerClassnames);
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
