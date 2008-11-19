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

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tuscany.sca.contribution.osgi.BundleReference;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.osgi.framework.Bundle;

/**
 * An implementation of an artifact resolver for OSGi bundles.
 *
 * @version $Rev$ $Date$
 */
public class OSGiModelResolverImpl implements ModelResolver {
    private static final long serialVersionUID = -7826976465762296634L;
    
    private Map<Object, Object> map = new HashMap<Object, Object>();
    
    private Hashtable<String, Bundle>  bundles;
    public OSGiModelResolverImpl(Hashtable<String, Bundle> bundles) {
        this.bundles = bundles;
    }
    
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        if (resolved != null) {
            
            // Return the resolved object
            return modelClass.cast(resolved);
            
        } else if (unresolved instanceof ClassReference) {
            
            // Load a class on demand
            ClassReference classReference = (ClassReference)unresolved;
            Class clazz = null;
            for (Bundle bundle : bundles.values()) {
                try {
                    clazz = bundle.loadClass(classReference.getClassName());
                } catch (ClassNotFoundException e) {
                    continue;
                }
                break;
            }
            if (clazz == null) {

                // Return the unresolved object
                return unresolved;
            }
            
            // Store a new ClassReference wrapping the loaded class
            resolved = new ClassReference(clazz);
            map.put(resolved, resolved);
            
            // Return the resolved ClassReference
            return modelClass.cast(resolved);
                
        } else if (unresolved instanceof BundleReference) {
            for (String bundlePath: bundles.keySet()) {
                Bundle bundle = bundles.get(bundlePath);
                BundleReference bundleRef = (BundleReference)unresolved;
                String bundleVersion = (String)bundle.getHeaders().get("Bundle-Version");
                if (bundle.getSymbolicName().equals(bundleRef.getBundleName())&&
                    (bundleVersion == null || bundleRef.getBundleVersion() == null ||
                    bundleVersion.equals(bundleRef.getBundleVersion()))) {
                    
                    resolved = new BundleReference(bundle, 
                            bundle.getSymbolicName(), 
                            bundleVersion,
                            bundlePath);
                    map.put(resolved, resolved);
                    
                    // Return the resolved BundleReference
                    return modelClass.cast(resolved);
                    
                }
            }
        } 
            
        // Return the unresolved object
        return unresolved;
    }
    
    public void addModel(Object resolved) {
        map.put(resolved, resolved);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(resolved);
    }
    
    public Collection<Object> getModels() {
        return map.values();
    }
    
}
