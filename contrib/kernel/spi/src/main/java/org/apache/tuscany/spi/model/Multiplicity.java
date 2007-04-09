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
package org.apache.tuscany.spi.model;

/**
 * Enumeration for multiplicity.
 */
public enum Multiplicity {
    /**
     * Indicates a relationship that is optionally connected to the requestor and which, if supplied, must be connected
     * to exactly one provider.
     */
    ZERO_ONE,

    /**
     * Indicates a relationship that must be connected between exactly one requestor and exactly one provider.
     */
    ONE_ONE,

    /**
     * Indicates a relationship that is optionally connects the requestor to zero to unbounded providers.
     */
    ZERO_N,

    /**
     * Indicates a relationship that must be connected at the requestor and which connects it to zero to unbounded
     * providers.
     */
    ONE_N

}
