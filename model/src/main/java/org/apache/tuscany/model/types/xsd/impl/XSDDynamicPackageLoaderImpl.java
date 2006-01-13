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
package org.apache.tuscany.model.types.xsd.impl;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.sdo.impl.DynamicEDataObjectImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * A dynamic package model loader for XSD models.
 */
public class XSDDynamicPackageLoaderImpl implements EPackage.Descriptor {
    private DynamicPackage dynamicPackage;
    private ResourceLoader bundleContext;
    private EPackage ePackage;

    /**
     * Constructor
     *
     * @param dynamicPackage
     */
    public XSDDynamicPackageLoaderImpl(DynamicPackage dynamicPackage, ResourceLoader bundleContext) {
        this.dynamicPackage = dynamicPackage;
        this.bundleContext = bundleContext;
    }

    /**
     * @see org.eclipse.emf.ecore.EPackage.Descriptor#getEPackage()
     */
    public EPackage getEPackage() {
        if (ePackage == null) {
            try {
                try {
                    // SECURITY
                    ePackage = (EPackage) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public Object run() throws IOException {
                            return demandLoad();
                        }
                    });
                } catch (PrivilegedActionException e1) {
                    throw e1.getException();
                }
                if (ePackage == null) {
                    throw new IllegalArgumentException("No package found in " + dynamicPackage.getLocation());
                }
            } catch (Exception e) {
                // fixme throw better exceptions
                throw new WrappedException(e);
            }
        }
        return ePackage;
    }

    /**
     * Load the dynamic package
     */
    private EPackage demandLoad() throws IOException {
        URI location = URI.createURI(dynamicPackage.getLocation());
        String nsURI = dynamicPackage.getUri();

        // Resolve a relative URI
        if (location.isRelative()) {
            URL url = bundleContext.getResource(location.path());
            if (url == null)
                throw new IllegalArgumentException("Location [" + location + "] cannot be resolved.");
            location = URI.createURI(url.toString());
        }

        // Load the XSD document into an XSD resource
        ConfiguredResourceSet resourceSet = (ConfiguredResourceSet) dynamicPackage.eResource().getResourceSet();
        Resource resource = resourceSet.getResource(location, true);

        // Load  XSDs and build the corresponding EMF packages
        Map packages = new HashMap();
        XSDEcoreBuilder builder = new XSD2SDOBuilder(resourceSet, nsURI);
        for (Iterator contents = resource.getContents().iterator(); contents.hasNext();) {
            Object content = contents.next();

            // Only process XSDSchemas
            if (!(content instanceof XSDSchema))
                continue;
            XSDSchema xsdSchema = (XSDSchema) content;
            builder.generate(xsdSchema);
        }

        // Add all packages
        Map ePackages = builder.getTargetNamespaceToEPackageMap();
        packages.putAll(ePackages);

        // Register the loaded packages and make sure that they use the SDO factory
        EPackage.Registry packageRegistry = resourceSet.getPackageRegistry();
        for (Iterator i = packages.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            EPackage ePackage = (EPackage) entry.getValue();

            EFactory eFactory = ePackage.getEFactoryInstance();
            if (eFactory == null || eFactory.getClass() == EFactoryImpl.class)
                ePackage.setEFactoryInstance(new DynamicEDataObjectImpl.FactoryImpl());

            packageRegistry.put(entry.getKey(), ePackage);
            if (ePackage.eResource() == null) {
                Resource ePackageResource = new XMIResourceImpl(URI.createURI(key));
                ePackageResource.getContents().add(ePackage);
                resourceSet.getResources().add(ePackageResource);
            }
        }

        // Return the package with the target URI
        return (EPackage) packages.get(nsURI);
    }

}