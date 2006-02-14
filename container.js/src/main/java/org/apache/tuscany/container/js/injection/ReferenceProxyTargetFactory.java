/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.js.injection;

import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.ObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.ConfiguredReference;

/**
 * Returns a service component reference target for injection onto a component implementation instance. The target may be a proxy or an actual
 * component implementation instance.
 */
public class ReferenceProxyTargetFactory<T> implements ObjectFactory<T> {

    // the proxy factory for the reference
    private ProxyFactory<T> factory;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ReferenceProxyTargetFactory(ConfiguredReference reference) throws FactoryInitException {
        // FIXME how to handle a reference that is a list - may take different proxy factories for each entry
        assert (reference != null) : "Reference was null";

        // FIXME should not need the cast to ProxyFactory
        factory = (ProxyFactory) reference.getProxyFactory();
        if (factory == null) {
            throw new FactoryInitException("No proxy factory found");
        }
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public T getInstance() throws ObjectCreationException {
        try {
            return factory.createProxy();
        } catch (ProxyCreationException e) {
            throw new ObjectCreationException(e);
        }
    }

}
