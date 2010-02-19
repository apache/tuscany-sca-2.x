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

package org.apache.tuscany.sca.interfacedef;

/**
 * The InterfaceContractMapper is responsible to match interfaces
 * 
 * @version $Rev$ $Date$
 */
public interface InterfaceContractMapper {
    /**
     * @param source The source interface contract
     * @param target The target interface contract
     * @param compatibility The compatibility style 
     * @param ignoreCallback
     * @param silent
     * @return
     * @throws IncompatibleInterfaceContractException
     */
    boolean checkCompatibility(InterfaceContract source,
                               InterfaceContract target,
                               Compatibility compatibility,
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
     * @param passByValue A flag to indicate how the compatibility is checked
     * <ul>
     * <li>true: Check the two types as compatible "by-value" (can be copied)
     * <li>false: Check the two types as compatible "by-reference" (can be assigned)
     * </ul> 
     * @return true if the source data type is the same or subtype of the target data type 
     */
    boolean isCompatible(DataType<?> source, DataType<?> target, boolean passByValue);

    /**
     * Check if source operation is compatible with the target operation. A source operation is 
     * compatible with the target operation means the following:
     * 
     * <ol>
     * <li>compatibility for the two operations is defined as compatibility 
     * of the signature, i.e., the operation name, the input types, and the output types are the same
     * 
     * <li>the order of the input and output types of the source operation is the same as the order of 
     * the input and output types for the corresponding target operation
     * <li>the set of Faults and Exceptions expected by the source operation is the same as or is 
     * a SUPERSET of the set of Faults and Exceptions specified by the corresponding target operation
     * </ol>
     * 
     * Simply speaking, any request from the source operation can be processed by the target operation and
     * the normal response or fault/exception from the target operation can be handled by the source operation.
     * 
     * Please note this compatibility check is NOT symmetric.  
     * 
     * @param source The source operation
     * @param target The target operation
     * @param compatibilityType TODO
     * @return true if the source operation is compatible with the target
     *         operation
     */
    boolean isCompatible(Operation source, Operation target, Compatibility compatibilityType);

    /**
     * An interface A is a Compatible Subset of a second interface B if and only if all of points 1 through 6 
     * in the following list apply:
     * <ol>
     * <li>interfaces A and B are either both remotable or else both local
     * <li>the set of operations in interface A is the same as or is a subset of the set of operations in 
     * interface B
     * <li>compatibility for individual operations of the interfaces A and B is defined as compatibility 
     * of the signature, i.e., the operation name, the input types, and the output types are the same
     * <li>the order of the input and output types for each operation in interface A is the same as the 
     * order of the input and output types for the corresponding operation in interface B
     * <li>the set of Faults and Exceptions expected by each operation in interface A is the same as or is 
     * a superset of the set of Faults and Exceptions specified by the corresponding operation in interface B
     * <li>for checking the compatibility of 2 remotable interfaces which are in different interface 
     * languages, both are mapped to WSDL 1.1 (if not already WSDL 1.1) and compatibility checking is done 
     * between the WSDL 1.1 mapped interfaces.<br>
     * For checking the compatibility of 2 local interfaces which are in different interface languages, the 
     * method of checking compatibility is defined by the specifications which define those interface types, 
     * which must define mapping rules for the 2 interface types concerned.
     * 
     * </ol>
     * 
     * <b>The callback interfaces are not considered her.</b>
     * 
     * @param source The source interface 
     * @param target The target interface 
     * @return true if the source interface is a compatible subset of the target interface 
     */
    boolean isCompatibleSubset(Interface source, Interface target);

    /**
     * An interface A is a Compatible Subset of a second interface B if and only if all of points 1 through 7 
     * in the following list apply:
     * <ol>
     * <li>interfaces A and B are either both remotable or else both local
     * <li>the set of operations in interface A is the same as or is a subset of the set of operations in 
     * interface B
     * <li>compatibility for individual operations of the interfaces A and B is defined as compatibility 
     * of the signature, i.e., the operation name, the input types, and the output types are the same
     * <li>the order of the input and output types for each operation in interface A is the same as the 
     * order of the input and output types for the corresponding operation in interface B
     * <li>the set of Faults and Exceptions expected by each operation in interface A is the same as or is 
     * a superset of the set of Faults and Exceptions specified by the corresponding operation in interface B
     * <li>for checking the compatibility of 2 remotable interfaces which are in different interface 
     * languages, both are mapped to WSDL 1.1 (if not already WSDL 1.1) and compatibility checking is done 
     * between the WSDL 1.1 mapped interfaces.<br>
     * For checking the compatibility of 2 local interfaces which are in different interface languages, the 
     * method of checking compatibility is defined by the specifications which define those interface types, 
     * which must define mapping rules for the 2 interface types concerned.
     * <li>if either interface A or interface B declares a callback interface then both interface
     * A and interface B declare callback interfaces and the callback interface declared on interface B is a 
     * compatible subset of the callback interface declared on interface A, according to points 1 through 6 
     * above
     * </ol>
     * 
     * @param source The source interface contract
     * @param target The target interface contract
     * @return true if the source interface contract is a compatible subset of the target interface contract
     */
    boolean isCompatibleSubset(InterfaceContract source, InterfaceContract target);

    /**
     * Check that two interfaces are mutually compatible. The interfaces are mutually compatible if the two 
     * interfaces have the same set of operations, with each operation having the same signature (name, input 
     * types, output types and fault/exception types).
     * 
     * @param source an interface
     * @param target a second interface
     * @return true if the two interfaces are mutually compatible, otherwise return false
     */
    public boolean isMutuallyCompatible(Interface source, Interface target);

    /**
     * An interface A is Compatible with a second interface B if and only if all of points 1 through 7 in the
     * following list apply:<p>
     * <ol>
     * <li>interfaces A and B are either both remotable or else both local
     * <li>the set of operations in interface A is the same as the set of operations in interface B
     * <li>compatibility for individual operations of the interfaces A and B is defined as compatibility 
     * of the signature, i.e., the operation name, the input types, and the output types are the same
     * <li>the order of the input and output types for each operation in interface A is the same as the 
     * order of the input and output types for the corresponding operation in interface B
     * <li>the set of Faults and Exceptions expected by each operation in interface A is the
     * same as the set of Faults and Exceptions specified by the corresponding operation in interface B
     * <li>for checking the compatibility of 2 remotable interfaces which are in different interface 
     * languages, both are mapped to WSDL 1.1 (if not already WSDL 1.1) and compatibility checking is done 
     * between the WSDL 1.1 mapped interfaces.
     * <br>For checking the compatibility of 2 local interfaces which are in different interface languages, 
     * the method of checking compatibility is defined by the specifications which define those interface types, 
     * which must define mapping rules for the 2 interface types concerned.
     * <li>if either interface A or interface B declares a callback interface then both interface
     * A and interface B declare callback interfaces and the callback interface declared on interface A is 
     * compatible with the callback interface declared on interface B, according to points 1 through 6 above
     *  
     * @param source - the source interface contract
     * @param target - the target interface contract
     * @return true if the source and target interface contracts are mutually compatible
     */
    boolean isMutuallyCompatible(InterfaceContract source, InterfaceContract target);

    /**
     * Map the source operation to a compatible operation in the target interface
     * 
     * @param target The target interface
     * @param source The source operation
     * @return A compatible operation if the target interface is compatible superset of the source interface
     */
    Operation map(Interface target, Operation source);

}
