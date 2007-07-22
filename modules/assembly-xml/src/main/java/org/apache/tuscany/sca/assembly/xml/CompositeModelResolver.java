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

package org.apache.tuscany.sca.assembly.xml;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionImport;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;

/**
 * An Model Resolver for Composite artifact types.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class CompositeModelResolver extends DefaultModelResolver {

    public CompositeModelResolver(ClassLoader cl, Contribution contribution) {
        super(cl,contribution);
    }

    private Composite resolveImportedModel(Composite unresolved) {
        Composite resolved = unresolved;
        String namespace = unresolved.getName().getNamespaceURI();
        if (namespace != null && namespace.length() > 0) {
            for (ContributionImport contributionImport : this.contribution.getImports()) {
                if (contributionImport.getNamespace().equals(namespace)) {
                    
                    // Delegate the resolution to the import resolver
                    resolved = contributionImport.getModelResolver().resolveModel(Composite.class, unresolved);
                    
                    // If resolved... then we are done
                    if(unresolved.isUnresolved() == false) {
                        break;
                    }
                }
            }
        }
        return resolved;
    }
    
    @Override
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Composite resolved = (Composite) super.resolveModel(modelClass, unresolved);

        if (resolved.isUnresolved()) {
            resolved = resolveImportedModel(resolved);
        }
        
        return (T)resolved;
    }
    
    
}
