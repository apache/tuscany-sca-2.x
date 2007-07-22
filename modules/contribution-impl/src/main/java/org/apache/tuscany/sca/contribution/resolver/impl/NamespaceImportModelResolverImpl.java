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

package org.apache.tuscany.sca.contribution.resolver.impl;

import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A model resolver implementation for a namespace Import.
 *
 * @version $Rev$ $Date$
 */
public class NamespaceImportModelResolverImpl implements ModelResolver {
    
    private NamespaceImport namespaceImport;
    private ModelResolver targetModelResolver;
    
    public NamespaceImportModelResolverImpl(NamespaceImport namespaceImport, ModelResolver targetModelResolver) {
        this.namespaceImport = namespaceImport;
        this.targetModelResolver = targetModelResolver;
    }

    public void addModel(Object resolved) {
        throw new IllegalStateException();
    }

    public Object removeModel(Object resolved) {
        throw new IllegalStateException();
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        
        // Delegate to the given target model resolver. This is either the model resolver
        // of a matching export, or a model resolver that considers matching exports
        // from all available contributions
        return targetModelResolver.resolveModel(modelClass, unresolved);
    }

}
