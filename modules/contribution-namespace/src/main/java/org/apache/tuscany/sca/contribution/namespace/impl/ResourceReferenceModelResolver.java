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

package org.apache.tuscany.sca.contribution.namespace.impl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ResourceReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A Model Resolver for ResourceReferences.
 *
 */
public class ResourceReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    protected WeakReference<ClassLoader> classLoader;
    private Map<String, ResourceReference> map = new HashMap<String, ResourceReference>();

    private ModelResolver osgiResolver;

    public ResourceReferenceModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        this.contribution = contribution;
        this.classLoader = new WeakReference<ClassLoader>(this.contribution.getClassLoader());        

        try {
            Class osgiResolverClass =
                Class.forName("org.apache.tuscany.sca.contribution.osgi.impl.OSGiResourceReferenceModelResolver");
            if (osgiResolverClass != null) {
                Constructor constructor =
                    osgiResolverClass.getConstructor(Contribution.class, ModelFactoryExtensionPoint.class);
                this.osgiResolver = (ModelResolver)constructor.newInstance(contribution, modelFactories);
            }
        } catch (Exception e) {
        }
    }

    public void addModel(Object resolved) {
        ResourceReference resourceRef = (ResourceReference)resolved;
        map.put(resourceRef.getResourceName(), resourceRef);
    }

    public Object removeModel(Object resolved) {
        return map.remove(((ResourceReference)resolved).getResourceName());
    }

  

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);

        if (resolved != null) {
            return modelClass.cast(resolved);
        }

        //Get a resource
        String resourceName = ((ResourceReference)unresolved).getResourceName();
        URL resourceURL = null;
        
        if (URI.create(resourceName).isAbsolute()) {
        	try {
        		File resourceFile = new File(resourceName);
        		if (resourceFile.exists())
				    resourceURL = resourceFile.toURL();
			} catch (MalformedURLException e) {
			}
        }
        
        if (osgiResolver != null) {
            resolved = osgiResolver.resolveModel(modelClass, unresolved);
            resourceURL = ((ResourceReference)resolved).getResource();
        }
        
        if (resourceURL == null) {
        	resourceURL = classLoader.get().getResource(resourceName);
        }

        if (resourceURL != null) {          
            // Store a new ResourceReference wrappering the resource
            ResourceReference resourceReference = new ResourceReference(resourceName, resourceURL);
            map.put(resourceName, resourceReference);

            // Return the resolved ResourceReference
            return modelClass.cast(resourceReference);
        } else {
            return unresolved;
        }

    }

   
}
