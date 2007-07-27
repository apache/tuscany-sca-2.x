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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;

/**
 * A Model Resolver for WSDL models.
 *
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class WSDLModelResolver implements ModelResolver {
    private Contribution contribution;
    private Map<String, WSDLDefinition> map = new HashMap<String, WSDLDefinition>();
    
    public WSDLModelResolver(Contribution contribution) {
        this.contribution = contribution;
    }

    public void addModel(Object resolved) {
        WSDLDefinition definition = (WSDLDefinition)resolved;
        map.put(definition.getNamespace(), definition);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((WSDLDefinition)resolved).getNamespace());
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        
        // Lookup a definition for the given namespace
        String namespace = ((WSDLDefinition)unresolved).getNamespace();
        WSDLDefinition resolved = (WSDLDefinition) map.get(namespace);
        if (resolved != null) {
            return (T)resolved;
        }
        
        // No definition found, delegate the resolution to the imports
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                if (namespaceImport.getNamespace().equals(namespace)) {
                    
                    // Delegate the resolution to the import resolver
                    resolved = namespaceImport.getModelResolver().resolveModel(WSDLDefinition.class, (WSDLDefinition)unresolved);
                    if (!resolved.isUnresolved()) {
                        return (T)resolved;
                    }
                }
            }
        }
        return (T)unresolved;
    }
    
}
