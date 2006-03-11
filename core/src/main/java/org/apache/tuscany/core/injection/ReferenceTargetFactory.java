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

import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.model.assembly.ConfiguredService;

/**
 * Returns a direct reference to a target within the same aggregate, i.e. the factory avoids creating proxies and
 * returns the actual target instance
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceTargetFactory<T> implements ObjectFactory<T> {

    private AggregateContext parentContext;

    // the SCDL name of the target component/service for this reference
    private String targetName;

    /**
     * Constructs a reference object factory from a configured reference on a type
     * 
     * @throws FactoryInitException
     */
    public ReferenceTargetFactory(ConfiguredService targetService, AggregateContext parentContext) throws FactoryInitException {
        assert (targetService != null) : "Target service was null";
        assert (parentContext != null) : "Parent context was null";

        this.parentContext = parentContext;
        if (targetService.getAggregatePart() == null) {
            // FIXME not correct
            if (targetService.getService() == null) {
                throw new FactoryInitException("No target service specified");
            }
            targetName = targetService.getService().getName();
        } else {
            targetName = targetService.getAggregatePart().getName();
        }
    }

    public T getInstance() throws ObjectCreationException {
        return (T) parentContext.locateInstance(targetName);
    }

}
