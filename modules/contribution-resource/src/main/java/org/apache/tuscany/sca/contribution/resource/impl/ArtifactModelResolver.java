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

package org.apache.tuscany.sca.contribution.resource.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resource.ResourceImport;

/**
 * A Model Resolver for contribution artifacts.
 *
 * @version $Rev$ $Date$
 */
public class ArtifactModelResolver implements ModelResolver {
    private Contribution contribution;
    private Map<String, Artifact> map = new HashMap<String, Artifact>();
    
    public ArtifactModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
    	this.contribution = contribution;
    }

    public void addModel(Object resolved) {
    	Artifact artifact = (Artifact)resolved;
        map.put(artifact.getURI(), artifact);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((Artifact)resolved).getURI());
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {

    	// Get the artifact URI
        String uri = ((Artifact)unresolved).getURI();
        if (uri == null) {
        	return (T)unresolved;
        }
        
        // Lookup the artifact
        Artifact resolved = (Artifact) map.get(uri);
        if (resolved != null) {
            return modelClass.cast(resolved);
        } 
        
        // If not found, delegate the resolution to the imports (in this case based on the resource imports)
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof ResourceImport) {
            	ResourceImport resourceImport = (ResourceImport)import_;
            	//check the import location against the computed package name from the componentType URI
                if ((resourceImport.getURI().equals(uri)) &&
                    (resourceImport.getModelResolver() != null)){
                    // Delegate the resolution to the import resolver
                    resolved = resourceImport.getModelResolver().resolveModel(Artifact.class, (Artifact)unresolved);
                    if (!resolved.isUnresolved()) {
                        return modelClass.cast(resolved);
                    }
                }
            }
        }

        return (T)unresolved;
    }
    
}
