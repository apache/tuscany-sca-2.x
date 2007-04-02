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
package org.apache.tuscany.assembly.model;

/**
 * Enumeration for multiplicity. Defines the number of wires that can connect a
 * reference to target services.
 * 
 * @version $Rev$ $Date$
 */
public enum Multiplicity {

    /**
     * Zero or one wire can have the reference as a source.
     */
    ZERO_ONE,

    /**
     * The default setting, one wire can have the reference as a source.
     */
    ONE_ONE,

    /**
     * Zero or more wires can have the reference as a source.
     */
    ZERO_N,

    /**
     * One or more wires can have the reference as a source.
     */
    ONE_N

}
