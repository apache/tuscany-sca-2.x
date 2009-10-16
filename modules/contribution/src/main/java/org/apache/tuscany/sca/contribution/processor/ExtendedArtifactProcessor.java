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
package org.apache.tuscany.sca.contribution.processor;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * Interface for extended Artifact Processors which require a pre-resolve phase prior to the resolve phase
 * 
 * @version $Rev$ $Date$
 */
public interface ExtendedArtifactProcessor<M> extends ArtifactProcessor<M> {
    
    /**
     * Pre-resolve references from this model to other models. Used for models where initial setup of
     * the resolve phase is required.  An example is Contribution models with imports and exports which must
     * be set up prior to the main resolve phase
     * 
     * @param model The model to resolve
     * @param resolver The resolver to use to resolve referenced models
     * @param context The context
     */
    void preResolve(M model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException;
    
} // end interface
