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
package org.apache.tuscany.core.context;

import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.AssemblyContext;

import java.util.List;

/**
 * A context which contains child component contexts.
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeContext extends Context {

    public String getURI();

    public void setURI(String uri);

    /**
     * TODO remove this method
     * @deprecated
     */
    public void setAssemblyContext(AssemblyContext context);

    /**
     * Returns the parent context, or null if the context does not have one
     */
    public CompositeContext getParent();

    /**
     * Sets the parent context
     */
    public void setParent(CompositeContext parent);

    /**
     * Adds runtime artifacts represented by the set of model objects to the composite context by merging them with
     * existing artifacts. Implementing classes may support only a subset of {@link Part} types.
     * 
     * @see org.apache.tuscany.model.assembly.Component
     * @see org.apache.tuscany.model.assembly.ModuleComponent
     * @see org.apache.tuscany.model.assembly.AtomicComponent
     * @see org.apache.tuscany.model.assembly.EntryPoint
     * @see org.apache.tuscany.model.assembly.ExternalService
     */
    public void registerModelObjects(List<? extends Extensible> models) throws ConfigurationException;

    /**
     * Adds a runtime artifact represented by the model object to the composite context by merging it with existing
     * artifacts. Implementing classes may support only a subset of {@link Part} types.
     * 
     * @see org.apache.tuscany.model.assembly.Component
     * @see org.apache.tuscany.model.assembly.ModuleComponent
     * @see org.apache.tuscany.model.assembly.AtomicComponent
     * @see org.apache.tuscany.model.assembly.EntryPoint
     * @see org.apache.tuscany.model.assembly.ExternalService
     */
    public void registerModelObject(Extensible model) throws ConfigurationException;

    /**
     * Returns the child context associated with a given name
     */
    public Context getContext(String name);

    /**
     * Returns the composite managed by this composite context
     */
    @Deprecated
    public Composite getComposite();

    public void removeContext(String name);

}
