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
package org.apache.tuscany.spi.policy;

/**
 * A registry for policy builders that dispatches to the appropriate builder when converting an assembly to runtime
 * artifacts. Policy builders operate on either a source- or target-side wires.
 *
 * @version $Rev$ $Date$
 * @deprecated
 */
public interface PolicyBuilderRegistry {

    int INITIAL = 0;
    int EXTENSION = 1;
    int FINAL = 2;

    /**
     * Registers a target-side policy builder. Called by extensions to register their builders.
     *
     * @param phase   the phase hwne the builder must be run
     * @param builder the builder to register
     */
    void registerTargetBuilder(int phase, TargetPolicyBuilder builder);

    /**
     * Registers a source-side policy builder. Called by extensions to register their builders.
     *
     * @param phase   the phase hwne the builder must be run
     * @param builder the builder to register
     */
    void registerSourceBuilder(int phase, SourcePolicyBuilder builder);
}
