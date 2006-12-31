package org.apache.tuscany.spi.component;

import java.util.List;

import org.apache.tuscany.spi.model.ServiceContract;

/**
 * The runtime instantiation of an SCA reference
 *
 * @version $Rev$ $Date$
 */
public interface Reference extends SCAObject {

    /**
     * Returns the contract for the reference.
     *
     * @return the contract for the reference.
     */
    ServiceContract<?> getServiceContract();

    /**
     * Returns the collection of bindings configured for the reference.
     *
     * @return the collection of bindings configured for the reference.
     */
    List<ReferenceBinding> getReferenceBindings();

    /**
     * Adds a binding the reference is configured with.
     *
     * @param binding the  binding the reference is configured with.
     */
    void addReferenceBinding(ReferenceBinding binding);
}
