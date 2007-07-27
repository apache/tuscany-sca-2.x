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

package org.apache.tuscany.sca.contribution.resolver;


/**
 * An extension point for Model Resolvers
 * 
 * @version $Rev: 539693 $ $Date: 2007-05-18 23:24:07 -0700 (Fri, 18 May 2007) $
 */
public interface ModelResolverExtensionPoint {

    /**
     * Register a Model Resolver Class using the Artifact Type as the key
     * 
     * @param modelType The model artifact type
     * @param resolver The model resolver Class
     */
    void addResolver(Class<?> modelType, Class <? extends ModelResolver> resolver);
    
    /**
     * Remove the Model Resolver Class for a specific Artifact Type
     * 
     * @param modelType The model artifact type
     */
    void removeResolver(Class<?> modelType);
    
    /**
     * Retrieve a Model Resolver Class for a specific Artifact Type
     * 
     * @param modelType The model artifact type
     * @return The model resolver Class
     */
    Class <? extends ModelResolver> getResolver(Class<?> modelType);
}
