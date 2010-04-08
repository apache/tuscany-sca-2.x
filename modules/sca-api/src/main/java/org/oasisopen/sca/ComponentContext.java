/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

import java.util.Collection;

/**
 * The ComponentContext interface is used to obtain contextual information
 * about the SCA component which is executing at the time the API is
 * invoked.
 * 
 * <p>Note: An SCA component can obtain a service reference either through
 * injection or programmatically through the ComponentContext API. Using
 * reference injection is the recommended way to access a service, since it
 * results in code with minimal use of middleware APIs.  The ComponentContext
 * API is provided for use in cases where reference injection is not possible.
 */
public interface ComponentContext {

    /**
     * Returns the absolute URI of the component within the SCA domain.
     * 
     * @return the absolute URI of the component within the SCA domain.
     */
    String getURI();

    /**
     * Returns a typed service proxy object for a reference defined by the
     * current component, where the reference has multiplicity 0..1 or 1..1.
     *
     * @param     <B> the Java type that is implemented by the returned proxy
     *            object.
     * @param     businessInterface the Class object for the Java type that
     *            is implemented by the returned proxy object.
     * @param     referenceName the name of the service reference.
     * @return    a proxy for the reference defined by the current component.
     *            Returns null if the named reference has no target service
     *            configured.
     * @exception IllegalArgumentException if the reference has multiplicity
     *            greater than one, or the component does not have the reference
     *            named by <code>referenceName</code>, or the interface of the named
     *            reference is not compatible with the interface supplied in
     *            the <code>businessInterface</code> parameter. 
     */
    <B> B getService(Class<B> businessInterface, String referenceName)
    		throws IllegalArgumentException;

    /**
     * Returns a ServiceReference object for a reference defined by the current
     * component, where the reference has multiplicity 0..1 or 1..1.
     * 
     * @param     <B> the Java type of the reference that is associated with
     *            the returned object.
     * @param     businessInterface the Class object for the Java type that
     *            is associated with the returned object.
     * @param     referenceName the name of the service reference.
     * @return    a ServiceReference object for a reference defined by the current
     *            component, where the reference has multiplicity 0..1 or 1..1.
     *            Returns null if the named reference has no target service
     *            configured.
     * @exception IllegalArgumentException if the reference has multiplicity
     *            greater than one, or the component does not have the reference
     *            named by <code>referenceName</code>, or the interface of the named
     *            reference is not compatible with the interface supplied in
     *            the <code>businessInterface</code> parameter.
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, 
                                                String referenceName)
            throws IllegalArgumentException;

    /**
     * Returns a list of typed service proxies for a reference defined by the current
     * component, where the reference has multiplicity 0..n or 1..n.
     *
     * @param     <B> the Java type that is implemented by the returned proxy
     *            objects.
     * @param     businessInterface the Class object for the Java type that
     *            is implemented by the returned proxy objects.
     * @param     referenceName the name of the service reference.
     * @return    a collection of proxy objects for the reference, one for each target
     *            service to which the reference is wired, where each proxy object
     *            implements the interface B contained in the 
     *            <code>businessInterface</code> parameter. The collection is empty if the
     *            reference is not wired to any target services.
     * @exception IllegalArgumentException if the reference has multiplicity
     *            greater other than 0..1 or 1..1, or the component does not have the reference
     *            named by <code>referenceName</code>, or the interface of the named
     *            reference is not compatible with the interface supplied in
     *            the <code>businessInterface</code> parameter.
     */
    <B> Collection<B> getServices(Class<B> businessInterface,
                                  String referenceName)
     		throws IllegalArgumentException; 

    /**
     * Returns a list of typed ServiceReference objects for a reference defined by the current
     * component, where the reference has multiplicity 0..n or 1..n.
     *
     * @param     <B> the Java type that is associated with returned proxy
     *            objects.
     * @param     <B> the Java type of the reference that is associated with
     *            the returned object.
     * @param     referenceName the name of the service reference.
     * @return    a collection of ServiceReference objects for the reference, one for each target
     *            service to which the reference is wired, where each proxy object implements
     *            the interface B contained in the <code>businessInterface</code> parameter.
     *            The collection is empty if the reference is not wired to any target services.
     * @exception IllegalArgumentException if the reference has multiplicity
     *            greater other than 0..1 or 1..1, or the component does not have the reference
     *            named by <code>referenceName</code>, or the interface of the named
     *            reference is not compatible with the interface supplied in
     *            the <code>businessInterface</code> parameter.
     */
    <B> Collection<ServiceReference<B>> getServiceReferences(
                       Class<B> businessInterface, String referenceName)
     		throws IllegalArgumentException; 

    /**
     * Returns a ServiceReference that can be used to invoke this component
     * over the designated service.
     *
     * @param     <B> the Java type of the reference that is associated with
     *            the returned object.
     * @param     businessInterface the Class object for the Java type that
     *            is associated with the returned object.
     * @return    a ServiceReference that can be used to invoke this
     *            component over the designated service.
     * @exception IllegalArgumentException if the component does not have a service
     *            which implements the interface identified by the <code>
     *            businessinterface</code> parameter.
     */
    <B> ServiceReference<B> createSelfReference(Class<B> businessInterface)
     		throws IllegalArgumentException;

    /**
     * Returns a ServiceReference that can be used to invoke this component
     * over the designated service. The <code>serviceName</code> parameter explicitly names
     * the service with which the returned ServiceReference is associated.
     *
     * @param     <B> the Java type of the reference that is associated with
     *            the returned object.
     * @param     businessInterface the Class object for the Java type that
     *            is associated with the returned object.
     * @param     serviceName the service name with which the returned ServiceReference
     *            is associated.
     * @return    a ServiceReference that can be used to invoke this component
     *            over the designated service.
     * @exception IllegalArgumentException if the component does not have a service
     *            with the name identified by the <code>serviceName</code> parameter, or
     *            if the named service does not implement the interface identified by the
     *            <code>businessinterface</code> parameter.
     */
    <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, 
                                                String serviceName)
     		throws IllegalArgumentException;

    /**
     * Returns the value of an SCA property defined by this component.
     *
     * @param     <B> the property type.
     * @param     type the Class object for the property type.
     * @param     propertyName the property name.
     * @return    The value of an SCA property defined by this component, or null if
     *            the property is not configured.
     * @exception IllegalArgumentException if the component does not have a property
     *            with the name identified by the <code>propertyName</code> parameter, or
     *            if the named property type is not compatible with the <code>type</code>
     *            parameter.
     */
    <B> B getProperty(Class<B> type, String propertyName)
		    throws IllegalArgumentException;

    /**
     * Casts a type-safe reference to a ServiceReference.
     *
     * @param     <B> the Java type of the reference that is associated with
     *            the returned object.
     * @param     target the type-safe reference proxy that implements interface <B>.
     * @return    a  type-safe reference to a ServiceReference.
     */
    <B> ServiceReference<B> cast(B target) 
            throws IllegalArgumentException;

    /**
     * Returns the RequestContext for the current SCA service request.
     *
     * @return    the RequestContext for the current SCA service request when
     *            invoked during the execution of a component service method or
     *            callback method. Returns null in all other cases.
     */
    RequestContext getRequestContext();
}
