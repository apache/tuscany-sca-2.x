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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;
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

import org.apache.tuscany.model.assembly.sdo.AssemblyPackage;
import org.apache.tuscany.model.assembly.sdo.OverrideOptions;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see org.apache.tuscany.model.assembly.sdo.AssemblyPackage
 */
public class AssemblyValidator extends EObjectValidator {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static final AssemblyValidator INSTANCE = new AssemblyValidator();

    /**
     * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.eclipse.emf.common.util.Diagnostic#getSource()
     * @see org.eclipse.emf.common.util.Diagnostic#getCode()
     */
    public static final String DIAGNOSTIC_SOURCE = "org.apache.tuscany.model.assembly.binding";

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

    /**
     * The cached base package validator.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected XMLTypeValidator xmlTypeValidator;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public AssemblyValidator() {
        super();
        xmlTypeValidator = XMLTypeValidator.INSTANCE;
    }

    /**
     * Returns the package of this validator switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected EPackage getEPackage() {
        return AssemblyPackage.eINSTANCE;
    }

    /**
     * Calls <code>validateXXX</code> for the corresonding classifier of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map context) {
        switch (classifierID) {
        case AssemblyPackage.BINDING:
            return validateBinding((Binding) value, diagnostics, context);
        case AssemblyPackage.COMPONENT:
            return validateComponent((Component) value, diagnostics, context);
        case AssemblyPackage.COMPONENT_TYPE:
            return validateComponentType((ComponentType) value, diagnostics, context);
        case AssemblyPackage.DOCUMENT_ROOT:
            return validateDocumentRoot((DocumentRoot) value, diagnostics, context);
        case AssemblyPackage.ENTRY_POINT:
            return validateEntryPoint((EntryPoint) value, diagnostics, context);
        case AssemblyPackage.EXTERNAL_SERVICE:
            return validateExternalService((ExternalService) value, diagnostics, context);
        case AssemblyPackage.IMPLEMENTATION:
            return validateImplementation((Implementation) value, diagnostics, context);
        case AssemblyPackage.INTERFACE:
            return validateInterface((Interface) value, diagnostics, context);
        case AssemblyPackage.JAVA_INTERFACE:
            return validateJavaInterface((JavaInterface) value, diagnostics, context);
        case AssemblyPackage.MODULE:
            return validateModule((Module) value, diagnostics, context);
        case AssemblyPackage.MODULE_COMPONENT:
            return validateModuleComponent((ModuleComponent) value, diagnostics, context);
        case AssemblyPackage.MODULE_FRAGMENT:
            return validateModuleFragment((ModuleFragment) value, diagnostics, context);
        case AssemblyPackage.MODULE_WIRE:
            return validateModuleWire((ModuleWire) value, diagnostics, context);
        case AssemblyPackage.PROPERTY:
            return validateProperty((Property) value, diagnostics, context);
        case AssemblyPackage.PROPERTY_VALUES:
            return validatePropertyValues((PropertyValues) value, diagnostics, context);
        case AssemblyPackage.REFERENCE:
            return validateReference((Reference) value, diagnostics, context);
        case AssemblyPackage.REFERENCE_VALUES:
            return validateReferenceValues((ReferenceValues) value, diagnostics, context);
        case AssemblyPackage.SCA_BINDING:
            return validateSCABinding((SCABinding) value, diagnostics, context);
        case AssemblyPackage.SERVICE:
            return validateService((Service) value, diagnostics, context);
        case AssemblyPackage.SUBSYSTEM:
            return validateSubsystem((Subsystem) value, diagnostics, context);
        case AssemblyPackage.SYSTEM_WIRE:
            return validateSystemWire((SystemWire) value, diagnostics, context);
        case AssemblyPackage.WSDL_PORT_TYPE:
            return validateWSDLPortType((WSDLPortType) value, diagnostics, context);
        case AssemblyPackage.OVERRIDE_OPTIONS:
            return validateOverrideOptions((Object) value, diagnostics, context);
        case AssemblyPackage.MULTIPLICITY:
            return validateMultiplicity((String) value, diagnostics, context);
        case AssemblyPackage.OVERRIDE_OPTIONS_OBJECT:
            return validateOverrideOptionsObject((OverrideOptions) value, diagnostics, context);
        default:
            return true;
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateBinding(Binding binding, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) binding, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateComponent(Component component, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) component, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateComponentType(ComponentType componentType, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) componentType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateDocumentRoot(DocumentRoot documentRoot, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) documentRoot, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateEntryPoint(EntryPoint entryPoint, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) entryPoint, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateExternalService(ExternalService externalService, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) externalService, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateImplementation(Implementation implementation, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) implementation, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateInterface(Interface interface_, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) interface_, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateJavaInterface(JavaInterface javaInterface, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) javaInterface, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateModule(Module module, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) module, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateModuleComponent(ModuleComponent moduleComponent, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) moduleComponent, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateModuleFragment(ModuleFragment moduleFragment, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) moduleFragment, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateModuleWire(ModuleWire moduleWire, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) moduleWire, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateProperty(Property property, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) property, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validatePropertyValues(PropertyValues propertyValues, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) propertyValues, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateReference(Reference reference, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) reference, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateReferenceValues(ReferenceValues referenceValues, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) referenceValues, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateSCABinding(SCABinding scaBinding, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) scaBinding, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateService(Service service, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) service, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateSubsystem(Subsystem subsystem, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) subsystem, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateSystemWire(SystemWire systemWire, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) systemWire, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateWSDLPortType(WSDLPortType wsdlPortType, DiagnosticChain diagnostics, Map context) {
        return validate_EveryDefaultConstraint((EObject) wsdlPortType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateOverrideOptions(Object overrideOptions, DiagnosticChain diagnostics, Map context) {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateMultiplicity(String multiplicity, DiagnosticChain diagnostics, Map context) {
        boolean result = validateMultiplicity_Enumeration(multiplicity, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see #validateMultiplicity_Enumeration
     */
    public static final Collection MULTIPLICITY__ENUMERATION__VALUES =
            wrapEnumerationValues
                    (new Object[]{
                            "0..1",
                            "1..1",
                            "0..n",
                            "1..n"
                    });

    /**
     * Validates the Enumeration constraint of '<em>Multiplicity</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean validateMultiplicity_Enumeration(String multiplicity, DiagnosticChain diagnostics, Map context) {
        boolean result = MULTIPLICITY__ENUMERATION__VALUES.contains(multiplicity);
        if (!result && diagnostics != null)
            reportEnumerationViolation(AssemblyPackage.eINSTANCE.getMultiplicity(), multiplicity, MULTIPLICITY__ENUMERATION__VALUES, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateOverrideOptionsObject(OverrideOptions overrideOptionsObject, DiagnosticChain diagnostics, Map context) {
		return true;
	}

} //AssemblyValidator
