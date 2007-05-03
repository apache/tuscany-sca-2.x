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
package org.apache.tuscany.spi.deployer;

import java.util.Collection;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;

/**
 * Interface that can be used to deploy SCA bundles to a runtime.
 * 
 * @version $Rev$ $Date$
 */
public interface Deployer {
    /**
     * Deploy a component as a child of the supplied parent. This operation
     * creates a new component in the runtime to represent the supplied
     * component definition. The type of component created will depend on the
     * component definition implementation; for example, if the implementation
     * of the component definition is a composite then typically a
     * CompositeComponent would be returned.
     * 
     * @param componentDefinition the component definition as parsed from an
     *            assembly
     * @return the newly deployed component
     */
    Collection<Component> deploy(Composite composite) throws BuilderException, ComponentException;
}
