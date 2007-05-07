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
package org.apache.tuscany.implementation.java.injection;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Resolves a runtime resource to be injected on a field or method of a Java component type marked with {@link
 * org.apache.tuscany.api.annotation.Resource}. If the mapped name of the resource is an absolute URI such as
 * <code>sca://localhost</code> or <code>jndi://localhost</code> the host container namespace is searched; otherwise the
 * URI is assumed to be relative and the parent composite is searched. If a mapped name is not provided, i.e. resolution
 * is by type, the parent composite is first searched followed by the host namespace.
 *
 * @version $Rev$ $Date$
 */
public class ResourceObjectFactory<T> implements ObjectFactory<T> {

    private Class<T> type;
    private String mappedName;
    private ResourceHost host;
    private boolean optional;

    /**
     * Instantiates a factory that resolves resources by type
     *
     * @param type     the type of the resource to inject
     * @param optional true if an error should be thrown if the resource is not found
     * @param host     the runtime resource provider
     */
    public ResourceObjectFactory(Class<T> type, boolean optional, ResourceHost host) {
        this(type, null, optional, host);
    }

    /**
     * Instantiates a factory that resolves resources by mapped name
     *
     * @param type       the type of the resource to inject
     * @param mappedName the resource name
     * @param optional   true if an error should be thrown if the resource is not found
     * @param host       the runtime resource provider
     */
    public ResourceObjectFactory(Class<T> type, String mappedName, boolean optional, ResourceHost host) {
        this.type = type;
        this.host = host;
        this.mappedName = mappedName;
        this.optional = optional;
    }

    @SuppressWarnings({"unchecked"})
    public T getInstance() throws ObjectCreationException {
        try {
            T resource;
            if (mappedName == null) {
                resource = host.resolveResource(type);
                if (!optional && resource == null) {
                    throw new ResourceNotFoundException("Resource not found", type.getName());
                }
            } else {
                resource = host.resolveResource(type, mappedName);
                if (!optional && resource == null) {
                    throw new ResourceNotFoundException("Resource not found", mappedName);
                }
            }
            return resource;
        } catch (ResourceResolutionException e) {
            throw new ObjectCreationException(e);
        }

    }
}
