/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.ExtensibleModelObject;

/**
 * Offers configuration services in the runtime.
 * A ConfigurationContext is able to configure a model and then build the runtime representation corresponding
 * to that model in the live system.
 * <p/>
 * Configuration contexts will typically be hierarchical, delegating to their parent <b>after</b> performing an operation.
 * The parent ConfigurationContext will typically be injected into an implementation of this interface during registration.
 *
 * @version $Rev$ $Date$
 */
public interface ConfigurationContext {

    /**
     * Adds additional configuration information to a model object.
     *
     * @param model the model object to be configured
     * @throws ConfigurationException
     */
    public void configure(ExtensibleModelObject model) throws ConfigurationException;

    /**
     * Decorates the supplied model object with a {@link org.apache.tuscany.core.builder.RuntimeConfiguration}
     * that can be used to create the runtime context corresponding to the model.
     *
     * @param parent an AggregrateContext that will ultimately become the parent of the runtime context
     * @param model  the model object that defines the configuration to be built
     * @throws BuilderConfigException
     * @see org.apache.tuscany.core.builder.RuntimeConfiguration
     */
    public void build(AggregateContext parent, ExtensibleModelObject model) throws BuilderConfigException;

}
