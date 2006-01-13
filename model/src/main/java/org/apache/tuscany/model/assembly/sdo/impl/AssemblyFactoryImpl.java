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


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
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
import org.apache.tuscany.model.assembly.sdo.impl.AssemblyPackageImpl.MergedEClassifier;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class AssemblyFactoryImpl extends EFactoryImpl implements AssemblyFactory {
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AssemblyFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public EObject createGen(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case AssemblyPackage.BINDING:
            return (EObject) createBinding();
        case AssemblyPackage.COMPONENT:
            return (EObject) createComponent();
        case AssemblyPackage.COMPONENT_TYPE:
            return (EObject) createComponentType();
        case AssemblyPackage.DOCUMENT_ROOT:
            return (EObject) createDocumentRoot();
        case AssemblyPackage.ENTRY_POINT:
            return (EObject) createEntryPoint();
        case AssemblyPackage.EXTERNAL_SERVICE:
            return (EObject) createExternalService();
        case AssemblyPackage.IMPLEMENTATION:
            return (EObject) createImplementation();
        case AssemblyPackage.INTERFACE:
            return (EObject) createInterface();
        case AssemblyPackage.JAVA_INTERFACE:
            return (EObject) createJavaInterface();
        case AssemblyPackage.MODULE:
            return (EObject) createModule();
        case AssemblyPackage.MODULE_COMPONENT:
            return (EObject) createModuleComponent();
        case AssemblyPackage.MODULE_FRAGMENT:
            return (EObject) createModuleFragment();
        case AssemblyPackage.MODULE_WIRE:
            return (EObject) createModuleWire();
        case AssemblyPackage.PROPERTY:
            return (EObject) createProperty();
        case AssemblyPackage.PROPERTY_VALUES:
            return (EObject) createPropertyValues();
        case AssemblyPackage.REFERENCE:
            return (EObject) createReference();
        case AssemblyPackage.REFERENCE_VALUES:
            return (EObject) createReferenceValues();
        case AssemblyPackage.SCA_BINDING:
            return (EObject) createSCABinding();
        case AssemblyPackage.SERVICE:
            return (EObject) createService();
        case AssemblyPackage.SUBSYSTEM:
            return (EObject) createSubsystem();
        case AssemblyPackage.SYSTEM_WIRE:
            return (EObject) createSystemWire();
        case AssemblyPackage.WSDL_PORT_TYPE:
            return (EObject) createWSDLPortType();
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
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
        case AssemblyPackage.OVERRIDE_OPTIONS: {
            OverrideOptions result = OverrideOptions.get(initialValue);
            if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
            return result;
        }
        case AssemblyPackage.MULTIPLICITY:
            return createMultiplicityFromString(eDataType, initialValue);
        case AssemblyPackage.OVERRIDE_OPTIONS_OBJECT:
            return createOverrideOptionsObjectFromString(eDataType, initialValue);
        default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
        case AssemblyPackage.OVERRIDE_OPTIONS:
            return instanceValue == null ? null : instanceValue.toString();
        case AssemblyPackage.MULTIPLICITY:
            return convertMultiplicityToString(eDataType, instanceValue);
        case AssemblyPackage.OVERRIDE_OPTIONS_OBJECT:
            return convertOverrideOptionsObjectToString(eDataType, instanceValue);
        default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Binding createBinding() {
        BindingImpl binding = new BindingImpl();
        return binding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Component createComponentGen() {
        ComponentImpl component = new ComponentImpl();
        return component;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ComponentType createComponentTypeGen() {
        ComponentTypeImpl componentType = new ComponentTypeImpl();
        return componentType;
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
    public EntryPoint createEntryPointGen() {
        EntryPointImpl entryPoint = new EntryPointImpl();
        return entryPoint;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ExternalService createExternalServiceGen() {
        ExternalServiceImpl externalService = new ExternalServiceImpl();
        return externalService;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Implementation createImplementation() {
        ImplementationImpl implementation = new ImplementationImpl();
        return implementation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Interface createInterface() {
        InterfaceImpl interface_ = new InterfaceImpl();
        return interface_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public JavaInterface createJavaInterfaceGen() {
        JavaInterfaceImpl javaInterface = new JavaInterfaceImpl();
        return javaInterface;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Module createModuleGen() {
        ModuleImpl module = new ModuleImpl();
        return module;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModuleComponent createModuleComponentGen() {
        ModuleComponentImpl moduleComponent = new ModuleComponentImpl();
        return moduleComponent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModuleFragment createModuleFragmentGen() {
        ModuleFragmentElementImpl moduleFragment = new ModuleFragmentElementImpl();
        return moduleFragment;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ModuleWire createModuleWire() {
        ModuleWireImpl moduleWire = new ModuleWireImpl();
        return moduleWire;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Property createPropertyGen() {
        PropertyImpl property = new PropertyImpl();
        return property;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public PropertyValues createPropertyValues() {
        PropertyValuesImpl propertyValues = new PropertyValuesImpl();
        return propertyValues;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Reference createReferenceGen() {
        ReferenceImpl reference = new ReferenceImpl();
        return reference;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ReferenceValues createReferenceValues() {
        ReferenceValuesImpl referenceValues = new ReferenceValuesImpl();
        return referenceValues;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public SCABinding createSCABinding() {
        SCABindingImpl scaBinding = new SCABindingImpl();
        return scaBinding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Service createServiceGen() {
        ServiceImpl service = new ServiceImpl();
        return service;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public Subsystem createSubsystemGen() {
        SubsystemImpl subsystem = new SubsystemImpl();
        return subsystem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public SystemWire createSystemWire() {
        SystemWireImpl systemWire = new SystemWireImpl();
        return systemWire;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public WSDLPortType createWSDLPortTypeGen() {
        WSDLPortTypeImpl wsdlPortType = new WSDLPortTypeImpl();
        return wsdlPortType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String createMultiplicityFromString(EDataType eDataType, String initialValue) {
        return (String) XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.eINSTANCE.getString(), initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertMultiplicityToString(EDataType eDataType, Object instanceValue) {
        return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.eINSTANCE.getString(), instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public OverrideOptions createOverrideOptionsObjectFromString(EDataType eDataType, String initialValue) {
        return (OverrideOptions) AssemblyFactory.eINSTANCE.createFromString(AssemblyPackage.eINSTANCE.getOverrideOptions(), initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public String convertOverrideOptionsObjectToString(EDataType eDataType, Object instanceValue) {
        return AssemblyFactory.eINSTANCE.convertToString(AssemblyPackage.eINSTANCE.getOverrideOptions(), instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AssemblyPackage getAssemblyPackage() {
        return (AssemblyPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @deprecated
     */
    public static AssemblyPackage getPackage() {
        return AssemblyPackage.eINSTANCE;
    }

    /**
     * Custom code
     */

    private final org.apache.tuscany.model.assembly.AssemblyFactory logicalModelFactory = new org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl();

    /**
     * @see org.eclipse.emf.ecore.impl.EFactoryImpl#create(org.eclipse.emf.ecore.EClass)
     */
    public EObject create(EClass eClass) {
        int classifierID = eClass.getClassifierID();
        MergedEClassifier mergedEClassifier = (MergedEClassifier) ((AssemblyPackageImpl) ePackage).getMergedEClassifiers().get(classifierID);
        int sourceClassifierID = mergedEClassifier.getSourceClassifierID();
        if (classifierID == sourceClassifierID) {
            return createGen(eClass);
        } else {
            EFactory sourceFactory = mergedEClassifier.getSourceEPackage().getEFactoryInstance();
            EClass sourceEClass = mergedEClassifier.getSourceEClass();
            return sourceFactory.create(sourceEClass);
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createComponentType()
     */
    public ComponentType createComponentType() {
        return (ComponentType) logicalModelFactory.createComponentType();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createEntryPoint()
     */
    public EntryPoint createEntryPoint() {
        return (EntryPoint) logicalModelFactory.createEntryPoint();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createExternalService()
     */
    public ExternalService createExternalService() {
        return (ExternalService) logicalModelFactory.createExternalService();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createJavaInterface()
     */
    public JavaInterface createJavaInterface() {
        return (JavaInterface) logicalModelFactory.createJavaInterface();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createModule()
     */
    public Module createModule() {
        return (Module) logicalModelFactory.createModule();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createModuleComponent()
     */
    public ModuleComponent createModuleComponent() {
        return (ModuleComponent) logicalModelFactory.createModuleComponent();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createProperty()
     */
    public Property createProperty() {
        return (Property) logicalModelFactory.createProperty();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createReference()
     */
    public Reference createReference() {
        return (Reference) logicalModelFactory.createReference();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createService()
     */
    public Service createService() {
        return (Service) logicalModelFactory.createService();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createSubsystem()
     */
    public Subsystem createSubsystem() {
        return (Subsystem) logicalModelFactory.createSubsystem();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createWSDLPortType()
     */
    public WSDLPortType createWSDLPortType() {
        return (WSDLPortType) logicalModelFactory.createWSDLPortType();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createComponent()
     */
    public Component createComponent() {
        return (Component) logicalModelFactory.createSimpleComponent();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.AssemblyFactory#createModuleFragment()
     */
    public ModuleFragment createModuleFragment() {
        return (ModuleFragment)logicalModelFactory.createModuleFragment();
	}
	
} //AssemblyFactoryImpl
