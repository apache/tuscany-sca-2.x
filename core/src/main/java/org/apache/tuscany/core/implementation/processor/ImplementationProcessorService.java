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
package org.apache.tuscany.core.implementation.processor;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.idl.java.IllegalCallbackException;

/**
 * Provides utility methods for Java implementation processing
 *
 * @version $Rev$ $Date$
 */
public interface ImplementationProcessorService {

    /**
     * Introspects the given interface to produce a mapped service
     */
    JavaMappedService createService(Class<?> interfaze)
        throws IllegalCallbackException, InvalidServiceContractException;

    /**
     * Processes the callback contract for a given interface type
     *
     * @param interfaze the interface type to examine
     * @param contract  the service contract the callback is associated wth
     * @throws org.apache.tuscany.core.idl.java.IllegalCallbackException
     */
    void processCallback(Class<?> interfaze, ServiceContract<?> contract)
        throws IllegalCallbackException;

    /**
     * Determines if all the members of a collection have unique types
     *
     * @param collection the collection to analyze
     * @return true if the types are unique
     */
    boolean areUnique(Class[] collection);

    /**
     * Inserts a name at the specified position, paddiling the list if its size is less than the position
     */
    void addName(List<String> names, int pos, String name);

    /**
     * Processes a constructor parameter by introspecting its annotations
     *
     * @param param            the parameter to process
     * @param paramAnnotations the parameter annotations
     * @param constructorNames the array of constructorNames specified by @Constructor
     * @param pos              the declaration position of the constructor parameter
     * @param type             the component type associated with implementation being reflected
     * @param injectionNames   the list of parameter constructorNames specified on parameter annotations
     * @throws org.apache.tuscany.spi.implementation.java.ProcessingException
     *
     */
    boolean processParam(Class<?> param,
                         Annotation[] paramAnnotations,
                         String[] constructorNames,
                         int pos,
                         PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                         List<String> injectionNames)
        throws ProcessingException;

    /**
     * Returns true if {@link @Autowire}, {@link @Property}, or {@link @Reference} are present in the given array
     */
    boolean injectionAnnotationsPresent(Annotation[][] annots);
}
