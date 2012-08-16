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

package org.apache.tuscany.sca.binding.local;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.binding.local.LocalSCABindingInvoker;
import org.apache.tuscany.sca.binding.sca.transform.BindingSCATransformer;
import org.apache.tuscany.sca.binding.sca.transform.SameDBCopyTransformer;
import org.apache.tuscany.sca.binding.sca.transform.WSDLMediateTransformer;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.util.OperationDataBindingHelper;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceAsyncProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.ServiceUnavailableException;

/**
*
* @version $Rev$ $Date$
*/
public class DefaultLocalSCAReferenceBindingProvider implements EndpointReferenceAsyncProvider {
    private RuntimeEndpointReference endpointReference;

    protected InterfaceContractMapper interfaceContractMapper;
    protected ExtensionPointRegistry extensionPoints;
    protected Mediator mediator;
    protected InterfaceContract componentTypeRefInterfaceContract;
    protected InterfaceContract wsdlBindingInterfaceContract;      // Computed lazily
    
    public DefaultLocalSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints, RuntimeEndpointReference endpointReference, SCABindingMapper mapper) {
        this.extensionPoints = extensionPoints;
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);     
        this.mediator = utilities.getUtility(Mediator.class);

        this.endpointReference = endpointReference;
    }
    
    protected String getDataBinding() {
        return DOMDataBinding.NAME;
    }

    protected InterfaceContract getWSDLBindingInterfaceContract() {
        if (this.wsdlBindingInterfaceContract != null) {
            return this.wsdlBindingInterfaceContract;
        }
        
        InterfaceContract wsdlInterfaceContract = (WSDLInterfaceContract)endpointReference.getGeneratedWSDLContract(componentTypeRefInterfaceContract);

        // Validation may be unnecessary.  This check may already be guaranteed at this point, not sure.
        Endpoint target = endpointReference.getTargetEndpoint();
        InterfaceContract targetInterfaceContract = target.getComponentServiceInterfaceContract();
        try {
            interfaceContractMapper.checkCompatibility(wsdlInterfaceContract, targetInterfaceContract, 
                                                       Compatibility.SUBSET, true, false);
        } catch (IncompatibleInterfaceContractException exc) {
            throw new ServiceRuntimeException(exc);
        }

        String dataBinding = getDataBinding();

        // Clone
        try {
            wsdlInterfaceContract = (WSDLInterfaceContract)wsdlInterfaceContract.clone();
        } catch (CloneNotSupportedException exc) {
            throw new ServiceRuntimeException(exc);
        }

        if (wsdlInterfaceContract.getInterface() != null) {             
            wsdlInterfaceContract.getInterface().resetDataBinding(dataBinding);
        }
        if (wsdlInterfaceContract.getCallbackInterface() != null) {
            wsdlInterfaceContract.getCallbackInterface().resetDataBinding(dataBinding);
        }
        this.wsdlBindingInterfaceContract = wsdlInterfaceContract;
        
        return wsdlInterfaceContract;
    }

    @Override
    public InterfaceContract getBindingInterfaceContract() {
        // Since we want to disable DataTransformationInterceptor and handle copy in the binding
        this.componentTypeRefInterfaceContract = endpointReference.getComponentTypeReferenceInterfaceContract();
        return componentTypeRefInterfaceContract;
    }

    

    @Override
    public Invoker createInvoker(Operation operation) {
        Invoker result = null;
        BindingSCATransformer bindingTransformer = null; 
            
        Endpoint target = endpointReference.getTargetEndpoint();
        if (target != null) {
            RuntimeComponentService service = (RuntimeComponentService) target.getService();
            if (service != null) { // not a callback wire
                                
                InvocationChain chain = ((RuntimeEndpoint) target).getInvocationChain(operation);

                boolean passByValue = false;
                Operation targetOp = chain.getTargetOperation();
                if (!operation.getInterface().isRemotable()) {
                    if (interfaceContractMapper.isCompatibleByReference(operation, targetOp, Compatibility.SUBSET)) {
                        passByValue = false;
                    }
                } else {
                    Reference ref = endpointReference.getReference().getReference();
                    // The spec says both ref and service needs to
                    // allowsPassByReference
                    boolean allowsPBR = (endpointReference.getReference().isAllowsPassByReference() || (ref != null && ref.isAllowsPassByReference())) && chain.allowsPassByReference();

                    if (allowsPBR && interfaceContractMapper.isCompatibleByReference(operation, targetOp, Compatibility.SUBSET)) {
                        passByValue = false;
                    } else if (interfaceContractMapper.isCompatibleWithoutUnwrapByValue(operation, targetOp, Compatibility.SUBSET)) {
                        passByValue = true;
                    }
                    bindingTransformer = getBindingTransformer(operation, targetOp);
                }
                                                
                // it turns out that the chain source and target operations are
                // the same, and are the operation
                // from the target, not sure if thats by design or a bug. The
                // SCA binding invoker needs to know
                // the source and target class loaders so pass in the real
                // source operation in the constructor
                result = chain == null ? null : new LocalSCABindingInvoker(chain, operation, passByValue, endpointReference, extensionPoints, bindingTransformer);
            }
        }

        if (result == null) {
            throw new ServiceUnavailableException("Unable to create SCA binding invoker for local target " + endpointReference.getComponent().getName() + " reference "
                    + endpointReference.getReference().getName() + " (bindingURI=" + endpointReference.getBinding().getURI() + " operation=" + operation.getName() + ")");
        }

        return result;
    }
    
    protected BindingSCATransformer getBindingTransformer(Operation sourceOperation, Operation targetOperation) {   
    	boolean differentDataBindings = OperationDataBindingHelper.isTransformationRequired(sourceOperation, targetOperation);

    	if (differentDataBindings) { 
    		InterfaceContract bindingInterfaceContract = getWSDLBindingInterfaceContract();   
    		if (!bindingInterfaceContract.getInterface().isRemotable()) {
    			throw new IllegalStateException("This method should only have been called for a remotable interface.");
    		}
    		Operation wsdlBindingOperation = interfaceContractMapper.map(bindingInterfaceContract.getInterface(), sourceOperation);                        
    		return new WSDLMediateTransformer(mediator, sourceOperation, wsdlBindingOperation, targetOperation);                
    	} else {
    		return new SameDBCopyTransformer(mediator,  sourceOperation, targetOperation);
    	}

    }

    @Override
    public boolean supportsOneWayInvocation() {
        // Default for Local invocation
        return false;
    }
    
    @Override
    public boolean supportsNativeAsync() {
        return true;
    }

    @Override
    public void configure() {
        // Nothing required for Local invocation
    }

    @Override
    public void start() {
        // Nothing required for Local invocation
    }

    @Override
    public void stop() {
        // Nothing required for Local invocation
    }
    
    /**
     * Allows us to replace the delegate EPR with the real EPR so that the local binding
     * optimization operates against the correct invocation chains.
     */
    public void setEndpointReference(RuntimeEndpointReference endpointReference){
        this.endpointReference = endpointReference;
    }
    

}
