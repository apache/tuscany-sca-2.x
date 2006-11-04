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
package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.host.ResourceResolutionException;

/**
 * Resolves a runtime resource to be injected on a field or method of a Java component type marked with {@link
 * org.osoa.sca.annotations.Resource}. If the mapped name of the resource is an absolute URI such as
 * <code>sca://localhost</code> or <code>jndi://localhost</code> the host container namespace is searched; otherwise the
 * URI is assumed to be relative and the parent composite is searched. If a mapped name is not provided, i.e. resolution
 * is by type, the parent composite is first searched followed by the host namespace.
 *
 * @version $Rev$ $Date$
 */
public class ResourceObjectFactory<T> implements ObjectFactory<T> {

    private Class<T> type;
    private String mappedName;
    private CompositeComponent parent;
    private ResourceHost host;
    private boolean resolveFromHost;
    private boolean optional;

    /**
     * Instantiates a factory that resolves resources by type
     *
     * @param type     the type of the resource to inject
     * @param optional true if an error should be thrown if the resource is not found
     * @param parent   the parent composite of the component to inject on
     * @param host     the runtime resource provider
     */
    public ResourceObjectFactory(Class<T> type,
                                 boolean optional,
                                 CompositeComponent parent,
                                 ResourceHost host) {
        this.type = type;
        this.parent = parent;
        this.host = host;
        this.optional = optional;
    }

    /**
     * Instantiates a factory that resolves resources by mapped name
     *
     * @param type       the type of the resource to inject
     * @param mappedName the resource name
     * @param optional   true if an error should be thrown if the resource is not found
     * @param parent     the parent composite of the component to inject on
     * @param host       the runtime resource provider
     */
    public ResourceObjectFactory(Class<T> type,
                                 String mappedName,
                                 boolean optional,
                                 CompositeComponent parent,
                                 ResourceHost host) {
        this.type = type;
        this.parent = parent;
        this.host = host;
        if (mappedName.indexOf("://") >= 0) {
            this.resolveFromHost = true;
        }
        this.mappedName = mappedName;
        this.optional = optional;
    }

    public T getInstance() throws ObjectCreationException {
        if (resolveFromHost) {
            return resolveInstance();
        } else {
            T instance = null;
            if (mappedName == null) {
                instance = parent.resolveSystemInstance(type);
                if (instance == null) {
                    // if not found in parent scope, search the host namespace
                    resolveFromHost = true;
                    instance = resolveInstance();
                }
                if (instance == null && !optional) {
                    ResourceNotFoundException e = new ResourceNotFoundException("No resource found matching type");
                    e.setIdentifier(type.getName());
                    throw e;
                }
                return instance;
            } else {
                SCAObject child = parent.getSystemChild(mappedName);
                if (child != null) {
                    instance = type.cast(child.getServiceInstance());
                }
                if (instance == null && !optional) {
                    ResourceNotFoundException e = new ResourceNotFoundException("No resource found for URI");
                    e.setIdentifier(mappedName);
                    throw e;
                }
                return instance;
            }
        }
    }

    private T resolveInstance() {
        try {
            if (mappedName == null) {
                return host.resolveResource(type);
            } else {
                return host.resolveResource(type, mappedName);
            }
        } catch (ResourceResolutionException e) {
            throw new ObjectCreationException(e);
        }

    }
}
