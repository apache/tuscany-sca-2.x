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
package org.apache.tuscany.model.types.wsdl.impl;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.sdo.impl.DynamicEDataObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyConstants;
import org.apache.tuscany.model.types.wsdl.WSDLInterfaceType;
import org.apache.tuscany.model.types.xsd.impl.XMLNameUtil;
import org.apache.tuscany.model.types.xsd.impl.XSD2SDOBuilder;

/**
 * A dynamic package model loader for XSD models.
 */
public class WSDLDynamicPackageLoaderImpl implements EPackage.Descriptor {
    private DynamicPackage dynamicPackage;
    private ResourceLoader bundleContext;
    private EPackage ePackage;

    /**
     * Constructor
     *
     * @param dynamicPackage
     */
    public WSDLDynamicPackageLoaderImpl(DynamicPackage dynamicPackage, ResourceLoader bundleContext) {
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

        // Load the WSDL document into a WSDL resource
        ConfiguredResourceSet resourceSet = (ConfiguredResourceSet) ((EObject) dynamicPackage).eResource().getResourceSet();
        Resource resource = resourceSet.getResource(location, true);

        // Load  inline XSDs and build the corresponding EMF packages
        Map packages = new HashMap();
        XSDEcoreBuilder builder = new XSD2SDOBuilder(resourceSet, nsURI);
        Definition definition = null;
        for (Iterator contents = resource.getContents().iterator(); contents.hasNext();) {
            Object content = contents.next();

            // Only process XSDSchemas
            if (content instanceof Definition) {
                definition = (Definition) content;
                Types types = definition.getETypes();
                if (types != null) {
                    for (Iterator schemas = types.getSchemas().iterator(); schemas.hasNext();) {
                        XSDSchema xsdSchema = (XSDSchema) schemas.next();
                        builder.generate(xsdSchema);
                    }
                }
                break;
            }
        }

        // Add all packages
        Map ePackages = builder.getTargetNamespaceToEPackageMap();
        packages.putAll(ePackages);

        // Register the loaded packages and make sure that they use the SDO factory
        EPackage.Registry packageRegistry = resourceSet.getPackageRegistry();
        for (Iterator i = packages.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            EPackage pkg = (EPackage) entry.getValue();

            EFactory eFactory = pkg.getEFactoryInstance();
            if (eFactory == null || eFactory.getClass() == EFactoryImpl.class)
                pkg.setEFactoryInstance(new DynamicEDataObjectImpl.FactoryImpl());

            packageRegistry.put(key, pkg);
            if (pkg.eResource() == null) {
                Resource ePackageResource = new XMIResourceImpl(URI.createURI(key));
                ePackageResource.getContents().add(pkg);
                resourceSet.getResources().add(ePackageResource);
            }
        }

        // Get the package with the target URI
        EPackage pkg = (EPackage) packages.get(nsURI);

        // Process the WSDL definition
        if (definition != null) {
            if (pkg == null) {

                // Create a new package
                pkg = EcoreFactory.eINSTANCE.createEPackage();
                pkg.setNsURI(nsURI);
                String ePackageName = XMLNameUtil.INSTANCE.getPackageNameFromNamespace(nsURI);
                pkg.setName(ePackageName);
                pkg.setNsPrefix(XMLNameUtil.INSTANCE.getNSPrefixFromPackageName(ePackageName));

                // set the SDO factory so that EClasses representing WSDL messages can be used as SDO types
                pkg.setEFactoryInstance(new DynamicEDataObjectImpl.FactoryImpl());

                // Register the package
                packageRegistry.put(nsURI, pkg);
            }
            WSDLDefinitionAdapter adapter = new WSDLDefinitionAdapterImpl(definition);
            pkg.eAdapters().add(adapter);

            // Populate the EPackage from the WSDL portTypes and operations
            ExtendedMetaData extendedMetaData = resourceSet.getExtendedMetaData();
            for (Iterator portTypes = definition.getEPortTypes().iterator(); portTypes.hasNext();) {
                PortType portType = (PortType) portTypes.next();

                // Create an EClass for the portType
                String localPart = portType.getQName().getLocalPart();
                EClass eClass = new WSDLInterfaceTypeImpl(portType);
                eClass.setName(AssemblyConstants.WSDL_INTERFACE_NAME_PREFIX + localPart);
                extendedMetaData.setName(eClass, localPart);
                eClass.setInterface(true);
                pkg.getEClassifiers().add(eClass);

                // Process operations
                for (Iterator operations = portType.getEOperations().iterator(); operations.hasNext();) {
                    Operation operation = (Operation) operations.next();

                    // Create an EOperation for the operation
                    EOperation eOperation = new WSDLOperationTypeImpl(operation);
                    eOperation.setName(operation.getName());
                    eClass.getEOperations().add(eOperation);

                    // Create the input EParameter
                    if (operation.getInput() != null) {
                        Message message = operation.getEInput().getEMessage();
                        if (message != null) {
                            EClassifier messageEClassifier = getEClassifier(message, pkg, extendedMetaData);
                            if (messageEClassifier != null) {

                                // Create an EParameter
                                EParameter eParameter = EcoreFactory.eINSTANCE.createEParameter();
                                eParameter.setName(operation.getName());
                                eParameter.setEType(messageEClassifier);
                                eOperation.getEParameters().add(eParameter);
                            }
                        }
                    }

                    // Create the output EType
                    if (operation.getOutput() != null) {
                        Message message = operation.getEOutput().getEMessage();
                        if (message != null) {
                            EClassifier messageEClassifier = getEClassifier(message, pkg, extendedMetaData);
                            if (messageEClassifier != null) {

                                // Set the EType
                                eOperation.setEType(messageEClassifier);
                            }
                        }
                    }

                    // Create EExceptions
                    for (Iterator faults = operation.getEFaults().iterator(); faults.hasNext();) {
                        Fault fault = (Fault) faults.next();
                        Message message = fault.getEMessage();
                        if (message != null) {
                            EClassifier messageEClassifier = getEClassifier(message, pkg, extendedMetaData);
                            if (messageEClassifier != null) {

                                // Add an EException
                                eOperation.getEExceptions().add(messageEClassifier);
                            }
                        }
                    }
                }

                // Initialize the interface type
                //FIXME pass a TModelContext
                ((WSDLInterfaceType) eClass).initialize(null);
            }
        }

        // Register the package
        if (pkg != null) {
            packageRegistry.put(nsURI, pkg);
            if (pkg.eResource() == null) {
                Resource ePackageResource = new XMIResourceImpl(URI.createURI(nsURI));
                ePackageResource.getContents().add(pkg);
                resourceSet.getResources().add(ePackageResource);
            }
        }

        return pkg;
    }

    /**
     * Returns a EStructuralFeature representing a message part.
     *
     * @param part
     * @param extendedMetaData
     */
    private EStructuralFeature getEStructuralFeature(Part part, ExtendedMetaData extendedMetaData, EClass msgEClass) {
        QName qname = part.getElementName();
        if (qname != null) {
            EStructuralFeature element = extendedMetaData.getElement(qname.getNamespaceURI(), qname.getLocalPart());
            if (element != null) {
                EStructuralFeature feature = (EStructuralFeature) EcoreUtil.copy(element);
                feature.setDerived(false);
                feature.setTransient(false);
                feature.setVolatile(false);
                // Set the feature name to the part name
                feature.setName(XMLNameUtil.INSTANCE.getValidNameFromXMLName(part.getName(), false));
                // Add the feature to EClass before setting extended metadata
                msgEClass.getEStructuralFeatures().add(feature);
                extendedMetaData.setAffiliation(feature, extendedMetaData.getAffiliation(element));
                extendedMetaData.setFeatureKind(feature, extendedMetaData.getFeatureKind(element));
                extendedMetaData.setGroup(feature, extendedMetaData.getGroup(element));
                extendedMetaData.setProcessingKind(feature, extendedMetaData.getProcessingKind(element));
                extendedMetaData.setWildcards(feature, extendedMetaData.getWildcards(element));
                // Set the XML namespace/name to the elment so that it can serialized as the element
                extendedMetaData.setNamespace(feature, qname.getNamespaceURI());
                extendedMetaData.setName(feature, qname.getLocalPart());
                return feature;
            } else {
                throw new IllegalArgumentException("Cannot find element for " + qname);
            }
        } else {
            qname = part.getTypeName();
            if (qname != null) {
                EClassifier classifier = extendedMetaData.getType(qname.getNamespaceURI(), qname.getLocalPart());
                if (classifier == null) {
                    throw new IllegalArgumentException("Cannot find type for " + qname);
                }
                if (classifier instanceof EDataType) {
                    EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
                    attribute.setEType(classifier);
                    attribute.setName(XMLNameUtil.INSTANCE.getValidNameFromXMLName(part.getName(), false));
                    msgEClass.getEStructuralFeatures().add(attribute);
                    extendedMetaData.setName(attribute, part.getName());
                    return attribute;
                } else {
                    EReference reference = EcoreFactory.eINSTANCE.createEReference();
                    reference.setContainment(true);
                    reference.setEType(classifier);
                    reference.setName(XMLNameUtil.INSTANCE.getValidNameFromXMLName(part.getName(), false));
                    msgEClass.getEStructuralFeatures().add(reference);
                    extendedMetaData.setName(reference, part.getName());
                    return reference;
                }
            } else
                throw new IllegalArgumentException("Missing type or element name for part " + part.getName());
        }
    }

    /**
     * Returns an EClassifier representing a WSDL message.
     * @param message
     * @param ePackage
     * @param extendedMetaData
     */
    private EClassifier getEClassifier(Message message, EPackage ePackage, ExtendedMetaData extendedMetaData) {

        // Get the message parts
        List parts = message.getEParts();
        int n = parts.size();
        if (n == 0) {
            // Empty message
            return null;
        }

        // Lookup an EClass for the given message
        QName qname = message.getQName();
        EClass messageEClass = null;
        EPackage messageEPackage = extendedMetaData.getPackage(qname.getNamespaceURI());
        if (messageEPackage != null) {
            messageEClass = (EClass) messageEPackage.getEClassifier(AssemblyConstants.WSDL_MESSAGE_NAME_PREFIX + qname.getLocalPart());
            if (messageEClass != null)
                return messageEClass;
        }

        // Create a new EClass if it's in the current EPackage
        if (!qname.getNamespaceURI().equals(ePackage.getNsURI())) {
            throw new IllegalArgumentException("Cannot find WSDL message for " + qname);
        }
        messageEClass = new WSDLMessageTypeImpl(message);
        messageEClass.setName(AssemblyConstants.WSDL_MESSAGE_NAME_PREFIX + qname.getLocalPart());
        extendedMetaData.setName(messageEClass, qname.getLocalPart());
        ePackage.getEClassifiers().add(messageEClass);

        // Create EStructuralFeatures for the message parts
        for (Iterator p = message.getEParts().iterator(); p.hasNext();) {
            Part part = (Part) p.next();
            EStructuralFeature feature = getEStructuralFeature(part, extendedMetaData, messageEClass);
//			messageEClass.getEStructuralFeatures().add(feature);
        }
        return messageEClass;

    }

}