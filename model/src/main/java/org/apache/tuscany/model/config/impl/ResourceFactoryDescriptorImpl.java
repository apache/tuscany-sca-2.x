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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;

import org.apache.tuscany.model.config.ResourceFactory;
import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * This is a Resource.Factory.Descriptor.
 *
 */
class ResourceFactoryDescriptorImpl implements Resource.Factory.Descriptor {
    private ResourceFactory resourceFactory;
    private ResourceLoader bundleContext;

    /**
     * Constructor
     *
     * @param resourceFactory
     */
    ResourceFactoryDescriptorImpl(ResourceFactory resourceFactory, ResourceLoader bundleContext) {
        this.resourceFactory = resourceFactory;
        this.bundleContext = bundleContext;
    }

    /**
     * Creates a Factory
     */
    public Factory createFactory() {
        try {
            final String className = resourceFactory.getClassName();
            Class resourceFactoryClass;
            try {
                // SECURITY
                resourceFactoryClass = (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws ClassNotFoundException {
                        return bundleContext.loadClass(className);
                    }
                });
            } catch (PrivilegedActionException e1) {
                throw e1.getException();
            }

            Object result = resourceFactoryClass.newInstance();
            return (Factory) result;

        } catch (Exception e) {
            // FIXME throw more meaningful exceptions
            throw new WrappedException(e);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return resourceFactory.toString();
    }
}