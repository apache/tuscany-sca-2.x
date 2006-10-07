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
package org.apache.tuscany.spi.wire;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;

/**
 * Base class for wire service extensions
 *
 * @version $Rev$ $Date$
 */
public abstract class WireServiceExtension implements WireService {

    protected PolicyBuilderRegistry policyRegistry;
    protected WorkContext context;

    protected WireServiceExtension(WorkContext context, PolicyBuilderRegistry policyRegistry) {
        this.policyRegistry = policyRegistry;
        this.context = context;
    }

    /**
     * Compares two operations for wiring compatibility as defined by the SCA assembly specification, namely: <p/> <ol>
     * <li>compatibility for the individual method is defined as compatibility of the signature, that is method name,
     * input types, and output types MUST BE the same. <li>the order of the input and output types also MUST BE the
     * same. <li>the set of Faults and Exceptions expected by the source MUST BE the same or be a superset of those
     * specified by the service. </ol>
     *
     * @param source the source contract to compare
     * @param target the target contract to compare
     * @throws IncompatibleServiceContractException
     *          if the two contracts don't match
     */
    public void checkCompatibility(ServiceContract<?> source, ServiceContract<?> target, boolean ignoreCallback)
        throws IncompatibleServiceContractException {
        if (source == target) {
            // Shortcut for performance
            return;
        }
        if (source.isRemotable() != target.isRemotable()) {
            IncompatibleServiceContractException ex =
                new IncompatibleServiceContractException("The remotable settings don't match");
            ex.setIdentifier(source.toString() + "," + target.toString());
            throw ex;
        }
        if (source.getInteractionScope() != target.getInteractionScope()) {
            IncompatibleServiceContractException ex =
                new IncompatibleServiceContractException("The interaction scopes don't match");
            ex.setIdentifier(source.toString() + "," + target.toString());
            throw ex;
        }

        for (Operation<?> operation : source.getOperations().values()) {
            Operation<?> targetOperation = target.getOperations().get(operation.getName());
            if (targetOperation == null) {
                IncompatibleServiceContractException ex =
                    new IncompatibleServiceContractException("Operation not found on target");
                ex.setIdentifier(operation.getName());
                throw ex;
            }
            if (!operation.equals(targetOperation)) {
                IncompatibleServiceContractException ex =
                    new IncompatibleServiceContractException("Target operation is not compatible");
                ex.setIdentifier(operation.getServiceContract().toString() + ":" + operation.getName());
                throw ex;
            }
        }

        if (ignoreCallback) {
            return;
        }

        for (Operation<?> operation : source.getCallbackOperations().values()) {
            Operation<?> targetOperation = target.getCallbackOperations().get(operation.getName());
            if (targetOperation == null) {
                IncompatibleServiceContractException ex =
                    new IncompatibleServiceContractException("Callback operation not found on target");
                ex.setIdentifier(operation.getName());
                throw ex;
            }
            if (!operation.equals(targetOperation)) {
                IncompatibleServiceContractException ex =
                    new IncompatibleServiceContractException("Target callback operation is not compatible");
                ex.setIdentifier(operation.getName());
                throw ex;
            }
        }
    }

}
