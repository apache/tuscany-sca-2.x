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
package org.apache.tuscany.model.config;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @model kind="package"
 * @generated
 * @see ModelConfigurationFactory
 */
public interface ModelConfigurationPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "config";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http://org.apache.tuscany/xmlns/model/config/0.9";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "config";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    ModelConfigurationPackage eINSTANCE = org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.DocumentRootImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getDocumentRoot()
     */
    int DOCUMENT_ROOT = 0;

    /**
     * The feature id for the '<em><b>Mixed</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MIXED = 0;

    /**
     * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

    /**
     * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Model Configuration</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MODEL_CONFIGURATION = 3;

    /**
     * The number of structural features of the the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.DynamicPackageImpl <em>Dynamic Package</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.DynamicPackageImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getDynamicPackage()
     */
    int DYNAMIC_PACKAGE = 1;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE__ANY = 0;

    /**
     * The feature id for the '<em><b>Loader Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE__LOADER_CLASS_NAME = 1;

    /**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE__LOCATION = 2;

    /**
     * The feature id for the '<em><b>Pre Load</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE__PRE_LOAD = 3;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE__URI = 4;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE__ANY_ATTRIBUTE = 5;

    /**
     * The number of structural features of the the '<em>Dynamic Package</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.DynamicPackageLoaderImpl <em>Dynamic Package Loader</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.DynamicPackageLoaderImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getDynamicPackageLoader()
     */
    int DYNAMIC_PACKAGE_LOADER = 2;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_LOADER__ANY = 0;

    /**
     * The feature id for the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_LOADER__CLASS_NAME = 1;

    /**
     * The feature id for the '<em><b>Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_LOADER__EXTENSION = 2;

    /**
     * The feature id for the '<em><b>Protocol</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_LOADER__PROTOCOL = 3;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_LOADER__ANY_ATTRIBUTE = 4;

    /**
     * The number of structural features of the the '<em>Dynamic Package Loader</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int DYNAMIC_PACKAGE_LOADER_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.GeneratedPackageImpl <em>Generated Package</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.GeneratedPackageImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getGeneratedPackage()
     */
    int GENERATED_PACKAGE = 3;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int GENERATED_PACKAGE__ANY = 0;

    /**
     * The feature id for the '<em><b>Package Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int GENERATED_PACKAGE__PACKAGE_CLASS_NAME = 1;

    /**
     * The feature id for the '<em><b>Pre Load</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int GENERATED_PACKAGE__PRE_LOAD = 2;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int GENERATED_PACKAGE__URI = 3;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int GENERATED_PACKAGE__ANY_ATTRIBUTE = 4;

    /**
     * The number of structural features of the the '<em>Generated Package</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int GENERATED_PACKAGE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.ModelConfigurationImpl <em>Model Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getModelConfiguration()
     */
    int MODEL_CONFIGURATION = 4;

    /**
     * The feature id for the '<em><b>Generated Packages</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__GENERATED_PACKAGES = 0;

    /**
     * The feature id for the '<em><b>Dynamic Packages</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__DYNAMIC_PACKAGES = 1;

    /**
     * The feature id for the '<em><b>Dynamic Package Loaders</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__DYNAMIC_PACKAGE_LOADERS = 2;

    /**
     * The feature id for the '<em><b>Uri Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__URI_MAPPINGS = 3;

    /**
     * The feature id for the '<em><b>Resource Factories</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__RESOURCE_FACTORIES = 4;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__ANY = 5;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION__ANY_ATTRIBUTE = 6;

    /**
     * The number of structural features of the the '<em>Model Configuration</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MODEL_CONFIGURATION_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.ResourceFactoryImpl <em>Resource Factory</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.ResourceFactoryImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getResourceFactory()
     */
    int RESOURCE_FACTORY = 5;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RESOURCE_FACTORY__ANY = 0;

    /**
     * The feature id for the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RESOURCE_FACTORY__CLASS_NAME = 1;

    /**
     * The feature id for the '<em><b>Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RESOURCE_FACTORY__EXTENSION = 2;

    /**
     * The feature id for the '<em><b>Protocol</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RESOURCE_FACTORY__PROTOCOL = 3;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RESOURCE_FACTORY__ANY_ATTRIBUTE = 4;

    /**
     * The number of structural features of the the '<em>Resource Factory</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RESOURCE_FACTORY_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.apache.tuscany.model.config.impl.URIMappingImpl <em>URI Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.config.impl.URIMappingImpl
     * @see org.apache.tuscany.model.config.impl.ModelConfigurationPackageImpl#getURIMapping()
     */
    int URI_MAPPING = 6;

    /**
     * The feature id for the '<em><b>Any</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int URI_MAPPING__ANY = 0;

    /**
     * The feature id for the '<em><b>Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int URI_MAPPING__SOURCE = 1;

    /**
     * The feature id for the '<em><b>Target</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int URI_MAPPING__TARGET = 2;

    /**
     * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int URI_MAPPING__ANY_ATTRIBUTE = 3;

    /**
     * The number of structural features of the the '<em>URI Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int URI_MAPPING_FEATURE_COUNT = 4;


    /**
     * Returns the meta object for class '{@link DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Document Root</em>'.
     * @generated
     * @see DocumentRoot
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @generated
     * @see DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @generated
     * @see DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @generated
     * @see DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link DocumentRoot#getModelConfiguration <em>Model Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference '<em>Model Configuration</em>'.
     * @generated
     * @see DocumentRoot#getModelConfiguration()
     * @see #getDocumentRoot()
     */
    EReference getDocumentRoot_ModelConfiguration();

    /**
     * Returns the meta object for class '{@link DynamicPackage <em>Dynamic Package</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Dynamic Package</em>'.
     * @generated
     * @see DynamicPackage
     */
    EClass getDynamicPackage();

    /**
     * Returns the meta object for the attribute list '{@link DynamicPackage#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see DynamicPackage#getAny()
     * @see #getDynamicPackage()
     */
    EAttribute getDynamicPackage_Any();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackage#getLoaderClassName <em>Loader Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Loader Class Name</em>'.
     * @generated
     * @see DynamicPackage#getLoaderClassName()
     * @see #getDynamicPackage()
     */
    EAttribute getDynamicPackage_LoaderClassName();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackage#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Location</em>'.
     * @generated
     * @see DynamicPackage#getLocation()
     * @see #getDynamicPackage()
     */
    EAttribute getDynamicPackage_Location();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackage#isPreLoad <em>Pre Load</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Pre Load</em>'.
     * @generated
     * @see DynamicPackage#isPreLoad()
     * @see #getDynamicPackage()
     */
    EAttribute getDynamicPackage_PreLoad();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackage#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @generated
     * @see DynamicPackage#getUri()
     * @see #getDynamicPackage()
     */
    EAttribute getDynamicPackage_Uri();

    /**
     * Returns the meta object for the attribute list '{@link DynamicPackage#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see DynamicPackage#getAnyAttribute()
     * @see #getDynamicPackage()
     */
    EAttribute getDynamicPackage_AnyAttribute();

    /**
     * Returns the meta object for class '{@link DynamicPackageLoader <em>Dynamic Package Loader</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Dynamic Package Loader</em>'.
     * @generated
     * @see DynamicPackageLoader
     */
    EClass getDynamicPackageLoader();

    /**
     * Returns the meta object for the attribute list '{@link DynamicPackageLoader#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see DynamicPackageLoader#getAny()
     * @see #getDynamicPackageLoader()
     */
    EAttribute getDynamicPackageLoader_Any();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackageLoader#getClassName <em>Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Class Name</em>'.
     * @generated
     * @see DynamicPackageLoader#getClassName()
     * @see #getDynamicPackageLoader()
     */
    EAttribute getDynamicPackageLoader_ClassName();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackageLoader#getExtension <em>Extension</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Extension</em>'.
     * @generated
     * @see DynamicPackageLoader#getExtension()
     * @see #getDynamicPackageLoader()
     */
    EAttribute getDynamicPackageLoader_Extension();

    /**
     * Returns the meta object for the attribute '{@link DynamicPackageLoader#getProtocol <em>Protocol</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Protocol</em>'.
     * @generated
     * @see DynamicPackageLoader#getProtocol()
     * @see #getDynamicPackageLoader()
     */
    EAttribute getDynamicPackageLoader_Protocol();

    /**
     * Returns the meta object for the attribute list '{@link DynamicPackageLoader#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see DynamicPackageLoader#getAnyAttribute()
     * @see #getDynamicPackageLoader()
     */
    EAttribute getDynamicPackageLoader_AnyAttribute();

    /**
     * Returns the meta object for class '{@link GeneratedPackage <em>Generated Package</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Generated Package</em>'.
     * @generated
     * @see GeneratedPackage
     */
    EClass getGeneratedPackage();

    /**
     * Returns the meta object for the attribute list '{@link GeneratedPackage#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see GeneratedPackage#getAny()
     * @see #getGeneratedPackage()
     */
    EAttribute getGeneratedPackage_Any();

    /**
     * Returns the meta object for the attribute '{@link GeneratedPackage#getPackageClassName <em>Package Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Package Class Name</em>'.
     * @generated
     * @see GeneratedPackage#getPackageClassName()
     * @see #getGeneratedPackage()
     */
    EAttribute getGeneratedPackage_PackageClassName();

    /**
     * Returns the meta object for the attribute '{@link GeneratedPackage#isPreLoad <em>Pre Load</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Pre Load</em>'.
     * @generated
     * @see GeneratedPackage#isPreLoad()
     * @see #getGeneratedPackage()
     */
    EAttribute getGeneratedPackage_PreLoad();

    /**
     * Returns the meta object for the attribute '{@link GeneratedPackage#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @generated
     * @see GeneratedPackage#getUri()
     * @see #getGeneratedPackage()
     */
    EAttribute getGeneratedPackage_Uri();

    /**
     * Returns the meta object for the attribute list '{@link GeneratedPackage#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see GeneratedPackage#getAnyAttribute()
     * @see #getGeneratedPackage()
     */
    EAttribute getGeneratedPackage_AnyAttribute();

    /**
     * Returns the meta object for class '{@link ModelConfiguration <em>Model Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Model Configuration</em>'.
     * @generated
     * @see ModelConfiguration
     */
    EClass getModelConfiguration();

    /**
     * Returns the meta object for the containment reference list '{@link ModelConfiguration#getGeneratedPackages <em>Generated Packages</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Generated Packages</em>'.
     * @generated
     * @see ModelConfiguration#getGeneratedPackages()
     * @see #getModelConfiguration()
     */
    EReference getModelConfiguration_GeneratedPackages();

    /**
     * Returns the meta object for the containment reference list '{@link ModelConfiguration#getDynamicPackages <em>Dynamic Packages</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Dynamic Packages</em>'.
     * @generated
     * @see ModelConfiguration#getDynamicPackages()
     * @see #getModelConfiguration()
     */
    EReference getModelConfiguration_DynamicPackages();

    /**
     * Returns the meta object for the containment reference list '{@link ModelConfiguration#getDynamicPackageLoaders <em>Dynamic Package Loaders</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Dynamic Package Loaders</em>'.
     * @generated
     * @see ModelConfiguration#getDynamicPackageLoaders()
     * @see #getModelConfiguration()
     */
    EReference getModelConfiguration_DynamicPackageLoaders();

    /**
     * Returns the meta object for the containment reference list '{@link ModelConfiguration#getUriMappings <em>Uri Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Uri Mappings</em>'.
     * @generated
     * @see ModelConfiguration#getUriMappings()
     * @see #getModelConfiguration()
     */
    EReference getModelConfiguration_UriMappings();

    /**
     * Returns the meta object for the containment reference list '{@link ModelConfiguration#getResourceFactories <em>Resource Factories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the containment reference list '<em>Resource Factories</em>'.
     * @generated
     * @see ModelConfiguration#getResourceFactories()
     * @see #getModelConfiguration()
     */
    EReference getModelConfiguration_ResourceFactories();

    /**
     * Returns the meta object for the attribute list '{@link ModelConfiguration#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see ModelConfiguration#getAny()
     * @see #getModelConfiguration()
     */
    EAttribute getModelConfiguration_Any();

    /**
     * Returns the meta object for the attribute list '{@link ModelConfiguration#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see ModelConfiguration#getAnyAttribute()
     * @see #getModelConfiguration()
     */
    EAttribute getModelConfiguration_AnyAttribute();

    /**
     * Returns the meta object for class '{@link ResourceFactory <em>Resource Factory</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Resource Factory</em>'.
     * @generated
     * @see ResourceFactory
     */
    EClass getResourceFactory();

    /**
     * Returns the meta object for the attribute list '{@link ResourceFactory#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see ResourceFactory#getAny()
     * @see #getResourceFactory()
     */
    EAttribute getResourceFactory_Any();

    /**
     * Returns the meta object for the attribute '{@link ResourceFactory#getClassName <em>Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Class Name</em>'.
     * @generated
     * @see ResourceFactory#getClassName()
     * @see #getResourceFactory()
     */
    EAttribute getResourceFactory_ClassName();

    /**
     * Returns the meta object for the attribute '{@link ResourceFactory#getExtension <em>Extension</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Extension</em>'.
     * @generated
     * @see ResourceFactory#getExtension()
     * @see #getResourceFactory()
     */
    EAttribute getResourceFactory_Extension();

    /**
     * Returns the meta object for the attribute '{@link ResourceFactory#getProtocol <em>Protocol</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Protocol</em>'.
     * @generated
     * @see ResourceFactory#getProtocol()
     * @see #getResourceFactory()
     */
    EAttribute getResourceFactory_Protocol();

    /**
     * Returns the meta object for the attribute list '{@link ResourceFactory#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see ResourceFactory#getAnyAttribute()
     * @see #getResourceFactory()
     */
    EAttribute getResourceFactory_AnyAttribute();

    /**
     * Returns the meta object for class '{@link URIMapping <em>URI Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>URI Mapping</em>'.
     * @generated
     * @see URIMapping
     */
    EClass getURIMapping();

    /**
     * Returns the meta object for the attribute list '{@link URIMapping#getAny <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any</em>'.
     * @generated
     * @see URIMapping#getAny()
     * @see #getURIMapping()
     */
    EAttribute getURIMapping_Any();

    /**
     * Returns the meta object for the attribute '{@link URIMapping#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Source</em>'.
     * @generated
     * @see URIMapping#getSource()
     * @see #getURIMapping()
     */
    EAttribute getURIMapping_Source();

    /**
     * Returns the meta object for the attribute '{@link URIMapping#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Target</em>'.
     * @generated
     * @see URIMapping#getTarget()
     * @see #getURIMapping()
     */
    EAttribute getURIMapping_Target();

    /**
     * Returns the meta object for the attribute list '{@link URIMapping#getAnyAttribute <em>Any Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the meta object for the attribute list '<em>Any Attribute</em>'.
     * @generated
     * @see URIMapping#getAnyAttribute()
     * @see #getURIMapping()
     */
    EAttribute getURIMapping_AnyAttribute();

    /**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ModelConfigurationFactory getModelConfigurationFactory();

} //ModelConfigPackage
