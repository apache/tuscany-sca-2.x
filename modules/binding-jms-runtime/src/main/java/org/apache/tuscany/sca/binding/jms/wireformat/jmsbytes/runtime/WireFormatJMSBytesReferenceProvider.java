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

package org.apache.tuscany.sca.binding.jms.wireformat.jmsbytes.runtime;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSBytes;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSBytesReferenceProvider implements WireFormatProvider {
    private ExtensionPointRegistry registry;
    private RuntimeEndpointReference endpointReference;
    private JMSBinding binding;
    private InterfaceContract interfaceContract; 

    public WireFormatJMSBytesReferenceProvider(ExtensionPointRegistry registry,
                                               RuntimeEndpointReference endpointReference) {
        super();
        this.registry = registry;
        this.endpointReference = endpointReference;
        this.binding = (JMSBinding) endpointReference.getBinding();
        
        // configure the reference based on this wire format
        
        // currently maintaining the message processor structure which 
        // contains the details of jms message processing however overried 
        // any message processors specied in the SCDL in this case
        if (this.binding.getRequestWireFormat() instanceof WireFormatJMSBytes){
            this.binding.setRequestMessageProcessorName(JMSBindingConstants.BYTES_MP_CLASSNAME);
        }
        if (this.binding.getResponseWireFormat() instanceof WireFormatJMSBytes){
            this.binding.setResponseMessageProcessorName(JMSBindingConstants.BYTES_MP_CLASSNAME);
        } 
        
        // just point to the reference interface contract so no 
        // databinding transformation takes place
        interfaceContract = endpointReference.getReference().getReference().getInterfaceContract();
    }
        
    public InterfaceContract configureWireFormatInterfaceContract(InterfaceContract interfaceContract){
        
        if (this.interfaceContract != null ) {
            if (this.binding.getRequestWireFormat() instanceof WireFormatJMSBytes){
                // set the request data transformation
                interfaceContract.getInterface().resetInterfaceInputTypes(this.interfaceContract.getInterface());
            }
            if (this.binding.getResponseWireFormat() instanceof WireFormatJMSBytes){
                // set the response data transformation
                interfaceContract.getInterface().resetInterfaceOutputTypes(this.interfaceContract.getInterface());
            }
        }
        
        return interfaceContract;
    }     

    public Interceptor createInterceptor() {
        return new WireFormatJMSBytesReferenceInterceptor(registry, null, endpointReference);
    }

    public String getPhase() {
        return Phase.REFERENCE_BINDING_WIREFORMAT;
    }

    public InterfaceContract getWireFormatInterfaceContract() {
        return interfaceContract;
    }
}
