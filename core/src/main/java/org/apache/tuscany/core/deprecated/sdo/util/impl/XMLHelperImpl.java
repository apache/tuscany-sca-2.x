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
package org.apache.tuscany.core.deprecated.sdo.util.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import commonj.sdo.DataObject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.sdo.EDataObject;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.model.util.DelegatingResourceSetImpl;
import org.apache.tuscany.model.util.XMLResourceFactoryImpl;
import org.apache.tuscany.core.deprecated.sdo.util.XMLHelper;

/**
 * Provides helper methods to load/save DataObjects and create serializable DataObject holders.
 *
 */
public class XMLHelperImpl implements XMLHelper {
    // [rfeng] The URI has to be an absolute URI so that references in the same resource can be resolved during loading
    private static final URI DATAOBJECT_URI = URI.createURI("tuscany:/dataObject.xml");

    private ConfiguredResourceSet configuredResourceSet;

    /**
     * Used to save the containment state of a DataObject, to be able to restore it after we've changed it
     */
    private class DataObjectContainmentState {
        private EDataObject dataObject;
        private EObject originalContainer;
        private boolean originalContainerDeliver;
        private EStructuralFeature originalContainingFeature;
        private boolean originalContainingFeatureDeliver;
        private EReference originalContainmentFeature;
        private boolean originalContainmentFeatureDeliver;
        private Resource originalResource;
        private boolean originalResourceDeliver;

        /**
         * Constructor
         *
         * @param dataObject
         */
        private DataObjectContainmentState(EDataObject dataObject) {
            this.dataObject = dataObject;
            originalContainer = dataObject.eContainer();
            if (originalContainer != null) {
                originalContainerDeliver = originalContainer.eDeliver();
                originalContainer.eSetDeliver(false);
                originalContainingFeature = dataObject.eContainingFeature();
                if (originalContainingFeature != null) {
                    originalContainingFeatureDeliver = originalContainingFeature.eDeliver();
                    originalContainingFeature.eSetDeliver(false);
                }
                originalContainmentFeature = dataObject.eContainmentFeature();
                if (originalContainmentFeature == originalContainingFeature)
                    originalContainmentFeature = null;
                if (originalContainmentFeature != null) {
                    originalContainmentFeatureDeliver = originalContainmentFeature.eDeliver();
                    originalContainmentFeature.eSetDeliver(false);
                }
            } else {
                originalResource = dataObject.eResource();
                if (originalResource != null) {
                    originalResourceDeliver = originalResource.eDeliver();
                    originalResource.eSetDeliver(false);
                }
            }
        }

        /**
         * Restore the containment state of the DataObject
         */
        private void restore() {
            if (originalContainer != null) {
                if (originalContainmentFeature != null) {
                    FeatureMap map = (FeatureMap) originalContainer.eGet(originalContainingFeature);
                    map.set(originalContainmentFeature, dataObject);
                    originalContainmentFeature.eSetDeliver(originalContainmentFeatureDeliver);
                } else {
                    originalContainer.eSet(originalContainingFeature, dataObject);
                }
                originalContainingFeature.eSetDeliver(originalContainingFeatureDeliver);
                originalContainer.eSetDeliver(originalContainerDeliver);

            } else if (originalResource != null) {
                originalResource.getContents().add(dataObject);
                originalResource.eSetDeliver(originalResourceDeliver);
            }
        }
    }

    /**
     * Constructor
     */
    protected XMLHelperImpl(ConfiguredResourceSet resourceSet) {
        this.configuredResourceSet = resourceSet;
    }

    public DataObject load(String uri) throws IOException {
        return load(uri, null, null);
    }

    public DataObject load(final byte[] xml) throws IOException {
        if (xml == null)
            return null;
        try {
            return (DataObject) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    ByteArrayInputStream bis = new ByteArrayInputStream(xml);
                    return load(bis);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }

    }

    public DataObject load(InputStream inputStream) throws IOException {
        return load(null, inputStream, null);
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.XMLHelper#load(org.w3c.dom.Node)
     */
    public DataObject load(Node node) throws IOException {
        return load(null, null, node);
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.XMLHelper#print(commonj.sdo.DataObject, java.io.OutputStream)
     */
    public void print(final DataObject dataObject, final OutputStream outputStream) {

        // Configure save options, print is different from save in that it formats the XML output
        final Map options = new HashMap();
        options.put(XMLResource.OPTION_FORMATTED, Boolean.TRUE);
        options.put(XMLResource.OPTION_LINE_WIDTH, new Integer(80));

        try {
            //SECURITY
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    save((EDataObject) dataObject, null, outputStream, null, options);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw new WrappedException(e.getException());
        }
    }

    public void save(DataObject dataObject, String uri) throws IOException {
        save((EDataObject) dataObject, uri, null, null, null);
    }

    public void save(DataObject dataObject, OutputStream outputStream) throws IOException {
        save((EDataObject) dataObject, null, outputStream, null, null);
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.XMLHelper#createDataObjectSerializer(commonj.sdo.DataObject)
     */
    public DataObjectSerializer createDataObjectSerializer(DataObject dataObject) {
        return new XMLDataObjectSerializer(dataObject);
    }

    /**
     * Save a DataObject
     *
     */
    private void save(EDataObject dataObject, String uriString, OutputStream outputStream, Document document, Map userOptions) throws IOException {
        // Create a resource set and resource
        ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(configuredResourceSet.getPackageRegistry());
        ResourceSet resourceSet = new DelegatingResourceSetImpl(configuredResourceSet);

        // Save the current DataObject's container and containing feature, we have to detach the DataObject from its
        // container to save it, reattach it after
        DataObjectContainmentState containmentState = new DataObjectContainmentState(dataObject);

        // Attach the DataObject to its DocumentRoot
        EObject object = attachToDocumentRoot(extendedMetaData, (EDataObject) dataObject);

        URI uri = (uriString == null || uriString.equals("")) ? DATAOBJECT_URI : URI.createURI(uriString);
        XMLResource resource = (XMLResource) new XMLResourceFactoryImpl().createResource(uri);
        resourceSet.getResources().add(resource);

        // Add to the resource
        resource.getContents().add(object);

        // Configure save options
        Map options = new HashMap();
        options.put(XMLResource.OPTION_FORMATTED, Boolean.FALSE);
        options.put(XMLResource.OPTION_LINE_WIDTH, new Integer(Integer.MAX_VALUE));
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);

        if (userOptions != null)
            options.putAll(userOptions);

        if (document == null) {

            // Save the resource to an output stream
            if (outputStream == null)
                outputStream = resourceSet.getURIConverter().createOutputStream(uri);
            resource.save(outputStream, options);
        } else {

            // Save the resource to a DOM document
            resource.save(document, options, null);
        }

        // Detach from the resource
        resource.getContents().clear();

        // Restore the containment state of the DataObject
        containmentState.restore();
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.XMLHelper#save(commonj.sdo.DataObject, org.w3c.dom.Document)
     */
    public void save(DataObject dataObject, Document document) throws IOException {
        save((EDataObject) dataObject, null, null, document, null);
    }

    /**
     * Load a DataObject
     *
     */
    private DataObject load(String uriString, InputStream inputStream, Node node) throws IOException {
        // Create a local resource set, extended metadata and resource
        ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(configuredResourceSet.getPackageRegistry());
        ResourceSet resourceSet = new DelegatingResourceSetImpl(configuredResourceSet);
        URI uri = (uriString == null) ? DATAOBJECT_URI : URI.createURI(uriString);
        XMLResource resource = (XMLResource) new XMLResourceFactoryImpl().createResource(uri);
        resourceSet.getResources().add(resource);

        // Configure the load options
        Map options = new HashMap();
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        Map xmlNameToFeatureMap = new HashMap();
        options.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, xmlNameToFeatureMap);

        if (node == null) {

            // Load from an input stream
            if (inputStream == null)
                inputStream = resourceSet.getURIConverter().createInputStream(uri);
            resource.load(inputStream, options);
        } else {

            // Load from a DOM node
            resource.load(node, options);
        }

        if (resource.getContents().isEmpty())
            return null;

        // Get the root object
        EDataObject root = (EDataObject) resource.getContents().get(0);

        // FIXME: [rfeng] EMF doesn't have a way to tell if the saved EObject has DocumentRoot or not
        // We now assume DocumentRoot is not saved so we need to detach the DocumentRoot first so the
        // proxy URIs can be fully resolved

        // Get the top element out of the DocumentRoot
        EClass rootClass = extendedMetaData.getDocumentRoot(root.eClass().getEPackage());
        if (rootClass != null && rootClass.isInstance(root)) {
            List elements = extendedMetaData.getElements(rootClass);
            for (Iterator i = elements.iterator(); i.hasNext();) {
                EStructuralFeature f = (EStructuralFeature) i.next();
                if (!root.eIsSet(f))
                    continue;
                Object element = root.eGet(f);
                if (element instanceof EDataObject) {
                    root = (EDataObject) element;
                    break;
                }
            }
        }

        // Detach from the resource. DON'T do resource.unload() since it will turn all contents into proxies
        resource.getContents().clear();

        return root;
    }

    /**
     * Attach the given EObject to its DocumentRoot object
     * @param extendedMetaData
     * @param object
     */
    private EObject attachToDocumentRoot(ExtendedMetaData extendedMetaData, EObject object) {
        if (object == null)
            return null;

        // Get the DocumentRoot EClass for the object's EPackage
        EClass documentRootEClass = extendedMetaData.getDocumentRoot(object.eClass().getEPackage());
        if (documentRootEClass == null) {

            // No document root found, this object will be saved using its XMI name
            return object;

        } else {

            // Check if the object itself is a DocumentRoot
            if (documentRootEClass.isInstance(object)) {
                return object;
            }

            // Check if the object is already in the correct DocumentRoot
            if (documentRootEClass.isInstance(object.eContainer())) {
                return object.eContainer();
            }

            //	Try to see if the object can be contained by a DocumentRoot
            for (Iterator i = extendedMetaData.getElements(documentRootEClass).iterator(); i.hasNext();) {
                EStructuralFeature ref = (EStructuralFeature) i.next();
                if (ref.getEType().isInstance(object)) {

                    // Create the Document Root
                    EObject documentRoot = EcoreUtil.create(documentRootEClass);

                    // Attach the object to the Document root
                    documentRoot.eSet(ref, object);
                    return documentRoot;
                }
            }

            EClass cls = object.eClass();
            String name = extendedMetaData.getName(cls);
            String ns = extendedMetaData.getName(cls);

            throw new IllegalArgumentException("{" + ns + "}" + name + " does not correspond to a global element.");
        }
	}
	
}