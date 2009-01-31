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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.osgi.BundleReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.osgi.runtime.OSGiRuntime;
import org.osgi.framework.Bundle;

/**
 * A Model Resolver for BundleReferences.
 *
 * @version $Rev$ $Date$
 */
public class OSGiBundleReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    private Map<String, BundleReference> map = new HashMap<String, BundleReference>();
    
    OSGiRuntime osgiRuntime;
    private OSGiBundleProcessor bundleProcessor;
    
    public OSGiBundleReferenceModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        this.contribution = contribution;
        this.bundleProcessor = new OSGiBundleProcessor();
    }

    public void addModel(Object resolved) {
        BundleReference bundleRef = (BundleReference)resolved;
        map.put(bundleRef.getBundleUniqueName(), bundleRef);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((BundleReference)resolved).getBundleUniqueName());
    }
    
    /**
     * Handle artifact resolution when the specific class reference is imported from another contribution
     * @param unresolved
     * @return
     */
    private BundleReference resolveImportedModel(BundleReference unresolved) {
        BundleReference resolved = unresolved;

        if( this.contribution != null) {
            for (Import import_ : this.contribution.getImports()) {
                
                resolved = import_.getModelResolver().resolveModel(BundleReference.class, unresolved);
                if (resolved != unresolved)
                        break;
            }
            
        }
        return resolved;
    }
    
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        
        if (resolved != null ){
            return modelClass.cast(resolved);
        } 
        
        try {
            if (osgiRuntime == null)
                osgiRuntime = OSGiRuntime.getRuntime();
        } catch (Exception e) {
        }
        if (osgiRuntime == null)
            return unresolved;

        //Load a class on demand
        Object bundle = null;
        String bundleName = ((BundleReference)unresolved).getBundleName();
        String bundleVersion = ((BundleReference)unresolved).getBundleVersion();
        
        bundle = osgiRuntime.findBundle(bundleName, bundleVersion);
        BundleReference bundleReference;
        
        if (bundle == null)
            bundleReference = bundleProcessor.installNestedBundle(contribution, bundleName, bundleVersion);
        else {
            bundleReference = new BundleReference(bundle, 
                    ((BundleReference)unresolved).getBundleName(),
                    bundleVersion,
                    getBundleFileName(bundle)
                    );
        }
                
        
        if (bundleReference != null) {
            //if we load the class            
            
            map.put(((BundleReference)unresolved).getBundleUniqueName(), bundleReference);
            
            // Return the resolved BundleReference
            return modelClass.cast(bundleReference);            
        } else {
            //delegate resolution of the class
            resolved = this.resolveImportedModel((BundleReference)unresolved);
            return modelClass.cast(resolved);
        }
        

    }
    
    
    private String getBundleFileName(Object bundle) {
        if (bundle instanceof Bundle) {
            String path = ((Bundle)bundle).getLocation();
            if (path.endsWith("/"))
                path = path.substring(0, path.length()-1);
            if (path.startsWith(contribution.getLocation())) {
                if (path.equals(contribution.getLocation())) {
                    int index = path.lastIndexOf('/');
                    if (index > 0 && index < path.length()-1)
                        path = path.substring(index+1);
                } else {
                    path = path.substring(contribution.getLocation().length());
                    if (path.startsWith("/"))
                        path = path.substring(1);
                }
            } else if (path.lastIndexOf('/') >= 0)
                path = path.substring(path.lastIndexOf('/')+1);
            return path;
        }
        return null;
            
    }
    
}
