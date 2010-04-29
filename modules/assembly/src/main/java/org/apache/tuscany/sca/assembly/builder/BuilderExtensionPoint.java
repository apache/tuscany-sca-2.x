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

package org.apache.tuscany.sca.assembly.builder;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Implementation;

/**
 * An extension point for Composite builders.
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public interface BuilderExtensionPoint {

    /**
     * Adds a composite builder.
     * 
     * @param compositeBuilder
     */
    void addCompositeBuilder(CompositeBuilder compositeBuilder);

    /**
     * Removes a composite builder.
     *  
     * @param compositeBuilder
     */
    void removeCompositeBuilder(CompositeBuilder compositeBuilder);

    /**
     * Returns the composite builder with the given id.
     * 
     * @param id
     * @return
     */
    CompositeBuilder getCompositeBuilder(String id);
    
    /**
     * Add a binding builder
     * @param bindingBuilder
     */
    void addBindingBuilder(BindingBuilder<?> bindingBuilder);

    /**
     * Look up a binding builder by the binding type
     * @param <B>
     * @param bindingType
     * @return
     */
    <B extends Binding> BindingBuilder<B> getBindingBuilder(QName bindingType);

    /**
     * Remove a binding builder
     * @param <B>
     * @param builder
     */
    <B extends Binding> void removeBindingBuilder(BindingBuilder<B> builder);

    /**
     * Add an implementation builder
     * @param implementationBuilder
     */
    void addImplementationBuilder(ImplementationBuilder<?> implementationBuilder);

    /**
     * Look up an implementation builder by implementation type
     * @param <I>
     * @param implementationType
     * @return
     */
    <I extends Implementation> ImplementationBuilder<I> getImplementationBuilder(QName implementationType);

    /**
     * Remove an implementation builder
     * @param <I>
     * @param builder
     */
    <I extends Implementation> void removeImplementationBuilder(ImplementationBuilder<I> builder);
    
    /**
     * Add a policy builder
     * @param policyBuilder
     */
    void addPolicyBuilder(PolicyBuilder<?> policyBuilder);

    /**
     * Look up a Policy builder by the Policy type
     * @param <P>
     * @param policyType
     * @return
     */
    <P> PolicyBuilder<P> getPolicyBuilder(QName policyType);

    /**
     * Remove a Policy builder
     * @param <P>
     * @param builder
     */
    <P> void removePolicyBuilder(PolicyBuilder<P> builder);
    
    /**
     * Get a collection of policy builders
     * @return
     */
    public Collection<PolicyBuilder> getPolicyBuilders();
}
