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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see ModelConfigurationPackage
 */
public interface ModelConfigurationFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    ModelConfigurationFactory eINSTANCE = new org.apache.tuscany.model.config.impl.ModelConfigurationFactoryImpl();

    /**
     * Returns a new object of class '<em>Document Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Document Root</em>'.
     * @generated
     */
    DocumentRoot createDocumentRoot();

    /**
     * Returns a new object of class '<em>Dynamic Package</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Dynamic Package</em>'.
     * @generated
     */
    DynamicPackage createDynamicPackage();

    /**
     * Returns a new object of class '<em>Dynamic Package Loader</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Dynamic Package Loader</em>'.
     * @generated
     */
    DynamicPackageLoader createDynamicPackageLoader();

    /**
     * Returns a new object of class '<em>Generated Package</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Generated Package</em>'.
     * @generated
     */
    GeneratedPackage createGeneratedPackage();

    /**
     * Returns a new object of class '<em>Model Configuration</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Model Configuration</em>'.
     * @generated
     */
    ModelConfiguration createModelConfiguration();

    /**
     * Returns a new object of class '<em>Resource Factory</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>Resource Factory</em>'.
     * @generated
     */
    ResourceFactory createResourceFactory();

    /**
     * Returns a new object of class '<em>URI Mapping</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return a new object of class '<em>URI Mapping</em>'.
     * @generated
     */
    URIMapping createURIMapping();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the package supported by this factory.
     * @generated
     */
    ModelConfigurationPackage getModelConfigurationPackage();

} //ModelConfigFactory
