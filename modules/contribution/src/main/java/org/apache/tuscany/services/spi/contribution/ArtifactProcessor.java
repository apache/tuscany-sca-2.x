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
package org.apache.tuscany.services.spi.contribution;



/**
 * Interface for extensions that can process contributions.
 * 
 * @version $Rev: 522653 $ $Date: 2007-03-26 15:30:21 -0700 (Mon, 26 Mar 2007) $
 */
public interface ArtifactProcessor <S, M, T> {
    
    /**
     * Read a model from a source. Examples of sources are: an inputstream, an
     * XML reader, or an object representation of the source.
     * @param source
     * @return a model representation of the source.
     */
    M read(S source) throws ContributionReadException;
    
    /**
     * Resolve references from this model to other models. For example references
     * from a composite to another one, or references from a composite to a WSDL
     * model.
     * @param model the model to resolve
     * @param the resolver to use to resolve referenced models
     */
    void resolve(M model, ArtifactResolver resolver) throws ContributionException;
    
    /**
     * Optimize a model for consumption by the runtime. This can include applying policies at different
     * levels, or determining the configuration of services, references and properties in nested compositions
     * for example.
     * @param model the model to optimize
     */
    void optimize(M model) throws ContributionException;
    
    /**
     * Returns the type of artifact handled by this artifact processor. 
     * @return the type of artifact handled by this artifact processor
     */
    T getArtifactType();

    /**
     * Returns the type of model handled by this artifact processor.
     * @return the type of model handled by this artifact processor
     */
    Class<M> getModelType(); 
    
}
