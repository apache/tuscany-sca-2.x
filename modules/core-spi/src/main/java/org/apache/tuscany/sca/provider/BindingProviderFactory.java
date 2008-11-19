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

package org.apache.tuscany.sca.provider;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A factory for creating the runtime artifacts that represent bindings.
 * 
 * @version $Rev$ $Date$
 */
public interface BindingProviderFactory<M extends Binding> extends ProviderFactory<M> {

    /**
     * Creates a new reference binding provider for the given component and
     * reference.
     * 
     * @param component The runtime component
     * @param reference The reference on the runtime component
     * @param binding The binding assembly model object
     * @return The binding provider
     */
    ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                            RuntimeComponentReference reference,
                                                            M binding);

    /**
     * Creates a new service binding provider for the given component and
     * service.
     * 
     * @param component The runtime component
     * @param service The service on the runtime component
     * @param binding The binding assembly model object
     * @return The binding provider
     */
    ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                        RuntimeComponentService service,
                                                        M binding);

}
