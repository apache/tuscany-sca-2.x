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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.impl.XMLTypePackageImpl;

import org.apache.tuscany.model.config.DocumentRoot;
import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.config.DynamicPackageLoader;
import org.apache.tuscany.model.config.GeneratedPackage;
import org.apache.tuscany.model.config.ModelConfiguration;
import org.apache.tuscany.model.config.ModelConfigurationFactory;
import org.apache.tuscany.model.config.ModelConfigurationPackage;
import org.apache.tuscany.model.config.ResourceFactory;
import org.apache.tuscany.model.config.URIMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ModelConfigurationPackageImpl extends EPackageImpl implements ModelConfigurationPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass documentRootEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass dynamicPackageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass dynamicPackageLoaderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass generatedPackageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass modelConfigurationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass resourceFactoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass uriMappingEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.apache.tuscany.model.config.ModelConfigurationPackage#eNS_URI
     * @see #init()
     */
    private ModelConfigurationPackageImpl() {
        super(eNS_URI, ModelConfigurationFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     */
    public static ModelConfigurationPackage init() {
        if (isInited) return (ModelConfigurationPackage) EPackage.Registry.INSTANCE.getEPackage(ModelConfigurationPackage.eNS_URI);

        // Obtain or create and register package
        ModelConfigurationPackageImpl theModelConfigurationPackage = (ModelConfigurationPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ModelConfigurationPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ModelConfigurationPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XMLTypePackageImpl.init();

        // Create package meta-data objects
        theModelConfigurationPackage.createPackageContents();

        // Initialize created meta-data
        theModelConfigurationPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theModelConfigurationPackage.freeze();

        return theModelConfigurationPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getDocumentRoot() {
        return documentRootEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_Mixed() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_XMLNSPrefixMap() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_XSISchemaLocation() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_ModelConfiguration() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getDynamicPackage() {
        return dynamicPackageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackage_Any() {
        return (EAttribute) dynamicPackageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackage_LoaderClassName() {
        return (EAttribute) dynamicPackageEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackage_Location() {
        return (EAttribute) dynamicPackageEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackage_PreLoad() {
        return (EAttribute) dynamicPackageEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackage_Uri() {
        return (EAttribute) dynamicPackageEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackage_AnyAttribute() {
        return (EAttribute) dynamicPackageEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getDynamicPackageLoader() {
        return dynamicPackageLoaderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackageLoader_Any() {
        return (EAttribute) dynamicPackageLoaderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackageLoader_ClassName() {
        return (EAttribute) dynamicPackageLoaderEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackageLoader_Extension() {
        return (EAttribute) dynamicPackageLoaderEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackageLoader_Protocol() {
        return (EAttribute) dynamicPackageLoaderEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDynamicPackageLoader_AnyAttribute() {
        return (EAttribute) dynamicPackageLoaderEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getGeneratedPackage() {
        return generatedPackageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getGeneratedPackage_Any() {
        return (EAttribute) generatedPackageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getGeneratedPackage_PackageClassName() {
        return (EAttribute) generatedPackageEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getGeneratedPackage_PreLoad() {
        return (EAttribute) generatedPackageEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getGeneratedPackage_Uri() {
        return (EAttribute) generatedPackageEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getGeneratedPackage_AnyAttribute() {
        return (EAttribute) generatedPackageEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getModelConfiguration() {
        return modelConfigurationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModelConfiguration_GeneratedPackages() {
        return (EReference) modelConfigurationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModelConfiguration_DynamicPackages() {
        return (EReference) modelConfigurationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModelConfiguration_DynamicPackageLoaders() {
        return (EReference) modelConfigurationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModelConfiguration_UriMappings() {
        return (EReference) modelConfigurationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModelConfiguration_ResourceFactories() {
        return (EReference) modelConfigurationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModelConfiguration_Any() {
        return (EAttribute) modelConfigurationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModelConfiguration_AnyAttribute() {
        return (EAttribute) modelConfigurationEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getResourceFactory() {
        return resourceFactoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getResourceFactory_Any() {
        return (EAttribute) resourceFactoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getResourceFactory_ClassName() {
        return (EAttribute) resourceFactoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getResourceFactory_Extension() {
        return (EAttribute) resourceFactoryEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getResourceFactory_Protocol() {
        return (EAttribute) resourceFactoryEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getResourceFactory_AnyAttribute() {
        return (EAttribute) resourceFactoryEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getURIMapping() {
        return uriMappingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getURIMapping_Any() {
        return (EAttribute) uriMappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getURIMapping_Source() {
        return (EAttribute) uriMappingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getURIMapping_Target() {
        return (EAttribute) uriMappingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getURIMapping_AnyAttribute() {
        return (EAttribute) uriMappingEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModelConfigurationFactory getModelConfigurationFactory() {
        return (ModelConfigurationFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        documentRootEClass = createEClass(DOCUMENT_ROOT);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        createEReference(documentRootEClass, DOCUMENT_ROOT__MODEL_CONFIGURATION);

        dynamicPackageEClass = createEClass(DYNAMIC_PACKAGE);
        createEAttribute(dynamicPackageEClass, DYNAMIC_PACKAGE__ANY);
        createEAttribute(dynamicPackageEClass, DYNAMIC_PACKAGE__LOADER_CLASS_NAME);
        createEAttribute(dynamicPackageEClass, DYNAMIC_PACKAGE__LOCATION);
        createEAttribute(dynamicPackageEClass, DYNAMIC_PACKAGE__PRE_LOAD);
        createEAttribute(dynamicPackageEClass, DYNAMIC_PACKAGE__URI);
        createEAttribute(dynamicPackageEClass, DYNAMIC_PACKAGE__ANY_ATTRIBUTE);

        dynamicPackageLoaderEClass = createEClass(DYNAMIC_PACKAGE_LOADER);
        createEAttribute(dynamicPackageLoaderEClass, DYNAMIC_PACKAGE_LOADER__ANY);
        createEAttribute(dynamicPackageLoaderEClass, DYNAMIC_PACKAGE_LOADER__CLASS_NAME);
        createEAttribute(dynamicPackageLoaderEClass, DYNAMIC_PACKAGE_LOADER__EXTENSION);
        createEAttribute(dynamicPackageLoaderEClass, DYNAMIC_PACKAGE_LOADER__PROTOCOL);
        createEAttribute(dynamicPackageLoaderEClass, DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE);

        generatedPackageEClass = createEClass(GENERATED_PACKAGE);
        createEAttribute(generatedPackageEClass, GENERATED_PACKAGE__ANY);
        createEAttribute(generatedPackageEClass, GENERATED_PACKAGE__PACKAGE_CLASS_NAME);
        createEAttribute(generatedPackageEClass, GENERATED_PACKAGE__PRE_LOAD);
        createEAttribute(generatedPackageEClass, GENERATED_PACKAGE__URI);
        createEAttribute(generatedPackageEClass, GENERATED_PACKAGE__ANY_ATTRIBUTE);

        modelConfigurationEClass = createEClass(MODEL_CONFIGURATION);
        createEReference(modelConfigurationEClass, MODEL_CONFIGURATION__GENERATED_PACKAGES);
        createEReference(modelConfigurationEClass, MODEL_CONFIGURATION__DYNAMIC_PACKAGES);
        createEReference(modelConfigurationEClass, MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS);
        createEReference(modelConfigurationEClass, MODEL_CONFIGURATION__URI_MAPPINGS);
        createEReference(modelConfigurationEClass, MODEL_CONFIGURATION__RESOURCE_FACTORIES);
        createEAttribute(modelConfigurationEClass, MODEL_CONFIGURATION__ANY);
        createEAttribute(modelConfigurationEClass, MODEL_CONFIGURATION__ANY_ATTRIBUTE);

        resourceFactoryEClass = createEClass(RESOURCE_FACTORY);
        createEAttribute(resourceFactoryEClass, RESOURCE_FACTORY__ANY);
        createEAttribute(resourceFactoryEClass, RESOURCE_FACTORY__CLASS_NAME);
        createEAttribute(resourceFactoryEClass, RESOURCE_FACTORY__EXTENSION);
        createEAttribute(resourceFactoryEClass, RESOURCE_FACTORY__PROTOCOL);
        createEAttribute(resourceFactoryEClass, RESOURCE_FACTORY__ANY_ATTRIBUTE);

        uriMappingEClass = createEClass(URI_MAPPING);
        createEAttribute(uriMappingEClass, URI_MAPPING__ANY);
        createEAttribute(uriMappingEClass, URI_MAPPING__SOURCE);
        createEAttribute(uriMappingEClass, URI_MAPPING__TARGET);
        createEAttribute(uriMappingEClass, URI_MAPPING__ANY_ATTRIBUTE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        XMLTypePackageImpl theXMLTypePackage = (XMLTypePackageImpl) EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

        // Add supertypes to classes

        // Initialize classes and features; add operations and parameters
        initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_ModelConfiguration(), this.getModelConfiguration(), null, "modelConfiguration", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

        initEClass(dynamicPackageEClass, DynamicPackage.class, "DynamicPackage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDynamicPackage_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, DynamicPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackage_LoaderClassName(), theXMLTypePackage.getString(), "loaderClassName", null, 0, 1, DynamicPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackage_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, DynamicPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackage_PreLoad(), theXMLTypePackage.getBoolean(), "preLoad", null, 0, 1, DynamicPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackage_Uri(), theXMLTypePackage.getString(), "uri", null, 0, 1, DynamicPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackage_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, DynamicPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(dynamicPackageLoaderEClass, DynamicPackageLoader.class, "DynamicPackageLoader", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDynamicPackageLoader_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, DynamicPackageLoader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackageLoader_ClassName(), theXMLTypePackage.getString(), "className", null, 0, 1, DynamicPackageLoader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackageLoader_Extension(), theXMLTypePackage.getString(), "extension", null, 0, 1, DynamicPackageLoader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackageLoader_Protocol(), theXMLTypePackage.getString(), "protocol", null, 0, 1, DynamicPackageLoader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDynamicPackageLoader_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, DynamicPackageLoader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(generatedPackageEClass, GeneratedPackage.class, "GeneratedPackage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getGeneratedPackage_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, GeneratedPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getGeneratedPackage_PackageClassName(), theXMLTypePackage.getString(), "packageClassName", null, 0, 1, GeneratedPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getGeneratedPackage_PreLoad(), theXMLTypePackage.getBoolean(), "preLoad", null, 0, 1, GeneratedPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getGeneratedPackage_Uri(), theXMLTypePackage.getString(), "uri", null, 0, 1, GeneratedPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getGeneratedPackage_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, GeneratedPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(modelConfigurationEClass, ModelConfiguration.class, "ModelConfiguration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getModelConfiguration_GeneratedPackages(), this.getGeneratedPackage(), null, "generatedPackages", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModelConfiguration_DynamicPackages(), this.getDynamicPackage(), null, "dynamicPackages", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModelConfiguration_DynamicPackageLoaders(), this.getDynamicPackageLoader(), null, "dynamicPackageLoaders", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModelConfiguration_UriMappings(), this.getURIMapping(), null, "uriMappings", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModelConfiguration_ResourceFactories(), this.getResourceFactory(), null, "resourceFactories", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModelConfiguration_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModelConfiguration_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModelConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(resourceFactoryEClass, ResourceFactory.class, "ResourceFactory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getResourceFactory_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ResourceFactory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getResourceFactory_ClassName(), theXMLTypePackage.getString(), "className", null, 0, 1, ResourceFactory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getResourceFactory_Extension(), theXMLTypePackage.getString(), "extension", null, 0, 1, ResourceFactory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getResourceFactory_Protocol(), theXMLTypePackage.getString(), "protocol", null, 0, 1, ResourceFactory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getResourceFactory_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ResourceFactory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(uriMappingEClass, URIMapping.class, "URIMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getURIMapping_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, URIMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getURIMapping_Source(), theXMLTypePackage.getString(), "source", null, 0, 1, URIMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getURIMapping_Target(), theXMLTypePackage.getString(), "target", null, 0, 1, URIMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getURIMapping_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, URIMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);

        // Create annotations
        // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
        createExtendedMetaDataAnnotations();
    }

    /**
     * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void createExtendedMetaDataAnnotations() {
        String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
        addAnnotation
                (documentRootEClass,
                        source,
                        new String[]{
                                "name", "",
                                "kind", "mixed"
                        });
        addAnnotation
                (getDocumentRoot_Mixed(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "name", ":mixed"
                        });
        addAnnotation
                (getDocumentRoot_XMLNSPrefixMap(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "xmlns:prefix"
                        });
        addAnnotation
                (getDocumentRoot_XSISchemaLocation(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "xsi:schemaLocation"
                        });
        addAnnotation
                (getDocumentRoot_ModelConfiguration(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "modelConfiguration",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (dynamicPackageEClass,
                        source,
                        new String[]{
                                "name", "DynamicPackage",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getDynamicPackage_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getDynamicPackage_LoaderClassName(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "loaderClassName"
                        });
        addAnnotation
                (getDynamicPackage_Location(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "location"
                        });
        addAnnotation
                (getDynamicPackage_PreLoad(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "preLoad"
                        });
        addAnnotation
                (getDynamicPackage_Uri(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "uri"
                        });
        addAnnotation
                (getDynamicPackage_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":5",
                                "processing", "lax"
                        });
        addAnnotation
                (dynamicPackageLoaderEClass,
                        source,
                        new String[]{
                                "name", "DynamicPackageLoader",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getDynamicPackageLoader_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getDynamicPackageLoader_ClassName(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "className"
                        });
        addAnnotation
                (getDynamicPackageLoader_Extension(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "extension"
                        });
        addAnnotation
                (getDynamicPackageLoader_Protocol(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "protocol"
                        });
        addAnnotation
                (getDynamicPackageLoader_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (generatedPackageEClass,
                        source,
                        new String[]{
                                "name", "GeneratedPackage",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getGeneratedPackage_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getGeneratedPackage_PackageClassName(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "packageClassName"
                        });
        addAnnotation
                (getGeneratedPackage_PreLoad(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "preLoad"
                        });
        addAnnotation
                (getGeneratedPackage_Uri(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "uri"
                        });
        addAnnotation
                (getGeneratedPackage_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (modelConfigurationEClass,
                        source,
                        new String[]{
                                "name", "ModelConfiguration",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getModelConfiguration_GeneratedPackages(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "generatedPackage"
                        });
        addAnnotation
                (getModelConfiguration_DynamicPackages(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "dynamicPackage"
                        });
        addAnnotation
                (getModelConfiguration_DynamicPackageLoaders(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "dynamicPackageLoader"
                        });
        addAnnotation
                (getModelConfiguration_UriMappings(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "uriMapping"
                        });
        addAnnotation
                (getModelConfiguration_ResourceFactories(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "resourceFactory"
                        });
        addAnnotation
                (getModelConfiguration_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":5",
                                "processing", "lax"
                        });
        addAnnotation
                (getModelConfiguration_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":6",
                                "processing", "lax"
                        });
        addAnnotation
                (resourceFactoryEClass,
                        source,
                        new String[]{
                                "name", "ResourceFactory",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getResourceFactory_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getResourceFactory_ClassName(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "className"
                        });
        addAnnotation
                (getResourceFactory_Extension(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "extension"
                        });
        addAnnotation
                (getResourceFactory_Protocol(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "protocol"
                        });
        addAnnotation
                (getResourceFactory_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (uriMappingEClass,
                        source,
                        new String[]{
                                "name", "URIMapping",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getURIMapping_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getURIMapping_Source(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "source"
                        });
        addAnnotation
                (getURIMapping_Target(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "target"
                        });
        addAnnotation
                (getURIMapping_AnyAttribute(),
                        source,
		   new String[] {
			 "kind", "attributeWildcard",
			 "wildcards", "##any",
			 "name", ":3",
			 "processing", "lax"
		   });
	}

} //ModelConfigPackageImpl
