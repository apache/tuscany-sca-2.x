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

import java.net.URL;
import java.util.Map;

import org.apache.tuscany.services.contribution.model.Contribution;


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
     * Resolve an artifact by the qualified name
     * 
     * @param contribution the model of the contribution
     * @param modelClass The java type of the artifact 
     * @param namespace The namespace of the artifact
     * @param name The name of the artifact
     * @param attributes Additional attributes that can be used to constrain the
     *            resolution
     * @param context The deployment context
     * @return The resolved artifact
     */
    <T> T resolve(Contribution contribution,
                  Class<T> modelClass,
                  String namespace,
                  String name,
                  Map attributes/*,
                  DeploymentContext context*/);
    
    /**
     * Resolve an artifact by the URI. Some typical use cases are:
     * <ul>
     * <li>Reference a XML schema using
     * {http://www.w3.org/2001/XMLSchema-instance}schemaLocation or
     * <li>Reference a list of WSDLs using
     * {http://www.w3.org/2004/08/wsdl-instance}wsdlLocation
     * </ul>
     * @param targetNamespace The target namespace of the referenced artifact,
     *            if the targetNamespace is null, then it's not specified
     * @param location The URI of the referenced artifact, it can be absolute or
     *            relative
     * @param baseURI The URI of the owning artifact
     * 
     * @return The URI of the resolved artifact
     */
    URL resolve(Contribution contribution, String targetNamespace, String location, String baseURI);

}
