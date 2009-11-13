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

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSObject;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSObjectReferenceProvider implements WireFormatProvider {
    private ExtensionPointRegistry registry;
    private RuntimeEndpointReference endpointReference;
    private JMSBinding binding;
    private InterfaceContract interfaceContract; 
    
    private HashMap<String,String> singleArgMap; //map of one arg operations, leave empty if wrapSingleInput is true

    public WireFormatJMSObjectReferenceProvider(ExtensionPointRegistry registry,
                                               RuntimeEndpointReference endpointReference) {
        super();
        this.registry = registry;
        this.endpointReference = endpointReference;
        this.binding = (JMSBinding)endpointReference.getBinding();
        
        this.singleArgMap = new HashMap<String,String>();
        ComponentReference reference = endpointReference.getReference();
        
        // configure the reference based on this wire format
        
        // currently maintaining the message processor structure which 
        // contains the details of jms message processing however override 
        // any message processors specified in the SCDL in this case
        if (this.binding.getRequestWireFormat() instanceof WireFormatJMSObject){
            this.binding.setRequestMessageProcessorName(JMSBindingConstants.OBJECT_MP_CLASSNAME);
            
            //we don't need to create this map if wrapSingleInput is true
            if (!((WireFormatJMSObject) this.binding.getRequestWireFormat()).isWrappedSingleInput()){
                List<Operation> opList = reference.getReference().getInterfaceContract().getInterface().getOperations();
                
                for (Operation op: opList) {
                    if (op.getInputType().getLogical().size() == 1){
                        this.singleArgMap.put(op.getName(), "");
                    }
                }
            } 
        }
        if (this.binding.getResponseWireFormat() instanceof WireFormatJMSObject){
            this.binding.setResponseMessageProcessorName(JMSBindingConstants.OBJECT_MP_CLASSNAME);
        }
        
        // just point to the reference interface contract so no 
        // databinding transformation takes place
        interfaceContract = reference.getReference().getInterfaceContract();
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

    public Interceptor createInterceptor() {
        return new WireFormatJMSObjectReferenceInterceptor(registry, null, endpointReference, this.singleArgMap);
    }

    public String getPhase() {
        return Phase.REFERENCE_BINDING_WIREFORMAT;
    }

    public InterfaceContract getWireFormatInterfaceContract() {
        return interfaceContract;
    }
}
