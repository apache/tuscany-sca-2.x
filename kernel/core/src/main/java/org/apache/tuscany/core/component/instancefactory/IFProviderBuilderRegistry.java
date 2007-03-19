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
package org.apache.tuscany.core.component.instancefactory;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Registry for instance factory builders.
 * 
 * @version $Revision$ $Date: 2007-03-12 22:26:18 +0000 (Mon, 12 Mar
 *          2007) $
 */
public interface IFProviderBuilderRegistry {

    /**
     * Registers an instance factory provider builder.
     * 
     * @param ifpdClass Instance factory provider definition class.
     * @param builder Instance factory provider builder.
     */
    <IFPD extends InstanceFactoryProviderDefinition> void register(Class<IFPD> ifpdClass,
                                                                   IFProviderBuilder<?, IFPD> builder);

    /**
     * Builds an instnace factory provider from a definition.
     * 
     * @param providerDefinition Provider definition.
     * @param cl Clasloader to use.
     * @return Instance factory provider.
     * @param <T> the type of instance the InstanceFactory creates
     */
    <T> InstanceFactoryProvider<T> build(InstanceFactoryProviderDefinition<T> providerDefinition, ClassLoader cl)
        throws IFProviderBuilderException;
}
