package org.apache.tuscany.core.binding.local;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The runtime representaion of the local reference binding
 *
 * @version $Rev$ $Date$
 */
public class LocalReferenceBinding extends ReferenceBindingExtension {

    public LocalReferenceBinding(String name, CompositeComponent parent) throws CoreRuntimeException {
        super(name, parent);
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation)
        throws TargetInvokerCreationException {
        if (operation.isCallback()) {
            return new LocalCallbackTargetInvoker(operation, inboundWire);
        } else {
            return new LocalTargetInvoker(operation, outboundWire);
        }
    }

    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation)
        throws TargetInvokerCreationException {
        return new LocalCallbackTargetInvoker(operation, inboundWire);
    }

}
