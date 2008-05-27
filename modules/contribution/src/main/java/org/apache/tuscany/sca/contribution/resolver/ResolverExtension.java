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
 * Extension to assembly model implementations to provide a model resolver.
 *
 * @version $Rev$ $Date$
 */
public interface ResolverExtension {

    /**
     * Returns the model resolver for the models representing the artifacts
     * visible in the scope of this contribution.
     * 
     * @return The model resolver
     */
    ModelResolver getModelResolver();
    
    /**
     * Sets the model resolver for the models representing the artifacts
     * visible in the scope of this contribution.
     * 
     * @param modelResolver The model resolver
     */
    void setModelResolver(ModelResolver modelResolver);
    
}