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

package org.apache.tuscany.sca.binding.jms.wireformat.jmsobject.runtime;


import java.util.HashMap;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSObject;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSObjectServiceProvider implements WireFormatProvider {
    private ExtensionPointRegistry registry;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private JMSBinding binding;
    private InterfaceContract interfaceContract; 
    private HashMap<String,Class<?>> singleArgMap;
    private boolean wrapSingle = true;

    public WireFormatJMSObjectServiceProvider(ExtensionPointRegistry registry,
                                             RuntimeComponent component, 
                                             RuntimeComponentService service, 
                                             Binding binding) {
        super();
        this.registry = registry;
        this.component = component;
        this.service = service;
        this.binding = (JMSBinding)binding;
        this.singleArgMap = new HashMap<String,Class<?>>();
        
        // configure the service based on this wire format
        
        // currently maintaining the message processor structure which 
        // contains the details of jms message processing however override 
        // any message processors specified in the SCDL in this case
        if (this.binding.getRequestWireFormat() instanceof WireFormatJMSObject){
            this.binding.setRequestMessageProcessorName(JMSBindingConstants.OBJECT_MP_CLASSNAME);
            
            List<Operation> opList = service.getService().getInterfaceContract().getInterface().getOperations();
            
            for (Operation op: opList) {
                if (op.getInputType().getLogical().size() == 1){
                    this.singleArgMap.put(op.getName(), op.getInputType().getLogical().get(0).getPhysical());
                }
            }
            
            wrapSingle = ((WireFormatJMSObject) this.binding.getRequestWireFormat()).isWrappedSingleInput();
            
        }
        if (this.binding.getResponseWireFormat() instanceof WireFormatJMSObject){
            this.binding.setResponseMessageProcessorName(JMSBindingConstants.OBJECT_MP_CLASSNAME);
        }         

        // just point to the reference interface contract so no 
        // databinding transformation takes place
        interfaceContract = service.getService().getInterfaceContract();

        
    }
    
    public InterfaceContract configureWireFormatInterfaceContract(InterfaceContract interfaceContract){
        
        if (this.interfaceContract != null ) {
            if (this.binding.getRequestWireFormat() instanceof WireFormatJMSObject){
                // set the request data transformation
                interfaceContract.getInterface().resetInterfaceInputTypes(this.interfaceContract.getInterface());
            }
            if (this.binding.getResponseWireFormat() instanceof WireFormatJMSObject){
                // set the response data transformation
                interfaceContract.getInterface().resetInterfaceOutputTypes(this.interfaceContract.getInterface());
            }
        }
        
        return interfaceContract;
    }    

    /**
     */
    public Interceptor createInterceptor() {
        
        return new WireFormatJMSObjectServiceInterceptor(registry, (JMSBinding)binding, null,service.getRuntimeWire(binding), 
                this.singleArgMap, wrapSingle );
    }

    /**
     */
    public String getPhase() {
        return Phase.SERVICE_BINDING_WIREFORMAT;
    }

    public InterfaceContract getWireFormatInterfaceContract() {
        return interfaceContract;
    }
}
