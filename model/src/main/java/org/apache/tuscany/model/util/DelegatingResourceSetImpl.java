/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;


/**
 *         <p/>
 *         An EMF resource set which delegates to a parent resource set.
 */

public class DelegatingResourceSetImpl extends ExtendedResourceSetImpl {

    private ResourceSet parentResourceSet;

    /**
     * Constructor.
     */
    public DelegatingResourceSetImpl(ExtendedResourceSet parent) {
        super(parent.getResourceLoader());
        parentResourceSet = parent;

        // Use the resource factory registry and package registry from the parent
        setResourceFactoryRegistry(parent.getResourceFactoryRegistry());
        setPackageRegistry(parent.getPackageRegistry());
        setURIConverter(parentResourceSet.getURIConverter());
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#delegatedGetResource(org.eclipse.emf.common.util.URI, boolean)
     */
    protected Resource delegatedGetResource(URI uri, boolean loadOnDemand) {

        // Delegate to the package registry
        Resource resource = super.delegatedGetResource(uri, loadOnDemand);
        if (resource != null)
            return resource;

        // Delegate to the parent resource set
        return parentResourceSet.getResource(uri, loadOnDemand);
    }

}