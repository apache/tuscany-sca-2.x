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

import java.util.List;

import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceContractMapperImpl implements InterfaceContractMapper {

    public boolean isCompatible(DataType source, DataType target, boolean passByValue) {
        if (source == target) {
            return true;
        }
        if (!passByValue) {
            if (source == null || target == null) {
                return false;
            }
            // For local case
            return target.getPhysical().isAssignableFrom(source.getPhysical());
        } else {
            // FIXME: [rfeng] How to test if two remotable data type is compatible?
            // We will need to understand the different typing system used by the databindings
            // We should probably delegate to some extensions here
            return true;
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
        if (source == target) {
            return true;
        }

        if (source.isDynamic() || target.isDynamic()) {
            return true;
        }

        // Check name
        if (!source.getName().equals(target.getName())) {
            return false;
        }

        if (source.getInterface().isRemotable() != target.getInterface().isRemotable()) {
            return false;
        }

        boolean passByValue = (source.getInterface().isRemotable()) && byValue;

        //        if (source.getInterface().isRemotable()) {
        //            return true;
        //        }

        // FIXME: We need to deal with wrapped<-->unwrapped conversion

        // Check output type
        DataType<?> sourceOutputType = source.getOutputType();
        DataType<?> targetOutputType = target.getOutputType();

        boolean checkSourceWrapper = true;
        List<DataType> sourceInputType = source.getInputType().getLogical();
        if (source.isWrapperStyle() && source.getWrapper() != null) {
            sourceInputType = source.getWrapper().getUnwrappedInputType().getLogical();
            sourceOutputType = source.getWrapper().getUnwrappedOutputType();
            checkSourceWrapper = false;
        }
        boolean checkTargetWrapper = true;
        List<DataType> targetInputType = target.getInputType().getLogical();
        if (target.isWrapperStyle() && target.getWrapper() != null) {
            targetInputType = target.getWrapper().getUnwrappedInputType().getLogical();
            targetOutputType = target.getWrapper().getUnwrappedOutputType();
            checkTargetWrapper = false;
        }

        if (checkSourceWrapper != checkTargetWrapper) {
            return true;
        }

        if (!isCompatible(targetOutputType, sourceOutputType, passByValue)) {
            return false;
        }

        if (sourceInputType.size() != targetInputType.size()) {
            return false;
        }

        int size = sourceInputType.size();
        for (int i = 0; i < size; i++) {
            if (!isCompatible(sourceInputType.get(i), targetInputType.get(i), passByValue)) {
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
                if (isCompatible(targetFaultType, sourceFaultType, passByValue)) {
                    // Target fault type can be covered by the source fault type
                    found = true;
                    break;
                }
            }
            if (!found) {
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

    // FIXME: How to improve the performance for the lookup
    private Operation getOperation(List<Operation> operations, String name) {
        for (Operation op : operations) {
            if (op.getName().equals(name)) {
                return op;
            }
        }
        return null;
    }

    public boolean checkCompatibility(InterfaceContract source,
                                      InterfaceContract target,
                                      Compatibility compatibility,
                                      boolean ignoreCallback,
                                      boolean silent) throws IncompatibleInterfaceContractException {
        if (source == target) {
            // Shortcut for performance
            return true;
        }

        if (source == null || target == null) {
            return false;
        }

        if (source.getInterface() == target.getInterface()) {
            return ignoreCallback || isCallbackCompatible(source, target, silent);
        }

        if (source.getInterface() == null || target.getInterface() == null) {
            return false;
        }

        if (source.getInterface().isDynamic() || target.getInterface().isDynamic()) {
            return ignoreCallback || isCallbackCompatible(source, target, silent);
        }

        if (source.getInterface().isRemotable() != target.getInterface().isRemotable()) {
            if (!silent) {
                throw new IncompatibleInterfaceContractException("Remotable settings do not match", source, target);
            } else {
                return false;
            }
        }

        for (Operation operation : source.getInterface().getOperations()) {
            Operation targetOperation = map(target.getInterface(), operation);
            if (targetOperation == null) {
                if (!silent) {
                    throw new IncompatibleInterfaceContractException("Operation " + 
                                                                     operation.getName() +
                                                                     " not found on target", 
                                                                     source, 
                                                                     target);
                } else {
                    return false;
                }
            }
            if (!source.getInterface().isRemotable()) {
                // FIXME: for remotable operation, only compare name for now
                if (!isCompatible(operation, targetOperation, Compatibility.SUBSET)) {
                    if (!silent) {
                        throw new IncompatibleInterfaceContractException("Target operations called " +
                                                                         operation.getName() +
                                                                         " are not compatible",
                                                                         source, 
                                                                         target);
                    } else {
                        return false;
                    }
                }
            }
        }

        return ignoreCallback || isCallbackCompatible(source, target, silent);
    }

    protected boolean isCallbackCompatible(InterfaceContract source, InterfaceContract target, boolean silent)
        throws IncompatibleInterfaceContractException {
        if (source.getCallbackInterface() == null && target.getCallbackInterface() == null) {
            return true;
        }
        if (source.getCallbackInterface() == null || target.getCallbackInterface() == null) {
            if (!silent) {
                throw new IncompatibleInterfaceContractException("Callback interface doesn't match", source, target);
            } else {
                return false;
            }
        }

        for (Operation operation : source.getCallbackInterface().getOperations()) {
            Operation targetOperation =
                getOperation(target.getCallbackInterface().getOperations(), operation.getName());
            if (targetOperation == null) {
                if (!silent) {
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
