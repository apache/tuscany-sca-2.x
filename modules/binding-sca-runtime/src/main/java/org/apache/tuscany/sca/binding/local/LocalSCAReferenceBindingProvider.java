package org.apache.tuscany.sca.binding.local;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.binding.local.LocalSCABindingInvoker;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceAsyncProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceUnavailableException;

public class LocalSCAReferenceBindingProvider implements EndpointReferenceAsyncProvider {
    private RuntimeEndpointReference endpointReference;

    private InterfaceContractMapper interfaceContractMapper;
    private ExtensionPointRegistry extensionPoints;
    private Mediator mediator;

    public LocalSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints, RuntimeEndpointReference endpointReference, SCABindingMapper mapper) {
        this.extensionPoints = extensionPoints;
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
        this.mediator = utilities.getUtility(Mediator.class);

        this.endpointReference = endpointReference;
    }

    @Override
    public InterfaceContract getBindingInterfaceContract() {
        RuntimeEndpoint endpoint = (RuntimeEndpoint) endpointReference.getTargetEndpoint();
        if (endpoint != null) {
            return endpoint.getComponentTypeServiceInterfaceContract();
        } else {
            return endpointReference.getComponentTypeReferenceInterfaceContract();
        }
    }

    @Override
    public Invoker createInvoker(Operation operation) {
        Invoker result = null;

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
                }
                // it turns out that the chain source and target operations are
                // the same, and are the operation
                // from the target, not sure if thats by design or a bug. The
                // SCA binding invoker needs to know
                // the source and target class loaders so pass in the real
                // source operation in the constructor
                result = chain == null ? null : new LocalSCABindingInvoker(chain, operation, mediator, passByValue, endpointReference, extensionPoints);
            }
        }

        if (result == null) {
            throw new ServiceUnavailableException("Unable to create SCA binding invoker for local target " + endpointReference.getComponent().getName() + " reference "
                    + endpointReference.getReference().getName() + " (bindingURI=" + endpointReference.getBinding().getURI() + " operation=" + operation.getName() + ")");
        }

        return result;
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
