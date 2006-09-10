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
package org.apache.tuscany.core.component;

import org.apache.tuscany.spi.component.CompositeComponent;


/**
 * A specialization of a CompositeComponent that is able to automatically resolve references for its children using
 * service interfaces exposed by it or, recursively, any of it parents.
 *
 * @version $Rev$ $Date$
 */
public interface AutowireComponent extends CompositeComponent {

    /**
     * Invoked by child components to return an an autowire target. Resolved targets may be services or components in
     * the parent or its ancestors, or references in a sibling component
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException;

    /**
     * Invoked by a parent component to return an autowire target in a child. Resolved targets must be services. For
     * example, given a parent P and two siblings, A and B, A would request an autowire by invoking {@link
     * #resolveInstance(Class<T>)} on P, which in turn could invoke the present method on B in order to resolve a
     * target.
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException;


}
