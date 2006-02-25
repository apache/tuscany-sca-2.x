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
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;

/**
 * Returns a direct reference to a target service, i.e. the factory avoids creating proxies and returns the actual
 * target instance
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceTargetFactory<T> implements ObjectFactory<T> {

    private AggregateContext parentContext;

    // the SCDL name of the target component/service for this reference
    private String targetName;

    private QualifiedName targetComponentName;

    // the reference target is in another module
    private boolean interModule;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    /**
     * Constructs a reference object factory from a configured reference on a type
     */
    public ReferenceTargetFactory(ConfiguredReference reference, AggregateContext parentContext)
            throws FactoryInitException {
        // FIXME how to handle a reference that is a list - may take different proxy factories for each entry
        assert (reference != null) : "Reference was null";
        assert (parentContext != null) : "Parent context was null";

        this.parentContext = parentContext;
        // targetName = reference.getReference().getName();
        ConfiguredService targetService = reference.getTargetConfiguredServices().get(0);
        if (targetService.getAggregatePart() instanceof ExternalService) {
            targetName = ((ExternalService) targetService.getAggregatePart()).getName();
        } else if (targetService.getAggregatePart() instanceof Component) {
            Component targetComponent = (Component) targetService.getAggregatePart();
            targetName = targetComponent.getName();
        } else if (targetService.getAggregatePart() instanceof EntryPoint) {
            targetName = ((EntryPoint) targetService.getAggregatePart()).getName();
        } else if (targetService.getAggregatePart() == null) {
            // FIXME not correct
            if (targetService.getService() == null) {
                throw new FactoryInitException("No target service specified");
            }
            targetName = targetService.getService().getName();
        } else {
            FactoryInitException fie = new FactoryInitException("Unknown reference target type");
            fie.setIdentifier(reference.getReference().getName());
            throw fie;
        }
    }

    /**
     * Reference source is an external service, target is in another module
     * 
     * @param service
     * @param parentContext
     * @throws FactoryInitException
     */
    public ReferenceTargetFactory(String targetName, AggregateContext parentContext) throws FactoryInitException {
        //assert (service != null) : "Service was null";
        assert (parentContext != null) : "Parent context was null";
        interModule = true; // an external service with a reference target in another module
        this.targetName = targetName;// service.getAggregatePart().getName();
        targetComponentName = new QualifiedName(targetName);
        this.parentContext = parentContext;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public T getInstance() throws ObjectCreationException {
        if (interModule) {
            // only return entry points since this is an inter-module wire
            Object o = parentContext.getInstance(targetComponentName);
            if (o != null) {
                return (T) o;
            } else {
                // walk up the hierarchy of aggregate contexts
                AggregateContext ctx = parentContext;
                do {
                    if (ctx == null) {
                        break; // reached top of context hierarchy
                    }
                    InstanceContext compContext = ctx.getContext(targetComponentName.getPartName());
                    if (compContext != null) {
                        o = compContext.getInstance(targetComponentName);
                        if (o != null) {
                            return (T) o;
                        }
                    }
                    ctx = ctx.getParent();
                } while (ctx != null);
                TargetException e= new TargetException("Target reference not found");
                e.setIdentifier(targetName);
                throw e;
            }
        } else {
            // the target is in the same module, so just locate it
            return (T) parentContext.locateInstance(targetName);
        }
    }

}
