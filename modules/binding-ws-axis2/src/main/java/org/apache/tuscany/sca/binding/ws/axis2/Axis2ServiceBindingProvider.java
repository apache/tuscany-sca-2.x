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

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class Axis2ServiceBindingProvider implements ServiceBindingProvider2 {

    private WebServiceBinding wsBinding;
    private Axis2ServiceProvider axisProvider;

    public Axis2ServiceBindingProvider(RuntimeComponent component,
                                       RuntimeComponentService service,
                                       WebServiceBinding wsBinding,
                                       ServletHost servletHost,
                                       MessageFactory messageFactory) {

        this.wsBinding = wsBinding;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = service.getInterfaceContract().makeUnidirectional(false);
            if ((contract instanceof JavaInterfaceContract)) {
                contract = Java2WSDLHelper.createWSDLInterfaceContract((JavaInterfaceContract)contract);
            }
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());

        axisProvider = new Axis2ServiceProvider(component, service, wsBinding, servletHost, messageFactory);
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

    public Invoker createCallbackInvoker(Operation operation) {
        throw new UnsupportedOperationException();
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

}
