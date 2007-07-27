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
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class Axis2ServiceBindingProvider implements ServiceBindingProvider2 {

    private WebServiceBinding wsBinding;
    private Axis2ServiceClient axisClient;
    private Axis2ServiceProvider axisProvider;

    public Axis2ServiceBindingProvider(RuntimeComponent component,
                                       RuntimeComponentService service,
                                       WebServiceBinding wsBinding,
                                       ServletHost servletHost,
                                       MessageFactory messageFactory) {

        this.wsBinding = wsBinding;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = service.getInterfaceContract().makeUnidirectional(wsBinding.isCallback());
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
            axisProvider = new Axis2ServiceProvider(component, service, wsBinding, servletHost,
                                                    messageFactory);
        } else {
            // pass null as last parameter because SCDL doesn't allow a callback callback binding
            // to be specified for a callback binding, i.e., can't have the following:
            // <service>
            //   <binding.x/>
            //   <callback>
            //     <binding.y/>
            //     <callback>
            //       <binding.z/>
            //     </callback>
            //   </callback>
            // </service>
            // This means that you can't do a callback from a callback (at least not
            // in s spec-compliant way).
            axisClient = new Axis2ServiceClient(component, service, wsBinding, servletHost,
                                                messageFactory, null);
        }
    }

    public void start() {
        if (!wsBinding.isCallback()) {
            axisProvider.start();                                          
        } else {
            axisClient.start();
        }
    }

    public void stop() {
        if (!wsBinding.isCallback()) {
            axisProvider.stop();
        } else {
            axisClient.stop();
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public Invoker createCallbackInvoker(Operation operation) {
        if (!wsBinding.isCallback()) {
            throw new RuntimeException("Cannot create callback invoker for a forward binding");
        }
        return axisClient.createInvoker(operation);
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

}
