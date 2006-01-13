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
package org.apache.tuscany.model.config.impl;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;

import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.config.DynamicPackageLoader;
import org.apache.tuscany.model.config.ModelConfiguration;
import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * An EPackage.Descriptor that delegates to a loader.
 */
class DynamicPackageDescriptorImpl implements EPackage.Descriptor {
    private final DynamicPackage dynamicPackage;
    private final ResourceLoader bundleContext;
    private EPackage ePackage;

    /**
     * Constructor
     *
     * @param dynamicPackage
     */
    public DynamicPackageDescriptorImpl(DynamicPackage dynamicPackage, ResourceLoader bundleContext) {
        this.dynamicPackage = dynamicPackage;
        this.bundleContext = bundleContext;
    }

    /**
     * Returns the target EPackage
     */
    public EPackage getEPackage() {
        if (ePackage == null) {

            try {
                // First check the loader on the dynamicPackage entry
                String loaderClassName = dynamicPackage.getLoaderClassName();
                if (loaderClassName == null) {

                    // Try to match standalone loaders by protocol or extension
                    URI uri = URI.createURI(dynamicPackage.getLocation());
                    ModelConfiguration modelConfig = (ModelConfiguration) dynamicPackage.eContainer();
                    DynamicPackageLoader loader = modelConfig.getDynamicPackageLoader(uri);
                    if (loader != null)
                        loaderClassName = loader.getClassName();
                    else
                        throw new IllegalArgumentException("Cannot find Loader for " + dynamicPackage);
                }

                // Load the EPackage loader class
                final String descriptorClassName = loaderClassName;
                Class descriptorClass;
                try {
                    // SECURITY
                    descriptorClass = (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public Object run() throws ClassNotFoundException {
                            return bundleContext.loadClass(descriptorClassName);
                        }
                    });
                } catch (PrivilegedActionException e1) {
                    throw e1.getException();
                }

                final Class[] paramTypes = {DynamicPackage.class, ResourceLoader.class};
                Constructor constructor = descriptorClass.getConstructor(paramTypes);
                final Object[] args = {dynamicPackage, bundleContext};

                // Instantiate the EPackage.Descriptor and get the EPackage
                EPackage.Descriptor descriptor = (EPackage.Descriptor) constructor.newInstance(args);
                ePackage = descriptor.getEPackage();

            } catch (Exception e) {
                // FIXME throw more meaningful exceptions
                throw new WrappedException(e);
            }
        }
        return ePackage;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof DynamicPackageDescriptorImpl) {
            DynamicPackageDescriptorImpl target = (DynamicPackageDescriptorImpl) obj;

            String loaderClassName = dynamicPackage.getLoaderClassName();
            if (loaderClassName == null)
                loaderClassName = "";
            String location = dynamicPackage.getLocation();

            String targetLoaderClassName = target.dynamicPackage.getLoaderClassName();
            if (targetLoaderClassName == null)
                targetLoaderClassName = "";
            String targetLocation = target.dynamicPackage.getLocation();

            if (loaderClassName.equals(targetLoaderClassName) && location.equals(targetLocation))
                return true;
        }
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return dynamicPackage.toString();
    }

}

