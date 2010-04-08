/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

/**
 * The ServiceReference interface represents a component reference.
 * It can be injected using the @Reference annotation
 * on a field, a setter method, or constructor parameter taking the
 * type ServiceReference.
 *
 * @param     <B> the type of the service reference
 */
public interface ServiceReference<B> extends java.io.Serializable {

    /**
     * Returns a type-safe reference to the target of this reference.
     * The instance returned is guaranteed to implement the business
     * interface for this reference.  The value returned is a proxy
     * to the target that implements the business interface associated
     * with this reference.
     * 
     * @return a type-safe reference to the target of this reference.
     */
    B getService();

    /**
     * Returns the Java class for the business interface associated
     * with this reference.
     * 
     * @return the Java class for the business interface associated
     *         with this reference.
     */
    Class<B> getBusinessInterface();
}
