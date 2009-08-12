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
import org.apache.tuscany.sca.contribution.osgi.BundleReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.osgi.framework.Bundle;

/**
 * A Model Resolver for BundleReferences.
 *
 * @version $Rev$ $Date$
 */
public class OSGiBundleReferenceModelResolver implements ModelResolver {
    private Contribution contribution;
    private Map<BundleReference, BundleReference> refs = new HashMap<BundleReference, BundleReference>();

    private OSGiBundleProcessor bundleProcessor;

    public OSGiBundleReferenceModelResolver(Contribution contribution, FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.contribution = contribution;
        this.bundleProcessor = new OSGiBundleProcessor();
    }

    public void addModel(Object resolved) {
        BundleReference bundleRef = (BundleReference)resolved;
        refs.put(bundleRef, bundleRef);
    }

    public Object removeModel(Object resolved) {
        return refs.remove(resolved);
    }

    /**
     * Handle artifact resolution when the specific class reference is imported from another contribution
     * @param unresolved
     * @return
     */
    private BundleReference resolveImportedModel(BundleReference unresolved) {
        BundleReference resolved = unresolved;

        if (this.contribution != null) {
            for (Import import_ : this.contribution.getImports()) {

                resolved = import_.getModelResolver().resolveModel(BundleReference.class, unresolved);
                if (resolved != unresolved)
                    break;
            }

        }
        return resolved;
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = refs.get(unresolved);

        if (resolved != null) {
            return modelClass.cast(resolved);
        }

        if (OSGiBundleActivator.getBundleContext() == null)
            return unresolved;

        //Load a class on demand
        Bundle bundle = null;
        String bundleName = ((BundleReference)unresolved).getSymbolicName();
        String bundleVersion = ((BundleReference)unresolved).getVersion();

        bundle = OSGiBundleActivator.findBundle(bundleName, bundleVersion);
        BundleReference bundleReference;

        if (bundle == null) {
            bundleReference = bundleProcessor.installNestedBundle(contribution, bundleName, bundleVersion);
        } else {
            bundleReference = new BundleReference(bundle);
        }

        if (bundleReference != null) {
            //if we load the class            

            refs.put(((BundleReference)unresolved), bundleReference);

            // Return the resolved BundleReference
            return modelClass.cast(bundleReference);
        } else {
            //delegate resolution of the class
            resolved = this.resolveImportedModel((BundleReference)unresolved);
            return modelClass.cast(resolved);
        }

    }
}
