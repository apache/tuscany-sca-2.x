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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;

import org.apache.tuscany.model.config.GeneratedPackage;
import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * An EPackage.Descriptor for generated EPackages.
 *
 */
class GeneratedPackageDescriptorImpl implements EPackage.Descriptor {
    private final GeneratedPackage generatedPackage;
    private final ResourceLoader bundleContext;
    private EPackage ePackage;

    /**
     * Constructor.
     *
     * @param generatedPackage
     * @param bundleContext
     */
    public GeneratedPackageDescriptorImpl(GeneratedPackage generatedPackage, ResourceLoader bundleContext) {
        this.generatedPackage = generatedPackage;
        this.bundleContext = bundleContext;
    }

    /**
     * @see org.eclipse.emf.ecore.EPackage.Descriptor#getEPackage()
     */
    public EPackage getEPackage() {
        if (ePackage == null) {
            try {
                // Load the EPackage class
                final String className = generatedPackage.getPackageClassName();
                Class ePackageClass;
                try {
                    // SECURITY
                    ePackageClass = (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public Object run() throws ClassNotFoundException {
                            return bundleContext.loadClass(className);
                        }
                    });
                } catch (PrivilegedActionException e1) {
                    throw e1.getException();
                }

                // Get the EPackage instance
                Field eInstance = ePackageClass.getDeclaredField("eINSTANCE");
                ePackage = (EPackage) eInstance.get(null);

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
        if (obj instanceof GeneratedPackageDescriptorImpl) {
            GeneratedPackageDescriptorImpl target = (GeneratedPackageDescriptorImpl) obj;

            String className = generatedPackage.getPackageClassName();
            String targetClassName = target.generatedPackage.getPackageClassName();
            if (className.equals(targetClassName))
                return true;
        }
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return generatedPackage.toString();
    }

}