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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;

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
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ModelConfigurationFactoryImpl extends EFactoryImpl implements ModelConfigurationFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModelConfigurationFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case ModelConfigurationPackage.DOCUMENT_ROOT:
            return createDocumentRoot();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE:
            return createDynamicPackage();
        case ModelConfigurationPackage.DYNAMIC_PACKAGE_LOADER:
            return createDynamicPackageLoader();
        case ModelConfigurationPackage.GENERATED_PACKAGE:
            return createGeneratedPackage();
        case ModelConfigurationPackage.MODEL_CONFIGURATION:
            return createModelConfiguration();
        case ModelConfigurationPackage.RESOURCE_FACTORY:
            return createResourceFactory();
        case ModelConfigurationPackage.URI_MAPPING:
            return createURIMapping();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public DocumentRoot createDocumentRoot() {
        DocumentRootImpl documentRoot = new DocumentRootImpl();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public DynamicPackage createDynamicPackage() {
        DynamicPackageImpl dynamicPackage = new DynamicPackageImpl();
        return dynamicPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public DynamicPackageLoader createDynamicPackageLoader() {
        DynamicPackageLoaderImpl dynamicPackageLoader = new DynamicPackageLoaderImpl();
        return dynamicPackageLoader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public GeneratedPackage createGeneratedPackage() {
        GeneratedPackageImpl generatedPackage = new GeneratedPackageImpl();
        return generatedPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModelConfiguration createModelConfiguration() {
        ModelConfigurationImpl modelConfiguration = new ModelConfigurationImpl();
        return modelConfiguration;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ResourceFactory createResourceFactory() {
        ResourceFactoryImpl resourceFactory = new ResourceFactoryImpl();
        return resourceFactory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public URIMapping createURIMapping() {
        URIMappingImpl uriMapping = new URIMappingImpl();
        return uriMapping;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModelConfigurationPackage getModelConfigurationPackage() {
        return (ModelConfigurationPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static ModelConfigurationPackage getPackage() {
        return ModelConfigurationPackage.eINSTANCE;
    }

} //ModelConfigFactoryImpl
