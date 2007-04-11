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

package org.apache.tuscany.interfacedef;

/**
 * The InterfaceContractMapper is responsible to match interfaces
 * 
 * @version $Rev$ $Date$
 */
public interface InterfaceContractMapper {
    /**
     * Check the compatiblity of the source and the target interface contracts.
     * <p>
     * A wire may only connect a source to a target if the target implements an
     * interface that is compatible with the interface required by the source.
     * The source and the target are compatible if: <p/>
     * <ol>
     * <li>the source interface and the target interface MUST either both be
     * remotable or they are both local
     * <li>the methods on the target interface MUST be the same as or be a
     * superset of the methods in the interface specified on the source
     * <li>compatibility for the individual method is defined as compatibility
     * of the signature, that is method name, input types, and output types MUST
     * BE the same.
     * <li>the order of the input and output types also MUST BE the same.
     * <li>the set of Faults and Exceptions expected by the source MUST BE the
     * same or be a superset of those specified by the service.
     * <li>other specified attributes of the two interfaces MUST match,
     * including Scope and Callback interface
     * </ol>
     * <p/>
     * <p>
     * Please note this test is not symetric: the success of isCompatible(A, B)
     * does NOT imply isCompatible(B, A)
     * 
     * @param source The source interface contract
     * @param target The target interface contract
     * @return true if the source contract can be supported by the target
     *         contract
     */
    boolean isCompatible(InterfaceContract source, InterfaceContract target);

    /**
     * @param source
     * @param target
     * @param ignoreCallback
     * @param silent
     * @return
     * @throws IncompatibleInterfaceContractException
     */
    boolean checkCompatibility(InterfaceContract source,
                               InterfaceContract target,
                               boolean ignoreCallback,
                               boolean silent) throws IncompatibleInterfaceContractException;

    /**
     * Test if the source data type is compatible with the target data type. The
     * compatibility is defined as follows.
     * <ul>
     * <li>source's logical type is either the same or subtype of the target's
     * logical type
     * </ul>
     * For example, if the source type is a SDO Customer and the target type is
     * a JAXB Customer and both Customer are generated from the same XSD type.
     * 
     * @param source The source data type
     * @param target The target data type
     * @return
     */
    boolean isCompatible(DataType source, DataType target, boolean remotable);

    /**
     * Check if source operation is compatible with the target operation
     * 
     * @param source The source operation
     * @param target The target operation
     * @return true if the source operation is compatible with the target
     *         operation
     */
    boolean isCompatible(Operation source, Operation target, boolean remotable);

    /**
     * @param source
     * @param target
     * @return
     */
    boolean isCompatible(Interface source, Interface target);

    /**
     * Map the source operation to a compatible operation in the target
     * interface
     * 
     * @param target The target interface
     * @param source The source operation
     * @return A compatible operation
     */
    Operation map(Interface target, Operation source);
}
