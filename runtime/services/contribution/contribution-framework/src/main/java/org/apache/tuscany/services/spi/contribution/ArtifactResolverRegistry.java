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
 * Registry for artifact resolvers
 * 
 * @version $Rev$ $Date$
 */
public interface ArtifactResolverRegistry extends ArtifactResolver {
    /**
     * Register a resolver by the type of artifacts. For example, you can 
     * register a resolver to resolve WSDL model objects and other resolver
     * for java classes
     * 
     * @param modelClass The java type of the model object
     * @param resolver The resolver 
     */
    void registerResolver(Class<?> modelClass, ArtifactResolver resolver);

    /**
     * Unregister all resolvers for the given model class
     * 
     * @param modelClass
     */
    void unregisterResolver(Class<?> modelClass);
}
