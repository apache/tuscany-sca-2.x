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

package org.apache.tuscany.sca.contribution.osgi.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResourceReference;
import org.apache.tuscany.sca.osgi.runtime.OSGiRuntime;
import org.osgi.framework.Bundle;

/**
 * A Model Resolver for ResourceReferences from OSGi bundles.
 *
 */
public class OSGiResourceReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    private Map<String, ResourceReference> map = new HashMap<String, ResourceReference>();
    private Bundle bundle;
    private boolean initialized;

    public OSGiResourceReferenceModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        this.contribution = contribution;
    }

    public void addModel(Object resolved) {
        ResourceReference resourceRef = (ResourceReference)resolved;
        map.put(resourceRef.getResourceName(), resourceRef);
    }

    public Object removeModel(Object resolved) {
        return map.remove(((ResourceReference)resolved).getResourceName());
    }

    /**
     * Handle artifact resolution when the specific class reference is imported from another contribution
     * @param unresolved
     * @return
     */
    private ResourceReference resolveImportedModel(ResourceReference unresolved) {
    	ResourceReference resolved = unresolved;

        if (this.contribution != null) {
            for (Import import_ : this.contribution.getImports()) {

                if (resolved == unresolved && bundle != null) {
                    resolved = import_.getModelResolver().resolveModel(ResourceReference.class, unresolved);
                    if (resolved != unresolved)
                        break;
                }
            }

        }
        return resolved;
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);

        if (resolved != null) {
            return modelClass.cast(resolved);
        }
        initialize();

        // Get a resource
        String resourceName = ((ResourceReference)unresolved).getResourceName();
        URL resourceURL = null;
        if (bundle != null) {
            try {
            	resourceURL = bundle.getResource(resourceName);
            } catch (Exception e) {
                // we will later try to delegate to imported model resolvers
            }
        }

        if (resourceURL != null) {       
            // Store a new ResourceReference wrappering the resource
        	ResourceReference resourceReference = new ResourceReference(resourceName, resourceURL);
            map.put(resourceName, resourceReference);

            // Return the resolved ClassReference
            return modelClass.cast(resourceReference);
        } else {
            //delegate resolution of the class
            resolved = this.resolveImportedModel((ResourceReference)unresolved);
            return modelClass.cast(resolved);
        }

    }

    private void initialize() {
        if (initialized)
            return;

        initialized = true;
        try {
            bundle = OSGiRuntime.findInstalledBundle(contribution.getLocation());
        } catch (Exception e) {
        }
    }
}
