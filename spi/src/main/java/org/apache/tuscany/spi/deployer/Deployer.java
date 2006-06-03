/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.deployer;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;

/**
 * Interface that can be used to deploy SCA bundles to a runtime.
 *
 * @version $Rev$ $Date$
 */
public interface Deployer {
    /**
     * Deploy a component as a child of the supplied parent. This operation creates a new component in the
     * runtime to represent the supplied component definition. The type of component created will depend on
     * the component definition implementation; for example, if the implementation of the component definition
     * is a composite then typically a CompositeComponent would be returned.
     *
     * @param parent              the parent context
     * @param componentDefinition the component definition as parsed from an assembly
     * @return the newly deployed component
     */
    <I extends Implementation<?>> SCAObject<?> deploy(CompositeComponent<?> parent, ComponentDefinition<I> componentDefinition) throws LoaderException;
}
