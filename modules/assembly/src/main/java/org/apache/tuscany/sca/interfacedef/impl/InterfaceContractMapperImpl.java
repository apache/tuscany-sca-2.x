/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.interfacedef.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.ContractBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.Audit;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.policy.ExtensionType;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceContractMapperImpl implements InterfaceContractMapper {

    protected ExtensionPointRegistry registry;
    protected BuilderExtensionPoint builders;
    protected ContractBuilder contractBuilder;

    public InterfaceContractMapperImpl(ExtensionPointRegistry registry){
        this.registry = registry;
        this.builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
    }

    public boolean isCompatible(DataType source, DataType target, boolean passByValue) {
        return isCompatible(source, target, passByValue, null);   
    }

    public boolean isCompatible(DataType source, DataType target, boolean passByValue, Audit audit) {
        if (source == target) {
            return true;
        }
        if (!passByValue) {
            if (source == null || target == null) {
                if (audit != null){
                    audit.append("One of either the source or target data types is null for");
                }
                return false;
            }
            // For local case
            return target.getPhysical().isAssignableFrom(source.getPhysical());
        } else {
            // For remote interfaces where the target is represented with WSDL 
            // the source will have been converted to WSDL so we rely on JAXB mappings
            // being the same in both cases and just compare the type names directly. 
            // TODO - is this right?
            XMLType sourceLogicalType = null;

            // There is some nesting of data types (when GeneratedDataTypes or arrays are used) so
            // dig a bit deeper to find the real data type. Use a loop since for a multidimensional 
            // array, we might need to go more than one level deep.
            while (source.getLogical() instanceof DataType<?>) {
                source = (DataType<?>)source.getLogical();
            }            
            sourceLogicalType = (XMLType)source.getLogical();

            XMLType targetLogicalType = null;
            while (target.getLogical() instanceof DataType<?>) {
                target = (DataType<?>)target.getLogical();
            }            
            targetLogicalType = (XMLType)target.getLogical();

            // The logical type is null in some cases. This is when the 
            // runtime can't determine the XML type for a particular type, for
            // example for a non-JAXB Java bean. This makes interface checking
            // rather lenient with errors being detected at runtime
            if (sourceLogicalType.getTypeName() == null ||
                targetLogicalType.getTypeName() == null) {
                return true;
            }

            boolean match = sourceLogicalType.getTypeName().equals(targetLogicalType.getTypeName());

            if (!match){

                QName anyType = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
                if (sourceLogicalType.getTypeName().equals(anyType) || 
                    targetLogicalType.getTypeName().equals(anyType)){
                    // special case where a Java interface uses a generic type, e.g.
                    // public OMElement getGreetings(OMElement om)
                    // while the provided WSDL uses a specific type. So we assume
                    // that xsd:anyType matched anything
                    match = true;
                } else {
                    if (audit != null){
                        audit.append("Operation argument types source = " + 
                                     sourceLogicalType.getTypeName() + 
                                     " target = " + 
                                     targetLogicalType.getTypeName() +
                        " don't match for");
                    }
                }
            }

            return match; 
        }

    }

    /**
     * Check that two interface contracts are equal. The contracts are equal if the two contracts have the 
     * same set of operations, with each operation having the same signature, both for forward and callback
     * interfaces
     * @param source
     * @param target
     * @return
     */
    public boolean isMutuallyCompatible(InterfaceContract source, InterfaceContract target) {
        ExtensionType ext = source.getInterface().getExtensionType();
        InterfaceContract sourceContract = null;

        // Are the forward interfaces equal?
        if (isMutuallyCompatible(source.getInterface(), target.getInterface())) {
            // Is there a Callback interface?
            if (source.getCallbackInterface() == null && target.getCallbackInterface() == null) {
                return true;
            } else {
                if (isMutuallyCompatible(source.getCallbackInterface(), target.getCallbackInterface())) {
                    return true;
                } // end if
            } // end if
        } // end if
        return false;
    } // end method isEqual

    /**
     * Check that two interfaces are equal. The interfaces are equal if the two interfaces have the 
     * same set of operations, with each operation having the same signature. 
     * @param source
     * @param target
     * @return
     */
    public boolean isMutuallyCompatible(Interface source, Interface target) {
        if (source == target) {
            // Shortcut for performance
            return true;
        } // end if
        if (source == null || target == null) {
            return false;
        } // end if

        if (source.isDynamic() || target.isDynamic()) {
            return true;
        }

        if (source.isRemotable() != target.isRemotable()) {
            return false;
        }
        if (source.getOperations().size() != target.getOperations().size()) {
            return false;
        }

        for (Operation operation : source.getOperations()) {
            Operation targetOperation = getOperation(target.getOperations(), operation.getName());
            if (targetOperation == null) {
                return false;
            }
            if (!isCompatible(operation, targetOperation, Compatibility.SUBSET)) {
                return false;
            }
        }
        return true;
    } // end method isEqual

    public boolean isCompatible(Operation source, Operation target, Compatibility compatibilityType) {
        return isCompatible(source, target, compatibilityType, true);
    }

    public boolean isCompatible(Operation source, Operation target, Compatibility compatibilityType, boolean byValue) {
        return isCompatible(source, target, compatibilityType, true, null);
    }

    public boolean isCompatible(Operation source, Operation target, Compatibility compatibilityType, boolean byValue, Audit audit) {
        if (source == target) {
            return true;
        }

        if (source.isDynamic() || target.isDynamic()) {
            return true;
        }

        // Check name
        if (!source.getName().equals(target.getName())) {
            if (audit != null){
                audit.append("operation names are not the same source = " +
                             source.getName() + 
                             " target = " +
                             target.getName());
                audit.appendSeperator();
            }
            return false;
        }

        if (source.getInterface().isRemotable() != target.getInterface().isRemotable()) {
            if (audit != null){
                audit.append("Interfaces have different remote settings source = " +
                             source.getName() + 
                             " target = " +
                             target.getName());
                audit.appendSeperator();
            }            
            return false;
        }

        if (source.isNonBlocking() != target.isNonBlocking()) {
            if (audit != null){
                audit.append("operations one-way not the same, source = " +
                             source.isNonBlocking() + 
                             " target = " +
                             target.isNonBlocking());
                audit.appendSeperator();
            }            
            return false;
        }

        boolean passByValue = (source.getInterface().isRemotable()) && byValue;

        //        if (source.getInterface().isRemotable()) {
        //            return true;
        //        }

        // FIXME: We need to deal with wrapped<-->unwrapped conversion

        // Check output type
        List<DataType> sourceOutputType = source.getOutputType().getLogical();
        List<DataType> targetOutputType = target.getOutputType().getLogical();

        boolean checkSourceWrapper = true;
        List<DataType> sourceInputType = source.getInputType().getLogical();
        if (source.isWrapperStyle() && source.getWrapper() != null) {
            sourceInputType = source.getWrapper().getUnwrappedInputType().getLogical();
            sourceOutputType = source.getWrapper().getUnwrappedOutputType().getLogical();
            checkSourceWrapper = false;
        }
        boolean checkTargetWrapper = true;
        List<DataType> targetInputType = target.getInputType().getLogical();
        if (target.isWrapperStyle() && target.getWrapper() != null) {
            targetInputType = target.getWrapper().getUnwrappedInputType().getLogical();
            targetOutputType = target.getWrapper().getUnwrappedOutputType().getLogical();
            checkTargetWrapper = false;
        }

        /* TODO - Why are we assuming compatibility if one side is wrapped and the other is not?
        if (checkSourceWrapper != checkTargetWrapper) {
            return true;
        }
         */

        if ( sourceOutputType.size() != targetOutputType.size()) {
            if (audit != null){
                audit.append("different number of output types");
                audit.appendSeperator();
            } 
            return false;
        }

        for ( int i=0; i < sourceOutputType.size(); i++) {
            if (!isCompatible(targetOutputType.get(i), sourceOutputType.get(i), passByValue, audit)) {
                if (audit != null){
                    audit.append(" output types");
                    audit.appendSeperator();
                } 
                return false;
            }
        }


        if (sourceInputType.size() != targetInputType.size()) {
            if (audit != null){
                audit.append("different number of input types");
                audit.appendSeperator();
            } 
            return false;
        }

        int size = sourceInputType.size();
        for (int i = 0; i < size; i++) {
            if (!isCompatible(sourceInputType.get(i), targetInputType.get(i), passByValue, audit)) {
                if (audit != null){
                    audit.append(" input types");
                    audit.appendSeperator();
                } 
                return false;
            }
        }

        // Check fault types
        for (DataType targetFaultType : target.getFaultTypes()) {
            // Source fault types must be the same or superset of target fault
            // types
            boolean found = true;
            for (DataType sourceFaultType : source.getFaultTypes()) {
                found = false;
                if (isCompatible(targetFaultType, sourceFaultType, passByValue, audit)) {
                    // Target fault type can be covered by the source fault type
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (audit != null){
                    audit.append("Fault types incompatible");
                    audit.appendSeperator();
                } 
                return false;
            }
        }

        return true;
    }    
    public boolean isCompatibleByReference(Operation source, Operation target, Compatibility compatibilityType) {
        return isCompatible(source, target, compatibilityType, false);
    }

    public boolean isCompatibleByValue(Operation source, Operation target, Compatibility compatibilityType) {
        return isCompatible(source, target, compatibilityType, true);
    }

    @Override
    public boolean isCompatibleWithoutUnwrapByValue(Operation source, Operation target, Compatibility compatibilityType) {
        if (!source.isWrapperStyle() == target.isWrapperStyle()) {
            return false; 
        } else {
            return isCompatible(source, target, compatibilityType, true);
        }
    }

    // FIXME: How to improve the performance for the lookup
    private Operation getOperation(List<Operation> operations, String name) {
        for (Operation op : operations) {
            if (op.getName().equals(name)) {
                return op;
            }
        }
        return null;
    }

    /*
     * this variant of the checkCompatibility method is intended to supersede the one without an audit argument
     * Presence of both method variants indicates a state of partial development
     */
    public boolean checkCompatibility(InterfaceContract source,
                                      InterfaceContract target, 
                                      Compatibility compatibility,
                                      boolean ignoreCallback, 
                                      boolean silent, 
                                      Audit audit)
        throws IncompatibleInterfaceContractException {

        if (source == target) {
            // Shortcut for performance
            return true;
        }

        if (source == null || target == null) {
            return false;
        }

        if (source.getInterface() == target.getInterface()) {
            return ignoreCallback
            || isCallbackCompatible(source, target, silent, audit);
        }

        if (source.getInterface() == null || target.getInterface() == null) {
            return false;
        }

        if (source.getInterface().isDynamic()
            || target.getInterface().isDynamic()) {
            return ignoreCallback
            || isCallbackCompatible(source, target, silent, audit);
        }

        if (source.getInterface().isRemotable() != target.getInterface()
            .isRemotable()) {
            if (!silent) {
                audit.append("Remotable settings do not match: "+ source + "," + target); // TODO see if serialization is sufficient
                audit.appendSeperator();
                throw new IncompatibleInterfaceContractException(
                                                                 "Remotable settings do not match", source, target);

            } else {
                return false;
            }
        }

        for (Operation operation : source.getInterface().getOperations()) {
            Operation targetOperation = map(target.getInterface(), operation);
            if (targetOperation == null) {
                if (!silent) {
                    audit.append("Operation " + operation.getName()+ " not found on target"); 
                    audit.appendSeperator();
                    throw new IncompatibleInterfaceContractException(
                                                                     "Operation " + operation.getName()
                                                                     + " not found on target", source, target);
                } else {
                    return false;
                }
            }

            if (!silent) {
                if (audit == null)
                    audit = new Audit();
                if (!isCompatible(operation, targetOperation,
                                  Compatibility.SUBSET, true, audit)) {
                    audit.append("Operations called " + operation.getName()+ " are not compatible"); 
                    audit.appendSeperator();
                    throw new IncompatibleInterfaceContractException(
                                                                     "Operations called " + operation.getName()
                                                                     + " are not compatible " + audit, source,
                                                                     target);
                }
            } else {
                if (!isCompatible(operation, targetOperation,
                                  Compatibility.SUBSET)) {
                    return false;
                }
            }
        }

        return ignoreCallback || isCallbackCompatible(source, target, silent, audit);
    }

    /*
     * The old checkCompatibility operation without auditing. This just delegates to the new one for the time
     * being while there are still calls that don't provide and audit object. In the medium term when the calls have
     * been converted to sue the new opetion directly this should be removed. 
     */
    public boolean checkCompatibility(InterfaceContract source,
                                      InterfaceContract target,
                                      Compatibility compatibility,
                                      boolean ignoreCallback,
                                      boolean silent) 
        throws IncompatibleInterfaceContractException {

        // create dummy audit object.
        Audit audit = new Audit();

        return checkCompatibility(source, 
                                  target, 
                                  compatibility, 
                                  ignoreCallback, 
                                  silent,
                                  audit);
    }



    protected boolean isCallbackCompatible(InterfaceContract source, InterfaceContract target, boolean silent, Audit audit)
        throws IncompatibleInterfaceContractException {
        if (source.getCallbackInterface() == null && target.getCallbackInterface() == null) {
            return true;
        }
        if (source.getCallbackInterface() == null || target.getCallbackInterface() == null) {
            if (!silent) {
                audit.append("Callback interface doesn't match as one of the callback interfaces is null");
                audit.appendSeperator();
                throw new IncompatibleInterfaceContractException("Callback interface doesn't match as one of the callback interfaces is null", source, target);
            } else {
                return false;
            }
        }

        for (Operation operation : source.getCallbackInterface().getOperations()) {
            Operation targetOperation =
                getOperation(target.getCallbackInterface().getOperations(), operation.getName());
            if (targetOperation == null) {
                if (!silent) {
                    audit.append("Callback operation not found on target " + operation.getName());
                    audit.appendSeperator();
                    throw new IncompatibleInterfaceContractException("Callback operation not found on target", source,
                                                                     target, null, targetOperation);
                } else {
                    return false;
                }
            }
            if (!source.getCallbackInterface().isRemotable()) {
                // FIXME: for remotable operation, only compare name for now
                if (!operation.equals(targetOperation)) {
                    if (!silent) {
                        audit.append("Target callback operation is not compatible " + operation.getName());
                        audit.appendSeperator();
                        throw new IncompatibleInterfaceContractException("Target callback operation is not compatible",
                                                                         source, target, operation, targetOperation);
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isCompatibleSubset(Interface source, Interface target) {
        if (source == target) {
            // Shortcut for performance
            return true;
        }
        if (source == null || target == null) {
            return false;
        }

        if (source.isDynamic() || target.isDynamic()) {
            return true;
        }

        if (source.isRemotable() != target.isRemotable()) {
            return false;
        }

        for (Operation operation : source.getOperations()) {
            Operation targetOperation = getOperation(target.getOperations(), operation.getName());
            if (targetOperation == null) {
                return false;
            }
            if (!isCompatible(operation, targetOperation, Compatibility.SUBSET)) {
                return false;
            }
        }
        return true;
    }

    /*
     * the variant of isCompatibleSubset with the audit parameter is intended to supersede the other
     * -- the presence of both indicates a partial development state
     */
    public boolean isCompatibleSubset(InterfaceContract source, InterfaceContract target, Audit audit) {

        try {
            return checkCompatibility(source, target, Compatibility.SUBSET, false, false, audit);
        } catch (IncompatibleInterfaceContractException e) {
            return false;
        }
    }

    public boolean isCompatibleSubset(InterfaceContract source, InterfaceContract target) {

        try {
            return checkCompatibility(source, target, Compatibility.SUBSET, false, false);
        } catch (IncompatibleInterfaceContractException e) {
            return false;
        }
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.InterfaceContractMapper#map(org.apache.tuscany.sca.interfacedef.Interface,
     *      org.apache.tuscany.sca.interfacedef.Operation)
     */
    public Operation map(Interface target, Operation source) {
        // TODO: How to handle the case that source operation is dynamic?
        if (target == null || target.isDynamic()) {
            return source;
        } else if (target.isRemotable()) {
            for (Operation op : target.getOperations()) {
                if (op.getName().equals(source.getName())) {
                    return op;
                }
            }
            return null;
        } else {
            for (Operation op : target.getOperations()) {
                if (isCompatible(source, op, Compatibility.SUBSET)) {
                    return op;
                }
            }
            return null;
        }
    }

}
