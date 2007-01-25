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

package org.apache.tuscany.spi.loader;

import org.apache.tuscany.spi.model.Multiplicity;

/**
 * Denote the violation of multiplicity declaration for a reference
 * 
 * @version $Rev $Date
 */
public class ReferenceMultiplicityViolationException extends LoaderException {
    private static final long serialVersionUID = -4049116356211578827L;

    private final Multiplicity multiplicity;
    private final int numberOfTargets;

    /**
     * @param message
     * @param identifier
     * @param multiplicity
     * @param numberOfTargets
     */
    public ReferenceMultiplicityViolationException(String message,
                                          String identifier,
                                          Multiplicity multiplicity,
                                          int numberOfTargets) {
        super(message, identifier);
        this.multiplicity = multiplicity;
        this.numberOfTargets = numberOfTargets;
    }

    /**
     * @param identifier
     * @param multiplicity
     * @param numberOfTargets
     */
    public ReferenceMultiplicityViolationException(String identifier, Multiplicity multiplicity, int numberOfTargets) {
        this("Multiplicity is violated", identifier, multiplicity, numberOfTargets);
    }
    
    /**
     * Get the multiplicity for the reference definition
     * 
     * @return multiplicity of the reference definition
     */
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * Get the number of targets defined for this reference
     * @return number of targets for this reference
     */
    public int getNumberOfTargets() {
        return numberOfTargets;
    }
}
