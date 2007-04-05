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

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.spi.TuscanyException;

/**
 * Denotes imcompatible service contracts for a wire
 *
 * @version $Rev$ $Date$
 */
public class IncompatibleServiceContractException extends TuscanyException {
    private static final long serialVersionUID = 5127478601823295587L;
    private final Contract source;
    private final Contract target;
    private final Operation sourceOperation;
    private final Operation targetOperation;

    public IncompatibleServiceContractException(String message, Contract source, Contract target) {
        super(message, (String) null);
        this.source = source;
        this.target = target;
        this.sourceOperation = null;
        this.targetOperation = null;
    }


    public IncompatibleServiceContractException(String message,
                                                Contract source,
                                                Contract target,
                                                Operation sourceOperation, Operation targetOperation) {
        super(message, (String) null);
        this.source = source;
        this.target = target;
        this.sourceOperation = sourceOperation;
        this.targetOperation = targetOperation;
    }

    public Contract getTarget() {
        return target;
    }

    public Contract getSource() {
        return source;
    }

    public Operation getSourceOperation() {
        return sourceOperation;
    }

    public Operation getTargetOperation() {
        return targetOperation;
    }
}
