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
package org.apache.tuscany.spi.generator;

import org.apache.tuscany.spi.model.IntentDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalInterceptorDefinition;

/**
 * Implementations are responsible for generating physical interceptor definitions for a wire based on an intent.
 *
 * @version $Rev$ $Date$
 */
public interface InterceptorGenerator<T extends IntentDefinition> {

    /**
     * Generates the physical interceptor definition for a wire
     *
     * @param definition the intent definition
     * @param context    the current generator context
     * @return the interceptor definition
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalInterceptorDefinition generate(T definition, GeneratorContext context) throws GenerationException;

}

