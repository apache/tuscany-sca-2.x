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

package org.apache.tuscany.sca.contribution;

import java.util.List;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;


/**
 * The representation of an import.
 * 
 * @version $Rev$ $Date$
 */
public interface Import {

    /**
     * Returns the model resolver for the models representing artifacts
     * made available by this import.
     * 
     * @return The model resolver
     */
    ModelResolver getModelResolver();

    /**
     * Sets the model resolver for the models representing artifacts
     * made available by this import.
     * 
     * @param modelResolver The model resolver
     */
    void setModelResolver(ModelResolver modelResolver);
    
    /**
     * Returns the list of exporting contributions that can be used to match 
     * this import.
     * 
     * FIXME This should be removed, imports and exports should not be
     * tied together.
     * 
     * @return List of exporting contributions
     */
    List<Contribution> getExportContributions();
    
    /**
     * Sets the list of exporting contributions that can be used to match 
     * this import.
     * 
     * FIXME This should be removed, imports and exports should not be
     * tied together.
     * 
     * @param contributions List of exporting contributions
     */
    void setExportContributions(List<Contribution> contributions);
    
    /**
     * Verify is a specific export is provider of what is being imported
     * 
     * FIXME This should be removed, imports and exports should not be
     * tied together.
     * 
     * @param export The Exported being verified
     * @return true/false
     */
    boolean match(Export export);
}