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

package org.apache.tuscany.sca.contribution.resource.impl;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportAllModelResolver;
import org.apache.tuscany.sca.contribution.resource.ResourceExport;
import org.apache.tuscany.sca.contribution.resource.ResourceImport;
import org.apache.tuscany.sca.contribution.service.ContributionListener;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;

/**
 * Resource Import/Export contribution listener
 * The listener would process all import/export from a given contribution 
 * and initialize the model resolvers properly
 * 
 * @version $Rev$ $Date$
 */
public class ResourceImportExportListener implements ContributionListener {

    /**
     * Initialize the import/export model resolvers
     * Export model resolvers are same as Contribution model resolver
     * Import model resolvers are matched to a specific contribution if a location URI is specified, 
     *    otherwise it try to resolve against all the other contributions
     * Also set the exporting contributions used by contribution ClassLoaders to 
     * match import/export for class loading.
     */    
    public void contributionAdded(ContributionRepository repository, Contribution contribution) {
        // Initialize the contribution exports
        for (Export export: contribution.getExports()) {
            export.setModelResolver(contribution.getModelResolver());
        }
        
        // Initialize the contribution imports
        for (Import import_: contribution.getImports()) {
            boolean initialized = false;

            if (import_ instanceof ResourceImport) {
                ResourceImport resourceImport = (ResourceImport)import_;
                
                // Find a matching contribution
                if (resourceImport.getLocation() != null) {
                    Contribution targetContribution = repository.getContribution(resourceImport.getLocation());
                    if (targetContribution != null) {
                    
                        // Find a matching contribution export
                        for (Export export: targetContribution.getExports()) {
                            if (export instanceof ResourceExport) {
                                ResourceExport resourceExport = (ResourceExport)export;
                                if (resourceImport.getURI().equals(resourceExport.getURI())) {
                                	resourceImport.setModelResolver(resourceExport.getModelResolver());
                                    initialized = true;
                                    break;
                                }
                            }
                        }
                    }
                } 
                
                //if no location was specified, try to resolve with any contribution            
                if( !initialized ) {
                    // Use a resolver that will consider all contributions
                    import_.setModelResolver(new DefaultImportAllModelResolver(import_, repository.getContributions()));
                }
            } 
        }
    }

    public void contributionRemoved(ContributionRepository repository, Contribution contribution) {

    }

    public void contributionUpdated(ContributionRepository repository, Contribution oldContribution, Contribution contribution) {

    }

}
