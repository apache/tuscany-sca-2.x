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

import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.loader.LoaderException;

/**
 * Interface that can be used to deploy SCA bundles to a runtime.
 *
 * @version $Rev$ $Date$
 */
public interface Deployer {
    /**
     * Deploy a component as a child of the supplied parent.
     *
     * @param parent the parent context
     * @param component the component definition
     */
    <I extends Implementation<?>> void deploy(CompositeContext<?> parent, Component<I> component) throws LoaderException;
}
