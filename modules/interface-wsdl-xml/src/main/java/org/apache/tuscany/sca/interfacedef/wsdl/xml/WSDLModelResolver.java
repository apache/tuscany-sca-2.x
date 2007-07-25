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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;

/**
 * An Model Resolver for WSDL artifact types.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class WSDLModelResolver extends DefaultModelResolver {

    public WSDLModelResolver(ClassLoader cl, Contribution contribution) {
        super(cl,contribution);
    }

    private WSDLDefinition resolveImportedModel(WSDLDefinition unresolved) {
        WSDLDefinition resolved = unresolved;
        String namespace = unresolved.getNamespace();
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                if (namespaceImport.getNamespace().equals(namespace)) {
                    
                    // Delegate the resolution to the import resolver
                    resolved = namespaceImport.getModelResolver().resolveModel(WSDLDefinition.class, unresolved);
                    
                    // If resolved... then we are done
                    if (resolved.isUnresolved() == false) {
                        break;
                    }
                }
            }
        }
        return resolved;
    }
    
    @Override
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        WSDLDefinition resolved = (WSDLDefinition) super.resolveModel(modelClass, unresolved);

        if (resolved.isUnresolved()) {
            resolved = resolveImportedModel(resolved);
        }
        
        return (T)resolved;
    }
    
}
