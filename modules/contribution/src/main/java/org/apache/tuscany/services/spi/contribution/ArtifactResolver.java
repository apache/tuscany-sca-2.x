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

package org.apache.tuscany.services.spi.contribution;



/**
 * SCA Assemblies reference many artifacts of a wide variety of types. These
 * include:
 * <ul>
 * <li> Reference from one SCA composite to another SCA composite
 * <li> Reference to PolicySet files
 * <li> Reference to interface definition files, either WSDL or Java interfaces
 * <li> Reference to XSD files
 * <li> Reference to any of a wide variety of implementation artifact files,
 * including Java classes, BPEL scripts, C++ DLLs and classes, PHP scripts
 * </ul>
 * In the SCA assemblies, these various artifacts are referenced using either
 * QNames or URIs that do not point to a specific entity. Resolution of these
 * references to concrete artifacts is necessary as part of the operation of the
 * SCA domain.
 * 
 * @version $Rev$ $Date$
 */
public interface ArtifactResolver {

    /**
     * Resolve an artifact.
     * @param modelClass the type of artifact
     * @param unresolved the unresolved artifact
     * @return the resolved artifact
     */
    <T> T resolve(Class<T> modelClass, T unresolved);
    
    /**
     * Add a resolved artifact.
     * @param resolved
     */
    void add(Object resolved);
    
    /**
     * Remove a resolved artifact.
     * @param resolved
     * @return the removed artifact, or null if the artifact was not removed
     */
    Object remove(Object resolved);
    
}
