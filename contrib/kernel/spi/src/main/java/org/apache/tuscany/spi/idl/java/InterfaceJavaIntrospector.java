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
package org.apache.tuscany.spi.idl.java;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;

/**
 * Processor for creating JavaServiceContract definitions from Java Classes.
 *
 * @version $Rev$ $Date$
 */
public interface InterfaceJavaIntrospector {

    /**
     * Introspect a Java interface and return a service contract definition.
     *
     * @param type the interface to inspect
     * @return a JavaServiceContract corresponding to the Java interface
     */
    <I> JavaServiceContract<I> introspect(Class<I> type) throws InvalidServiceContractException;

    /**
     * Introspect a Java interface and return a service contract definition.
     *
     * @param type     the interface to inspect
     * @param callback the callback interface to inspec
     * @return a JavaServiceContract corresponding to the Java interface
     */
    <I, C> JavaServiceContract<I> introspect(Class<I> type, Class<C> callback) throws InvalidServiceContractException;
}
