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

import javax.xml.namespace.QName;

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
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.xsd.XSDFactory;

public class Axis2ServiceBindingProvider implements ServiceBindingProvider {

    private WebServiceBinding wsBinding;
    private Axis2ServiceProvider axisProvider;

    public Axis2ServiceBindingProvider(RuntimeComponent component,
                                       RuntimeComponentService service,
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
            contract = service.getInterfaceContract().makeUnidirectional(false);
            if (contract instanceof JavaInterfaceContract) {
                ModelResolver resolver = component instanceof ResolverExtension ?
                                             ((ResolverExtension)component).getModelResolver() : null;
                contract = Java2WSDLHelper.createWSDLInterfaceContract(
                                   (JavaInterfaceContract)contract,
                                   requiresSOAP12(wsBinding),
                                   resolver,
                                   dataBindings,
                                   wsdlFactory,
                                   xsdFactory);
            } else {
                try {
                    //TUSCANY-2316 Cloning the Interface Contract to avoid overriding data biding information 
                    contract = (InterfaceContract) contract.clone();
                } catch (Exception e) {
                    //ignore
                }
            }
            wsBinding.setBindingInterfaceContract(contract);
        }
        
        // TODO - fix up the conversational flag and operation sequences in case the contract has come from WSDL
        // as we don't yet support requires="conversational" or sca:endConversation annotations
        // in WSDL interface descriptions (see section 1.5.4 of the Assembly Specification V1.0)
        if (service.getInterfaceContract().getInterface() != null ) {
            contract.getInterface().setConversational(service.getInterfaceContract().getInterface().isConversational());
            
            for (Operation operation : contract.getInterface().getOperations()){
                Operation serviceOperation = null;
                
                for (Operation tmpOp : service.getInterfaceContract().getInterface().getOperations()){
                    if ( operation.getName().equals(tmpOp.getName())) {
                        serviceOperation = tmpOp;
                        break;
                    }
                }
                
                if (serviceOperation != null ){
                    operation.setConversationSequence(serviceOperation.getConversationSequence());
                }
            }
        }

        
        // Set to use the Axiom data binding
        contract.getInterface().resetDataBinding(OMElement.class.getName());

        axisProvider = new Axis2ServiceProvider(component, service, wsBinding, servletHost, messageFactory, policyHandlerClassnames);
    }

    public void start() {
        axisProvider.start();                                          
    }

    public void stop() {
        axisProvider.stop();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    private static final QName SOAP12_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0", "soap.1_2");

    protected static boolean requiresSOAP12(WebServiceBinding wsBinding) {
        if (wsBinding instanceof IntentAttachPoint) {
            List<Intent> intents = ((IntentAttachPoint)wsBinding).getRequiredIntents();
            for (Intent intent : intents) {
                if (SOAP12_INTENT.equals(intent.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
