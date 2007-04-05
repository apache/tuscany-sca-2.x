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
package org.apache.tuscany.core.resolver;

import java.net.URI;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.spi.resolver.ResolutionException;

/**
 * Implementations are responsible for resolving autowire targets in an SCA Domain. It is assumed that autowire
 * resolution occurs after resource resolution as service interface classes may be laoded
 *
 * @version $Rev$ $Date$
 */
public interface AutowireResolver {

    /**
     * Resolves autowires for a component definition and its decendents
     *
     * @param parentDefinition the parent
     * @param definition       the component definition to resolve autowires for
     * @throws ResolutionException
     */
    void resolve(Composite parentDefinition,
                 Component definition) throws ResolutionException;

    /**
     * Resolves autowires for a composite component type and its decendents
     *
     * @param compositeType the component type to resolve autowires for
     * @throws ResolutionException
     */
    @SuppressWarnings({"unchecked"})
    public void resolve(Composite compositeType) throws ResolutionException;

    /**
     * Adds the uri of a host system service that can be an autowire target
     *
     * @param contract the service contract of the system service
     * @param uri      the component uri
     */
    void addPrimordialService(ComponentService contract, URI uri);

}
