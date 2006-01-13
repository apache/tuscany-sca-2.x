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
package org.apache.tuscany.model.assembly.sdo.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EClassifierImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.impl.XMLTypePackageImpl;
import org.osoa.sca.model.Binding;
import org.osoa.sca.model.Component;
import org.osoa.sca.model.ComponentType;
import org.osoa.sca.model.DocumentRoot;
import org.osoa.sca.model.EntryPoint;
import org.osoa.sca.model.ExternalService;
import org.osoa.sca.model.Implementation;
import org.osoa.sca.model.Interface;
import org.osoa.sca.model.JavaInterface;
import org.osoa.sca.model.Module;
import org.osoa.sca.model.ModuleComponent;
import org.osoa.sca.model.ModuleFragment;
import org.osoa.sca.model.ModuleWire;
import org.osoa.sca.model.Property;
import org.osoa.sca.model.PropertyValues;
import org.osoa.sca.model.Reference;
import org.osoa.sca.model.ReferenceValues;
import org.osoa.sca.model.SCABinding;
import org.osoa.sca.model.Service;
import org.osoa.sca.model.Subsystem;
import org.osoa.sca.model.SystemWire;
import org.osoa.sca.model.WSDLPortType;

import org.apache.tuscany.model.assembly.sdo.AssemblyFactory;
import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;
import org.apache.tuscany.model.assembly.sdo.OverrideOptions;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class AssemblyPackageImpl extends EPackageImpl implements AssemblyPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass bindingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass componentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass componentTypeEClass = null;

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
    private EClass entryPointEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass externalServiceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass implementationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass interfaceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass javaInterfaceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass moduleEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass moduleComponentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass moduleFragmentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass moduleWireEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass propertyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass propertyValuesEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass referenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass referenceValuesEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass scaBindingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass serviceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass subsystemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass systemWireEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass wsdlPortTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EEnum overrideOptionsEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType multiplicityEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType overrideOptionsObjectEDataType = null;

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
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyPackage#eNS_URI
     * @see #init()
     */
    private AssemblyPackageImpl() {
        super(eNS_URI, AssemblyFactory.eINSTANCE);
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
    public static AssemblyPackage init() {
        if (isInited) return (AssemblyPackage) EPackage.Registry.INSTANCE.getEPackage(AssemblyPackage.eNS_URI);

        // Obtain or create and register package
        AssemblyPackageImpl theAssemblyPackage = (AssemblyPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof AssemblyPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new AssemblyPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XMLTypePackageImpl.init();

        // Create package meta-data objects
        theAssemblyPackage.createPackageContents();

        // Initialize created meta-data
        theAssemblyPackage.initializePackageContents();

        // Register package validator
        EValidator.Registry.INSTANCE.put
                (theAssemblyPackage,
                        new EValidator.Descriptor() {
                            public EValidator getEValidator() {
                                return AssemblyValidator.INSTANCE;
                            }
                        });

        // Mark meta-data to indicate it can't be changed
        theAssemblyPackage.freeze();

        return theAssemblyPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getBinding() {
        return bindingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getBinding_Uri() {
        return (EAttribute) bindingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getComponent() {
        return componentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getComponent_Implementation() {
        return (EReference) componentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getComponent_Properties() {
        return (EReference) componentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getComponent_References() {
        return (EReference) componentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getComponent_Any() {
        return (EAttribute) componentEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getComponent_Name() {
        return (EAttribute) componentEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getComponent_AnyAttribute() {
        return (EAttribute) componentEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getComponentType() {
        return componentTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getComponentType_Services() {
        return (EReference) componentTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getComponentType_References() {
        return (EReference) componentTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getComponentType_Properties() {
        return (EReference) componentTypeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getComponentType_Any() {
        return (EAttribute) componentTypeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getComponentType_AnyAttribute() {
        return (EAttribute) componentTypeEClass.getEStructuralFeatures().get(4);
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
    public EReference getDocumentRoot_Binding() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_BindingSca() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_ComponentType() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Implementation() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Interface() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_InterfaceJava() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_InterfaceWsdl() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Module() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_ModuleFragment() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Source() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_SourceEpr() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_SourceUri() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(14);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Subsystem() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(15);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_Target() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(16);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getDocumentRoot_TargetEpr() {
        return (EReference) documentRootEClass.getEStructuralFeatures().get(17);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getDocumentRoot_TargetUri() {
        return (EAttribute) documentRootEClass.getEStructuralFeatures().get(18);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getEntryPoint() {
        return entryPointEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEntryPoint_InterfaceGroup() {
        return (EAttribute) entryPointEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEntryPoint_Interface() {
        return (EReference) entryPointEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEntryPoint_BindingGroup() {
        return (EAttribute) entryPointEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEntryPoint_Binding() {
        return (EReference) entryPointEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getEntryPoint_References() {
        return (EReference) entryPointEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEntryPoint_Any() {
        return (EAttribute) entryPointEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEntryPoint_Multiplicity() {
        return (EAttribute) entryPointEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEntryPoint_Name() {
        return (EAttribute) entryPointEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getEntryPoint_AnyAttribute() {
        return (EAttribute) entryPointEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getExternalService() {
        return externalServiceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getExternalService_InterfaceGroup() {
        return (EAttribute) externalServiceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getExternalService_Interface() {
        return (EReference) externalServiceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getExternalService_BindingsGroup() {
        return (EAttribute) externalServiceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getExternalService_Bindings() {
        return (EReference) externalServiceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getExternalService_Any() {
        return (EAttribute) externalServiceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getExternalService_Name() {
        return (EAttribute) externalServiceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getExternalService_Overridable() {
        return (EAttribute) externalServiceEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getExternalService_AnyAttribute() {
        return (EAttribute) externalServiceEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getImplementation() {
        return implementationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getInterface() {
        return interfaceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getJavaInterface() {
        return javaInterfaceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getJavaInterface_Any() {
        return (EAttribute) javaInterfaceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getJavaInterface_CallbackInterface() {
        return (EAttribute) javaInterfaceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getJavaInterface_Interface() {
        return (EAttribute) javaInterfaceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getJavaInterface_AnyAttribute() {
        return (EAttribute) javaInterfaceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getModule() {
        return moduleEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getModuleComponent() {
        return moduleComponentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModuleComponent_Properties() {
        return (EReference) moduleComponentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModuleComponent_References() {
        return (EReference) moduleComponentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleComponent_Any() {
        return (EAttribute) moduleComponentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleComponent_Module() {
        return (EAttribute) moduleComponentEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleComponent_Name() {
        return (EAttribute) moduleComponentEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleComponent_Uri() {
        return (EAttribute) moduleComponentEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleComponent_AnyAttribute() {
        return (EAttribute) moduleComponentEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getModuleFragment() {
        return moduleFragmentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModuleFragment_EntryPoints() {
        return (EReference) moduleFragmentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModuleFragment_Components() {
        return (EReference) moduleFragmentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModuleFragment_ExternalServices() {
        return (EReference) moduleFragmentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getModuleFragment_Wires() {
        return (EReference) moduleFragmentEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleFragment_Any() {
        return (EAttribute) moduleFragmentEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleFragment_Name() {
        return (EAttribute) moduleFragmentEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleFragment_AnyAttribute() {
        return (EAttribute) moduleFragmentEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getModuleWire() {
        return moduleWireEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleWire_SourceUri() {
        return (EAttribute) moduleWireEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleWire_TargetUri() {
        return (EAttribute) moduleWireEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleWire_Any() {
        return (EAttribute) moduleWireEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getModuleWire_AnyAttribute() {
        return (EAttribute) moduleWireEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getProperty() {
        return propertyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_Any() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_Default() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_Many() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_Name() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_Required() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_PropertyType() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getProperty_AnyAttribute() {
        return (EAttribute) propertyEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getPropertyValues() {
        return propertyValuesEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getPropertyValues_Any() {
        return (EAttribute) propertyValuesEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getPropertyValues_AnyAttribute() {
        return (EAttribute) propertyValuesEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getReference() {
        return referenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReference_InterfaceGroup() {
        return (EAttribute) referenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getReference_Interface() {
        return (EReference) referenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReference_Any() {
        return (EAttribute) referenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReference_Multiplicity() {
        return (EAttribute) referenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReference_Name() {
        return (EAttribute) referenceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReference_AnyAttribute() {
        return (EAttribute) referenceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getReferenceValues() {
        return referenceValuesEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReferenceValues_Any() {
        return (EAttribute) referenceValuesEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getReferenceValues_AnyAttribute() {
        return (EAttribute) referenceValuesEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getSCABinding() {
        return scaBindingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSCABinding_Any() {
        return (EAttribute) scaBindingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSCABinding_AnyAttribute() {
        return (EAttribute) scaBindingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getService() {
        return serviceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getService_InterfaceGroup() {
        return (EAttribute) serviceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getService_Interface() {
        return (EReference) serviceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getService_Any() {
        return (EAttribute) serviceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getService_Name() {
        return (EAttribute) serviceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getService_AnyAttribute() {
        return (EAttribute) serviceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getSubsystem() {
        return subsystemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getSubsystem_EntryPoints() {
        return (EReference) subsystemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getSubsystem_ModuleComponents() {
        return (EReference) subsystemEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getSubsystem_ExternalServices() {
        return (EReference) subsystemEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getSubsystem_Wires() {
        return (EReference) subsystemEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSubsystem_Any() {
        return (EAttribute) subsystemEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSubsystem_Name() {
        return (EAttribute) subsystemEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSubsystem_Uri() {
        return (EAttribute) subsystemEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSubsystem_AnyAttribute() {
        return (EAttribute) subsystemEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getSystemWire() {
        return systemWireEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSystemWire_SourceGroup() {
        return (EAttribute) systemWireEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getSystemWire_Source() {
        return (EReference) systemWireEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSystemWire_TargetGroup() {
        return (EAttribute) systemWireEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getSystemWire_Target() {
        return (EReference) systemWireEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getSystemWire_Any() {
        return (EAttribute) systemWireEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getWSDLPortType() {
        return wsdlPortTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getWSDLPortType_Any() {
        return (EAttribute) wsdlPortTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getWSDLPortType_CallbackInterface() {
        return (EAttribute) wsdlPortTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getWSDLPortType_Interface() {
        return (EAttribute) wsdlPortTypeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getWSDLPortType_AnyAttribute() {
        return (EAttribute) wsdlPortTypeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EEnum getOverrideOptions() {
        return overrideOptionsEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getMultiplicity() {
        return multiplicityEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getOverrideOptionsObject() {
        return overrideOptionsObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AssemblyFactory getAssemblyFactory() {
        return (AssemblyFactory) getEFactoryInstance();
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
        bindingEClass = createEClass(BINDING);
        createEAttribute(bindingEClass, BINDING__URI);

        componentEClass = createEClass(COMPONENT);
        createEReference(componentEClass, COMPONENT__IMPLEMENTATION);
        createEReference(componentEClass, COMPONENT__PROPERTIES);
        createEReference(componentEClass, COMPONENT__REFERENCES);
        createEAttribute(componentEClass, COMPONENT__ANY);
        createEAttribute(componentEClass, COMPONENT__NAME);
        createEAttribute(componentEClass, COMPONENT__ANY_ATTRIBUTE);

        componentTypeEClass = createEClass(COMPONENT_TYPE);
        createEReference(componentTypeEClass, COMPONENT_TYPE__SERVICES);
        createEReference(componentTypeEClass, COMPONENT_TYPE__REFERENCES);
        createEReference(componentTypeEClass, COMPONENT_TYPE__PROPERTIES);
        createEAttribute(componentTypeEClass, COMPONENT_TYPE__ANY);
        createEAttribute(componentTypeEClass, COMPONENT_TYPE__ANY_ATTRIBUTE);

        documentRootEClass = createEClass(DOCUMENT_ROOT);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
        createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
        createEReference(documentRootEClass, DOCUMENT_ROOT__BINDING);
        createEReference(documentRootEClass, DOCUMENT_ROOT__BINDING_SCA);
        createEReference(documentRootEClass, DOCUMENT_ROOT__COMPONENT_TYPE);
        createEReference(documentRootEClass, DOCUMENT_ROOT__IMPLEMENTATION);
        createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE);
        createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE_JAVA);
        createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE_WSDL);
        createEReference(documentRootEClass, DOCUMENT_ROOT__MODULE);
        createEReference(documentRootEClass, DOCUMENT_ROOT__MODULE_FRAGMENT);
        createEReference(documentRootEClass, DOCUMENT_ROOT__SOURCE);
        createEReference(documentRootEClass, DOCUMENT_ROOT__SOURCE_EPR);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__SOURCE_URI);
        createEReference(documentRootEClass, DOCUMENT_ROOT__SUBSYSTEM);
        createEReference(documentRootEClass, DOCUMENT_ROOT__TARGET);
        createEReference(documentRootEClass, DOCUMENT_ROOT__TARGET_EPR);
        createEAttribute(documentRootEClass, DOCUMENT_ROOT__TARGET_URI);

        entryPointEClass = createEClass(ENTRY_POINT);
        createEAttribute(entryPointEClass, ENTRY_POINT__INTERFACE_GROUP);
        createEReference(entryPointEClass, ENTRY_POINT__INTERFACE);
        createEAttribute(entryPointEClass, ENTRY_POINT__BINDING_GROUP);
        createEReference(entryPointEClass, ENTRY_POINT__BINDING);
        createEReference(entryPointEClass, ENTRY_POINT__REFERENCES);
        createEAttribute(entryPointEClass, ENTRY_POINT__ANY);
        createEAttribute(entryPointEClass, ENTRY_POINT__MULTIPLICITY);
        createEAttribute(entryPointEClass, ENTRY_POINT__NAME);
        createEAttribute(entryPointEClass, ENTRY_POINT__ANY_ATTRIBUTE);

        externalServiceEClass = createEClass(EXTERNAL_SERVICE);
        createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__INTERFACE_GROUP);
        createEReference(externalServiceEClass, EXTERNAL_SERVICE__INTERFACE);
        createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__BINDINGS_GROUP);
        createEReference(externalServiceEClass, EXTERNAL_SERVICE__BINDINGS);
        createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__ANY);
        createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__NAME);
        createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__OVERRIDABLE);
        createEAttribute(externalServiceEClass, EXTERNAL_SERVICE__ANY_ATTRIBUTE);

        implementationEClass = createEClass(IMPLEMENTATION);

        interfaceEClass = createEClass(INTERFACE);

        javaInterfaceEClass = createEClass(JAVA_INTERFACE);
        createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__ANY);
        createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__CALLBACK_INTERFACE);
        createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__INTERFACE);
        createEAttribute(javaInterfaceEClass, JAVA_INTERFACE__ANY_ATTRIBUTE);

        moduleEClass = createEClass(MODULE);

        moduleComponentEClass = createEClass(MODULE_COMPONENT);
        createEReference(moduleComponentEClass, MODULE_COMPONENT__PROPERTIES);
        createEReference(moduleComponentEClass, MODULE_COMPONENT__REFERENCES);
        createEAttribute(moduleComponentEClass, MODULE_COMPONENT__ANY);
        createEAttribute(moduleComponentEClass, MODULE_COMPONENT__MODULE);
        createEAttribute(moduleComponentEClass, MODULE_COMPONENT__NAME);
        createEAttribute(moduleComponentEClass, MODULE_COMPONENT__URI);
        createEAttribute(moduleComponentEClass, MODULE_COMPONENT__ANY_ATTRIBUTE);

        moduleFragmentEClass = createEClass(MODULE_FRAGMENT);
        createEReference(moduleFragmentEClass, MODULE_FRAGMENT__ENTRY_POINTS);
        createEReference(moduleFragmentEClass, MODULE_FRAGMENT__COMPONENTS);
        createEReference(moduleFragmentEClass, MODULE_FRAGMENT__EXTERNAL_SERVICES);
        createEReference(moduleFragmentEClass, MODULE_FRAGMENT__WIRES);
        createEAttribute(moduleFragmentEClass, MODULE_FRAGMENT__ANY);
        createEAttribute(moduleFragmentEClass, MODULE_FRAGMENT__NAME);
        createEAttribute(moduleFragmentEClass, MODULE_FRAGMENT__ANY_ATTRIBUTE);

        moduleWireEClass = createEClass(MODULE_WIRE);
        createEAttribute(moduleWireEClass, MODULE_WIRE__SOURCE_URI);
        createEAttribute(moduleWireEClass, MODULE_WIRE__TARGET_URI);
        createEAttribute(moduleWireEClass, MODULE_WIRE__ANY);
        createEAttribute(moduleWireEClass, MODULE_WIRE__ANY_ATTRIBUTE);

        propertyEClass = createEClass(PROPERTY);
        createEAttribute(propertyEClass, PROPERTY__ANY);
        createEAttribute(propertyEClass, PROPERTY__DEFAULT);
        createEAttribute(propertyEClass, PROPERTY__MANY);
        createEAttribute(propertyEClass, PROPERTY__NAME);
        createEAttribute(propertyEClass, PROPERTY__REQUIRED);
        createEAttribute(propertyEClass, PROPERTY__TYPE);
        createEAttribute(propertyEClass, PROPERTY__ANY_ATTRIBUTE);

        propertyValuesEClass = createEClass(PROPERTY_VALUES);
        createEAttribute(propertyValuesEClass, PROPERTY_VALUES__ANY);
        createEAttribute(propertyValuesEClass, PROPERTY_VALUES__ANY_ATTRIBUTE);

        referenceEClass = createEClass(REFERENCE);
        createEAttribute(referenceEClass, REFERENCE__INTERFACE_GROUP);
        createEReference(referenceEClass, REFERENCE__INTERFACE);
        createEAttribute(referenceEClass, REFERENCE__ANY);
        createEAttribute(referenceEClass, REFERENCE__MULTIPLICITY);
        createEAttribute(referenceEClass, REFERENCE__NAME);
        createEAttribute(referenceEClass, REFERENCE__ANY_ATTRIBUTE);

        referenceValuesEClass = createEClass(REFERENCE_VALUES);
        createEAttribute(referenceValuesEClass, REFERENCE_VALUES__ANY);
        createEAttribute(referenceValuesEClass, REFERENCE_VALUES__ANY_ATTRIBUTE);

        scaBindingEClass = createEClass(SCA_BINDING);
        createEAttribute(scaBindingEClass, SCA_BINDING__ANY);
        createEAttribute(scaBindingEClass, SCA_BINDING__ANY_ATTRIBUTE);

        serviceEClass = createEClass(SERVICE);
        createEAttribute(serviceEClass, SERVICE__INTERFACE_GROUP);
        createEReference(serviceEClass, SERVICE__INTERFACE);
        createEAttribute(serviceEClass, SERVICE__ANY);
        createEAttribute(serviceEClass, SERVICE__NAME);
        createEAttribute(serviceEClass, SERVICE__ANY_ATTRIBUTE);

        subsystemEClass = createEClass(SUBSYSTEM);
        createEReference(subsystemEClass, SUBSYSTEM__ENTRY_POINTS);
        createEReference(subsystemEClass, SUBSYSTEM__MODULE_COMPONENTS);
        createEReference(subsystemEClass, SUBSYSTEM__EXTERNAL_SERVICES);
        createEReference(subsystemEClass, SUBSYSTEM__WIRES);
        createEAttribute(subsystemEClass, SUBSYSTEM__ANY);
        createEAttribute(subsystemEClass, SUBSYSTEM__NAME);
        createEAttribute(subsystemEClass, SUBSYSTEM__URI);
        createEAttribute(subsystemEClass, SUBSYSTEM__ANY_ATTRIBUTE);

        systemWireEClass = createEClass(SYSTEM_WIRE);
        createEAttribute(systemWireEClass, SYSTEM_WIRE__SOURCE_GROUP);
        createEReference(systemWireEClass, SYSTEM_WIRE__SOURCE);
        createEAttribute(systemWireEClass, SYSTEM_WIRE__TARGET_GROUP);
        createEReference(systemWireEClass, SYSTEM_WIRE__TARGET);
        createEAttribute(systemWireEClass, SYSTEM_WIRE__ANY);

        wsdlPortTypeEClass = createEClass(WSDL_PORT_TYPE);
        createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__ANY);
        createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__CALLBACK_INTERFACE);
        createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__INTERFACE);
        createEAttribute(wsdlPortTypeEClass, WSDL_PORT_TYPE__ANY_ATTRIBUTE);

        // Create enums
        overrideOptionsEEnum = createEEnum(OVERRIDE_OPTIONS);

        // Create data types
        multiplicityEDataType = createEDataType(MULTIPLICITY);
        overrideOptionsObjectEDataType = createEDataType(OVERRIDE_OPTIONS_OBJECT);
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
        javaInterfaceEClass.getESuperTypes().add(this.getInterface());
        moduleEClass.getESuperTypes().add(this.getModuleFragment());
        scaBindingEClass.getESuperTypes().add(this.getBinding());
        wsdlPortTypeEClass.getESuperTypes().add(this.getInterface());

        // Initialize classes and features; add operations and parameters
        initEClass(bindingEClass, Binding.class, "Binding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getBinding_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(componentEClass, Component.class, "Component", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getComponent_Implementation(), this.getImplementation(), null, "implementation", null, 1, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComponent_Properties(), this.getPropertyValues(), null, "properties", null, 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComponent_References(), this.getReferenceValues(), null, "references", null, 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getComponent_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getComponent_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getComponent_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(componentTypeEClass, ComponentType.class, "ComponentType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getComponentType_Services(), this.getService(), null, "services", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComponentType_References(), this.getReference(), null, "references", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComponentType_Properties(), this.getProperty(), null, "properties", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getComponentType_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getComponentType_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ComponentType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Binding(), this.getBinding(), null, "binding", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_BindingSca(), this.getSCABinding(), null, "bindingSca", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_ComponentType(), this.getComponentType(), null, "componentType", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Implementation(), this.getImplementation(), null, "implementation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Interface(), this.getInterface(), null, "interface", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_InterfaceJava(), this.getJavaInterface(), null, "interfaceJava", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_InterfaceWsdl(), this.getWSDLPortType(), null, "interfaceWsdl", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Module(), this.getModule(), null, "module", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_ModuleFragment(), this.getModuleFragment(), null, "moduleFragment", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Source(), ecorePackage.getEObject(), null, "source", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_SourceEpr(), ecorePackage.getEObject(), null, "sourceEpr", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_SourceUri(), theXMLTypePackage.getAnyURI(), "sourceUri", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Subsystem(), this.getSubsystem(), null, "subsystem", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_Target(), ecorePackage.getEObject(), null, "target", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getDocumentRoot_TargetEpr(), ecorePackage.getEObject(), null, "targetEpr", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getDocumentRoot_TargetUri(), theXMLTypePackage.getAnyURI(), "targetUri", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED);

        initEClass(entryPointEClass, EntryPoint.class, "EntryPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEntryPoint_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 0, 1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEntryPoint_Interface(), this.getInterface(), null, "interface", null, 0, 1, EntryPoint.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getEntryPoint_BindingGroup(), ecorePackage.getEFeatureMapEntry(), "bindingGroup", null, 1, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEntryPoint_Binding(), this.getBinding(), null, "binding", null, 1, -1, EntryPoint.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEReference(getEntryPoint_References(), ecorePackage.getEObject(), null, "references", null, 1, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEntryPoint_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEntryPoint_Multiplicity(), this.getMultiplicity(), "multiplicity", "1..1", 0, 1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEntryPoint_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEntryPoint_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, EntryPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(externalServiceEClass, ExternalService.class, "ExternalService", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getExternalService_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 1, 1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getExternalService_Interface(), this.getInterface(), null, "interface", null, 1, 1, ExternalService.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getExternalService_BindingsGroup(), ecorePackage.getEFeatureMapEntry(), "bindingsGroup", null, 0, -1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getExternalService_Bindings(), this.getBinding(), null, "bindings", null, 0, -1, ExternalService.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getExternalService_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getExternalService_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getExternalService_Overridable(), this.getOverrideOptions(), "overridable", "may", 0, 1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getExternalService_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ExternalService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(implementationEClass, Implementation.class, "Implementation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(interfaceEClass, Interface.class, "Interface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(javaInterfaceEClass, JavaInterface.class, "JavaInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getJavaInterface_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getJavaInterface_CallbackInterface(), theXMLTypePackage.getNCName(), "callbackInterface", null, 0, 1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getJavaInterface_Interface(), theXMLTypePackage.getNCName(), "interface", null, 1, 1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getJavaInterface_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, JavaInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(moduleEClass, Module.class, "Module", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(moduleComponentEClass, ModuleComponent.class, "ModuleComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getModuleComponent_Properties(), this.getPropertyValues(), null, "properties", null, 0, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModuleComponent_References(), this.getReferenceValues(), null, "references", null, 0, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleComponent_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleComponent_Module(), theXMLTypePackage.getNCName(), "module", null, 1, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleComponent_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleComponent_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleComponent_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModuleComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(moduleFragmentEClass, ModuleFragment.class, "ModuleFragment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getModuleFragment_EntryPoints(), this.getEntryPoint(), null, "entryPoints", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModuleFragment_Components(), this.getComponent(), null, "components", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModuleFragment_ExternalServices(), this.getExternalService(), null, "externalServices", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModuleFragment_Wires(), this.getModuleWire(), null, "wires", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleFragment_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleFragment_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleFragment_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModuleFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(moduleWireEClass, ModuleWire.class, "ModuleWire", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getModuleWire_SourceUri(), theXMLTypePackage.getAnyURI(), "sourceUri", null, 1, 1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleWire_TargetUri(), theXMLTypePackage.getAnyURI(), "targetUri", null, 1, 1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleWire_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModuleWire_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ModuleWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(propertyEClass, Property.class, "Property", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getProperty_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getProperty_Default(), theXMLTypePackage.getString(), "default", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getProperty_Many(), theXMLTypePackage.getBoolean(), "many", "false", 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getProperty_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getProperty_Required(), theXMLTypePackage.getBoolean(), "required", "false", 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getProperty_PropertyType(), theXMLTypePackage.getQName(), "propertyType", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getProperty_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(propertyValuesEClass, PropertyValues.class, "PropertyValues", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getPropertyValues_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, PropertyValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getPropertyValues_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, PropertyValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(referenceEClass, Reference.class, "Reference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getReference_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getReference_Interface(), this.getInterface(), null, "interface", null, 1, 1, Reference.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getReference_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getReference_Multiplicity(), this.getMultiplicity(), "multiplicity", "1..1", 0, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getReference_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getReference_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(referenceValuesEClass, ReferenceValues.class, "ReferenceValues", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getReferenceValues_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, ReferenceValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getReferenceValues_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, ReferenceValues.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(scaBindingEClass, SCABinding.class, "SCABinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getSCABinding_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, SCABinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSCABinding_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, SCABinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(serviceEClass, Service.class, "Service", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getService_InterfaceGroup(), ecorePackage.getEFeatureMapEntry(), "interfaceGroup", null, 1, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_Interface(), this.getInterface(), null, "interface", null, 1, 1, Service.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(subsystemEClass, Subsystem.class, "Subsystem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getSubsystem_EntryPoints(), this.getEntryPoint(), null, "entryPoints", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSubsystem_ModuleComponents(), this.getModuleComponent(), null, "moduleComponents", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSubsystem_ExternalServices(), this.getExternalService(), null, "externalServices", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSubsystem_Wires(), this.getSystemWire(), null, "wires", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSubsystem_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSubsystem_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSubsystem_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSubsystem_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, Subsystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(systemWireEClass, SystemWire.class, "SystemWire", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getSystemWire_SourceGroup(), ecorePackage.getEFeatureMapEntry(), "sourceGroup", null, 1, 1, SystemWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSystemWire_Source(), ecorePackage.getEObject(), null, "source", null, 1, 1, SystemWire.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getSystemWire_TargetGroup(), ecorePackage.getEFeatureMapEntry(), "targetGroup", null, 1, 1, SystemWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSystemWire_Target(), ecorePackage.getEObject(), null, "target", null, 1, 1, SystemWire.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
        initEAttribute(getSystemWire_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, SystemWire.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsdlPortTypeEClass, WSDLPortType.class, "WSDLPortType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getWSDLPortType_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 0, -1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getWSDLPortType_CallbackInterface(), theXMLTypePackage.getAnyURI(), "callbackInterface", null, 0, 1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getWSDLPortType_Interface(), theXMLTypePackage.getAnyURI(), "interface", null, 1, 1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getWSDLPortType_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1, WSDLPortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(overrideOptionsEEnum, OverrideOptions.class, "OverrideOptions");
        addEEnumLiteral(overrideOptionsEEnum, OverrideOptions.NO_LITERAL);
        addEEnumLiteral(overrideOptionsEEnum, OverrideOptions.MAY_LITERAL);
        addEEnumLiteral(overrideOptionsEEnum, OverrideOptions.MUST_LITERAL);

        // Initialize data types
        initEDataType(multiplicityEDataType, String.class, "Multiplicity", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(overrideOptionsObjectEDataType, OverrideOptions.class, "OverrideOptionsObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);

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
     *
     * @generated
     */
    protected void createExtendedMetaDataAnnotations() {
        String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
        addAnnotation
                (bindingEClass,
                        source,
                        new String[]{
                                "name", "Binding",
                                "kind", "empty"
                        });
        addAnnotation
                (getBinding_Uri(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "uri"
                        });
        addAnnotation
                (componentEClass,
                        source,
                        new String[]{
                                "name", "Component",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getComponent_Implementation(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "implementation",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getComponent_Properties(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "properties",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getComponent_References(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "references",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getComponent_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":3",
                                "processing", "lax"
                        });
        addAnnotation
                (getComponent_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getComponent_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":5",
                                "processing", "lax"
                        });
        addAnnotation
                (componentTypeEClass,
                        source,
                        new String[]{
                                "name", "ComponentType",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getComponentType_Services(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "service",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getComponentType_References(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "reference",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getComponentType_Properties(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "property",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getComponentType_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":3",
                                "processing", "lax"
                        });
        addAnnotation
                (getComponentType_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":4",
                                "processing", "lax"
                        });
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
                (getDocumentRoot_Binding(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "binding",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_BindingSca(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "binding.sca",
                                "namespace", "##targetNamespace",
                                "affiliation", "binding"
                        });
        addAnnotation
                (getDocumentRoot_ComponentType(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "componentType",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Implementation(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "implementation",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Interface(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_InterfaceJava(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface.java",
                                "namespace", "##targetNamespace",
                                "affiliation", "interface"
                        });
        addAnnotation
                (getDocumentRoot_InterfaceWsdl(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface.wsdl",
                                "namespace", "##targetNamespace",
                                "affiliation", "interface"
                        });
        addAnnotation
                (getDocumentRoot_Module(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "module",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_ModuleFragment(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "moduleFragment",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Source(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "source",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_SourceEpr(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "source.epr",
                                "namespace", "##targetNamespace",
                                "affiliation", "source"
                        });
        addAnnotation
                (getDocumentRoot_SourceUri(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "source.uri",
                                "namespace", "##targetNamespace",
                                "affiliation", "source"
                        });
        addAnnotation
                (getDocumentRoot_Subsystem(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "subsystem",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_Target(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "target",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getDocumentRoot_TargetEpr(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "target.epr",
                                "namespace", "##targetNamespace",
                                "affiliation", "target"
                        });
        addAnnotation
                (getDocumentRoot_TargetUri(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "target.uri",
                                "namespace", "##targetNamespace",
                                "affiliation", "target"
                        });
        addAnnotation
                (entryPointEClass,
                        source,
                        new String[]{
                                "name", "EntryPoint",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getEntryPoint_InterfaceGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "interface:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEntryPoint_Interface(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface",
                                "namespace", "##targetNamespace",
                                "group", "interface:group"
                        });
        addAnnotation
                (getEntryPoint_BindingGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "binding:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEntryPoint_Binding(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "binding",
                                "namespace", "##targetNamespace",
                                "group", "binding:group"
                        });
        addAnnotation
                (getEntryPoint_References(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "reference",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getEntryPoint_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":5",
                                "processing", "lax"
                        });
        addAnnotation
                (getEntryPoint_Multiplicity(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "multiplicity"
                        });
        addAnnotation
                (getEntryPoint_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getEntryPoint_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":8",
                                "processing", "lax"
                        });
        addAnnotation
                (externalServiceEClass,
                        source,
                        new String[]{
                                "name", "ExternalService",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getExternalService_InterfaceGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "interface:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getExternalService_Interface(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface",
                                "namespace", "##targetNamespace",
                                "group", "interface:group"
                        });
        addAnnotation
                (getExternalService_BindingsGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "binding:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getExternalService_Bindings(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "binding",
                                "namespace", "##targetNamespace",
                                "group", "binding:group"
                        });
        addAnnotation
                (getExternalService_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (getExternalService_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getExternalService_Overridable(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "overridable"
                        });
        addAnnotation
                (getExternalService_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":7",
                                "processing", "lax"
                        });
        addAnnotation
                (implementationEClass,
                        source,
                        new String[]{
                                "name", "Implementation",
                                "kind", "empty"
                        });
        addAnnotation
                (interfaceEClass,
                        source,
                        new String[]{
                                "name", "Interface",
                                "kind", "empty"
                        });
        addAnnotation
                (javaInterfaceEClass,
                        source,
                        new String[]{
                                "name", "JavaInterface",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getJavaInterface_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getJavaInterface_CallbackInterface(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "callbackInterface"
                        });
        addAnnotation
                (getJavaInterface_Interface(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "interface"
                        });
        addAnnotation
                (getJavaInterface_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":3",
                                "processing", "lax"
                        });
        addAnnotation
                (moduleEClass,
                        source,
                        new String[]{
                                "name", "Module",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (moduleComponentEClass,
                        source,
                        new String[]{
                                "name", "ModuleComponent",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getModuleComponent_Properties(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "properties",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleComponent_References(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "references",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleComponent_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (getModuleComponent_Module(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "module"
                        });
        addAnnotation
                (getModuleComponent_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getModuleComponent_Uri(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "uri"
                        });
        addAnnotation
                (getModuleComponent_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":6",
                                "processing", "lax"
                        });
        addAnnotation
                (moduleFragmentEClass,
                        source,
                        new String[]{
                                "name", "ModuleFragment",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getModuleFragment_EntryPoints(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "entryPoint",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleFragment_Components(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "component",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleFragment_ExternalServices(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "externalService",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleFragment_Wires(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "wire",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleFragment_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (getModuleFragment_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getModuleFragment_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":6",
                                "processing", "lax"
                        });
        addAnnotation
                (moduleWireEClass,
                        source,
                        new String[]{
                                "name", "ModuleWire",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getModuleWire_SourceUri(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "source.uri",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleWire_TargetUri(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "target.uri",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getModuleWire_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (getModuleWire_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":3",
                                "processing", "lax"
                        });
        addAnnotation
                (multiplicityEDataType,
                        source,
                        new String[]{
                                "name", "Multiplicity",
                                "baseType", "http://www.eclipse.org/emf/2003/XMLType#string",
                                "enumeration", "0..1 1..1 0..n 1..n"
                        });
        addAnnotation
                (overrideOptionsEEnum,
                        source,
                        new String[]{
                                "name", "OverrideOptions"
                        });
        addAnnotation
                (overrideOptionsObjectEDataType,
                        source,
                        new String[]{
                                "name", "OverrideOptions:Object",
                                "baseType", "OverrideOptions"
                        });
        addAnnotation
                (propertyEClass,
                        source,
                        new String[]{
                                "name", "Property",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getProperty_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getProperty_Default(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "default"
                        });
        addAnnotation
                (getProperty_Many(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "many"
                        });
        addAnnotation
                (getProperty_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getProperty_Required(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "required"
                        });
        addAnnotation
                (getProperty_PropertyType(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "type"
                        });
        addAnnotation
                (getProperty_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":6",
                                "processing", "lax"
                        });
        addAnnotation
                (propertyValuesEClass,
                        source,
                        new String[]{
                                "name", "PropertyValues",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getPropertyValues_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getPropertyValues_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (referenceEClass,
                        source,
                        new String[]{
                                "name", "Reference",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getReference_InterfaceGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "interface:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getReference_Interface(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface",
                                "namespace", "##targetNamespace",
                                "group", "interface:group"
                        });
        addAnnotation
                (getReference_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (getReference_Multiplicity(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "multiplicity"
                        });
        addAnnotation
                (getReference_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getReference_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":5",
                                "processing", "lax"
                        });
        addAnnotation
                (referenceValuesEClass,
                        source,
                        new String[]{
                                "name", "ReferenceValues",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getReferenceValues_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getReferenceValues_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (scaBindingEClass,
                        source,
                        new String[]{
                                "name", "SCABinding",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getSCABinding_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":1",
                                "processing", "lax"
                        });
        addAnnotation
                (getSCABinding_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (serviceEClass,
                        source,
                        new String[]{
                                "name", "Service",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getService_InterfaceGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "interface:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getService_Interface(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "interface",
                                "namespace", "##targetNamespace",
                                "group", "interface:group"
                        });
        addAnnotation
                (getService_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":2",
                                "processing", "lax"
                        });
        addAnnotation
                (getService_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getService_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (subsystemEClass,
                        source,
                        new String[]{
                                "name", "Subsystem",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getSubsystem_EntryPoints(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "entryPoint",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getSubsystem_ModuleComponents(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "moduleComponent",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getSubsystem_ExternalServices(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "externalService",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getSubsystem_Wires(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "wire",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getSubsystem_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (getSubsystem_Name(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "name"
                        });
        addAnnotation
                (getSubsystem_Uri(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "uri"
                        });
        addAnnotation
                (getSubsystem_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":7",
                                "processing", "lax"
                        });
        addAnnotation
                (systemWireEClass,
                        source,
                        new String[]{
                                "name", "SystemWire",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getSystemWire_SourceGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "source:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getSystemWire_Source(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "source",
                                "namespace", "##targetNamespace",
                                "group", "source:group"
                        });
        addAnnotation
                (getSystemWire_TargetGroup(),
                        source,
                        new String[]{
                                "kind", "group",
                                "name", "target:group",
                                "namespace", "##targetNamespace"
                        });
        addAnnotation
                (getSystemWire_Target(),
                        source,
                        new String[]{
                                "kind", "element",
                                "name", "target",
                                "namespace", "##targetNamespace",
                                "group", "target:group"
                        });
        addAnnotation
                (getSystemWire_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":4",
                                "processing", "lax"
                        });
        addAnnotation
                (wsdlPortTypeEClass,
                        source,
                        new String[]{
                                "name", "WSDLPortType",
                                "kind", "elementOnly"
                        });
        addAnnotation
                (getWSDLPortType_Any(),
                        source,
                        new String[]{
                                "kind", "elementWildcard",
                                "wildcards", "##other",
                                "name", ":0",
                                "processing", "lax"
                        });
        addAnnotation
                (getWSDLPortType_CallbackInterface(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "callbackInterface"
                        });
        addAnnotation
                (getWSDLPortType_Interface(),
                        source,
                        new String[]{
                                "kind", "attribute",
                                "name", "interface"
                        });
        addAnnotation
                (getWSDLPortType_AnyAttribute(),
                        source,
                        new String[]{
                                "kind", "attributeWildcard",
                                "wildcards", "##any",
                                "name", ":3",
                                "processing", "lax"
                        });
    }

    /**
     * Custom code
     */

    private List mergedEClassifiers;

    class MergedEClassifier {
        private EClassifier eClassifier;
        private EPackage sourceEPackage;
        private int sourceClassifierID;
        private EClass sourceEClass;

        /**
         * Constructor
         *
         * @param sourceEPackage
         * @param eClassifier
         */
        MergedEClassifier(EPackage sourceEPackage, EClassifier eClassifier) {
            this.sourceEPackage = sourceEPackage;
            this.eClassifier = eClassifier;
            this.sourceClassifierID = eClassifier.getClassifierID();
            if (eClassifier instanceof EClass) {
                this.sourceEClass = EcoreFactory.eINSTANCE.createEClass();
                sourceEClass.setName(eClassifier.getName());
                ((EClassImpl) sourceEClass).setClassifierID(sourceClassifierID);
            }
        }

        /**
         * @return Returns the eClassifier.
         */
        EClassifier getEClassifier() {
            return eClassifier;
        }

        /**
         * @return Returns the originalClassifierID.
         */
        int getSourceClassifierID() {
            return sourceClassifierID;
        }

        /**
         * @return Returns the sourceEPackage.
         */
        EPackage getSourceEPackage() {
            return sourceEPackage;
        }

        /**
         * @return Returns the sourceEClass.
         */
        EClass getSourceEClass() {
            return sourceEClass;
        }
    }

    /**
     * @return Returns the mergedEClassifiers.
     */
    public List getMergedEClassifiers() {
        if (mergedEClassifiers == null) {
            mergedEClassifiers = new ArrayList();
            for (Iterator i = getEClassifiers().iterator(); i.hasNext();) {
                EClassifier eClassifier = (EClassifier) i.next();
                MergedEClassifier mergedEClassifier = new MergedEClassifier(this, eClassifier);
                mergedEClassifiers.add(mergedEClassifier);
            }
        }
        return mergedEClassifiers;
    }

    public void merge(EPackage sourceEPackage) {
        // Move the classifiers from the source package to this package
        List mergedEClassifiers = getMergedEClassifiers();
        List eClassifiers = getEClassifiers();
        int classifierID = eClassifiers.size();
        EClass sourceDocumentRoot = (EClass) sourceEPackage.getEClassifier("DocumentRoot");
        for (Iterator i = new ArrayList(sourceEPackage.getEClassifiers()).iterator(); i.hasNext();) {
            EClassifierImpl eClassifier = (EClassifierImpl) i.next();
            if (eClassifier != sourceDocumentRoot) {

                MergedEClassifier mergedEClassifier = new MergedEClassifier(sourceEPackage, eClassifier);
                mergedEClassifiers.add(mergedEClassifier);

                eClassifier.setClassifierID(classifierID);
                classifierID++;
                eClassifiers.add(eClassifier);
            }
        }

        // Move the features from the source document root to this document root
        List eStructuralFeatures = getDocumentRoot().getEStructuralFeatures();
        int featureID = eStructuralFeatures.size();
        for (Iterator i = new ArrayList(sourceDocumentRoot.getEStructuralFeatures()).iterator(); i.hasNext(); ) {
            EStructuralFeatureImpl eStructuralFeature=(EStructuralFeatureImpl)i.next();
            int id=eStructuralFeature.getFeatureID();
            if (id==DOCUMENT_ROOT__MIXED || id==DOCUMENT_ROOT__XMLNS_PREFIX_MAP || id==DOCUMENT_ROOT__XSI_SCHEMA_LOCATION)
                continue;
            eStructuralFeature.setFeatureID(featureID);
            featureID++;
            eStructuralFeatures.add(eStructuralFeature);
        }
	}
	
} //AssemblyPackageImpl
