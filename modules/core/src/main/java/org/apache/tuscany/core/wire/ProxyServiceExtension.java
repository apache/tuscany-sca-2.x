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
package org.apache.tuscany.core.wire;

import java.util.List;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * Base class for wire service extensions
 * 
 * @version $Rev$ $Date$
 */
public abstract class ProxyServiceExtension implements ProxyService {
    protected WorkContext context;

    protected ProxyServiceExtension(WorkContext context) {
        this.context = context;
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

    public boolean checkCompatibility(Contract source, Contract target, boolean ignoreCallback, boolean silent)
        throws IncompatibleServiceContractException {
        if (source == target) {
            // Shortcut for performance
            return true;
        }
        if (source.getInterface().isRemotable() != target.getInterface().isRemotable()) {
            if (!silent) {
                throw new IncompatibleServiceContractException("Remotable settings do not match", source, target);
            } else {
                return false;
            }
        }
        if (source.getInterface().isConversational() != target.getInterface().isConversational()) {
            if (!silent) {
                throw new IncompatibleServiceContractException("Interaction scopes do not match", source, target);
            } else {
                return false;
            }
        }

        for (Operation operation : source.getInterface().getOperations()) {
            Operation targetOperation = getOperation(target.getInterface().getOperations(), operation.getName());
            if (targetOperation == null) {
                if (!silent) {
                    throw new IncompatibleServiceContractException("Operation not found on target", source, target);
                } else {
                    return false;
                }
            }
            if (!operation.equals(targetOperation)) {
                if (!silent) {
                    throw new IncompatibleServiceContractException("Target operations are not compatible", source,
                                                                   target);
                } else {
                    return false;
                }
            }
        }

        if (ignoreCallback) {
            return true;
        }

        if (source.getCallbackInterface() != null) {
            for (Operation operation : source.getCallbackInterface().getOperations()) {
                Operation targetOperation = getOperation(target.getCallbackInterface().getOperations(), operation
                    .getName());
                if (targetOperation == null) {
                    if (!silent) {
                        throw new IncompatibleServiceContractException("Callback operation not found on target",
                                                                       source, target, null, targetOperation);
                    } else {
                        return false;
                    }
                }
                if (!operation.equals(targetOperation)) {
                    if (!silent) {
                        throw new IncompatibleServiceContractException("Target callback operation is not compatible",
                                                                       source, target, operation, targetOperation);
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
