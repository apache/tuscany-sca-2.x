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

import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionImport;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;

/**
 * An Model Resolver for ConstrainigType artifact types.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class ConstrainingTypeModelResolver extends DefaultModelResolver {

    public ConstrainingTypeModelResolver(ClassLoader cl, Contribution contribution) {
        super(cl,contribution);
    }

    private ConstrainingType resolveImportedModel(ConstrainingType unresolved) {
        ConstrainingType resolved = unresolved;
        String namespace = unresolved.getName().getNamespaceURI();
        if (namespace != null && namespace.length() > 0) {
            for (ContributionImport contributionImport : this.contribution.getImports()) {
                if (contributionImport.getNamespace().equals(namespace)) {
                    
                    // Delegate the resolution to the import resolver
                    resolved = contributionImport.getModelResolver().resolveModel(ConstrainingType.class, unresolved);
                    
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
        ConstrainingType resolved = (ConstrainingType) super.resolveModel(modelClass, unresolved);

        if (resolved.isUnresolved()) {
            resolved = resolveImportedModel(resolved);
        }
        
        return (T)resolved;
    }
    
    
}
