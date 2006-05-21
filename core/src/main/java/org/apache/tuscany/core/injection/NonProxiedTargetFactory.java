/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.model.assembly.ConfiguredService;

/**
 * Returns a direct reference to a target within the same composite, i.e. the factory avoids creating proxies and
 * returns the actual target instance
 * 
 * @version $Rev$ $Date$
 */
public class NonProxiedTargetFactory<T> implements ObjectFactory<T> {

    private ContextResolver resolver;

    // the name of the target component/service for this reference
    private QualifiedName targetName;
    private QualifiedName qualifiedServiceName;

    /**
     * Constructs a reference object factory from a configured reference on a type
     * 
     * @throws FactoryInitException
     */
    public NonProxiedTargetFactory(ConfiguredService targetService, ContextResolver resolver) throws FactoryInitException {
        assert (targetService != null) : "Target service was null";
        assert (resolver != null) : "Context resolver was null";

        this.resolver = resolver;
        targetName = new QualifiedName(targetService.getPart().getName());
        qualifiedServiceName = new QualifiedName("./"+targetName.getPortName());
    }

    public T getInstance() throws ObjectCreationException {
        return (T) resolver.getCurrentContext().getContext(targetName.getPartName()).getInstance(qualifiedServiceName); //locateInstance(targetName);  //locateInstance(targetName);
    }


}
