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

import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionExport;
import org.apache.tuscany.sca.contribution.ContributionImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A model resolver implementation, that consider Contribution Imports in any contribution
 *
 * @version $Rev: 548560 $ $Date: 2007-06-18 19:25:19 -0700 (Mon, 18 Jun 2007) $
 */
public class ContributionImportAnyModelResolverImpl implements ModelResolver {
    
    private ContributionImport contributionImport;
    private Map<String, Contribution> contributionRegistry;
    
    public ContributionImportAnyModelResolverImpl(ContributionImport contributionImport, Map<String, Contribution> contributionRegistry) {
        this.contributionImport = contributionImport;
        this.contributionRegistry = contributionRegistry;
    }

    public void addModel(Object resolved) {
        throw new IllegalStateException();
    }

    public Object removeModel(Object resolved) {
        throw new IllegalStateException();
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        // This needs to delegate to the matching ContributionExportModelResolver
        // from the contribution matching the import's location URI, or a ModelResolver
        // that goes over all exports with a matching namespace if there is no URI

        Object resolved = null;
        for (Contribution contribution : contributionRegistry.values()) {
            for (ContributionExport contributionExport : contribution.getExports()) {
                if (contributionImport.getNamespace().equals(contributionExport.getNamespace())) {
                    resolved = contributionExport.getModelResolver().resolveModel(modelClass, unresolved);
                }
            }
        }
        
        if (resolved != null) {
            return modelClass.cast(resolved);
        } else {
            return unresolved;
        }
    }

}
