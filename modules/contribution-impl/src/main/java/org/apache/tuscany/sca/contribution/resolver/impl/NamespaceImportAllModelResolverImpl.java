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

import java.util.Collection;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.NamespaceExport;
import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A model resolver implementation that considers Exports in any available contribution
 *
 * @version $Rev: 548560 $ $Date: 2007-06-18 19:25:19 -0700 (Mon, 18 Jun 2007) $
 */
public class NamespaceImportAllModelResolverImpl implements ModelResolver {
    
    private NamespaceImport namespaceImport;
    private Collection<Contribution> contributions;
    
    public NamespaceImportAllModelResolverImpl(NamespaceImport namespaceImport, Collection<Contribution> contributions) {
        this.namespaceImport = namespaceImport;
        this.contributions = contributions;
    }

    public void addModel(Object resolved) {
        throw new IllegalStateException();
    }

    public Object removeModel(Object resolved) {
        throw new IllegalStateException();
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        
        //TODO optimize and cache results of the resolution later
        
        // Go over all available contributions
        Object resolved = null;
        for (Contribution contribution : contributions) {
            
            // Go over all exports in the contribution
            for (Export export : contribution.getExports()) {
                if (export instanceof NamespaceExport) {
                    NamespaceExport namespaceExport = (NamespaceExport)export;
                    if (namespaceImport.getNamespace().equals(namespaceExport.getNamespace())) {
                        Object r = namespaceExport.getModelResolver().resolveModel(modelClass, unresolved);
                        if (r != null) {
                            //FIXME we should test the unresolved flag instead
                            resolved = r;
                            break;
                        }
                    }
                }
            }
            if (resolved != null)
                break;
        }
        
        if (resolved != null) {
            return modelClass.cast(resolved);
        } else {
            return unresolved;
        }
    }

}
