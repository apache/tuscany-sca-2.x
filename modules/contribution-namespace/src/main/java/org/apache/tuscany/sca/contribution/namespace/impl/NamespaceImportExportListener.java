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

package org.apache.tuscany.sca.contribution.namespace.impl;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.namespace.NamespaceExport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportAllModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionListener;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;

/**
 * Namespace Import/Export contribution listener
 * The listener would process all import/export from a given contribution 
 * and initialize the model resolvers properly
 * 
 * @version $Rev$ $Date$
 */
public class NamespaceImportExportListener implements ContributionListener {

    /**
     * Initialize the import/export model resolvers
     * Export model resolvers are same as Contribution model resolver
     * Import model resolvers are matched to a specific contribution if a location uri is specified, 
     *    otherwise it try to resolve agains all the other contributions
     */    
    public void contributionAdded(ContributionRepository repository, Contribution contribution) {
        // Initialize the contribution exports
        for (Export export: contribution.getExports()) {
            export.setModelResolver(contribution.getModelResolver());
        }
        
        // Initialize the contribution imports
        for (Import import_: contribution.getImports()) {
            boolean initialized = false;
            
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                
                // Find a matching contribution
                if (namespaceImport.getLocation() != null) {
                    Contribution targetContribution = repository.getContribution(namespaceImport.getLocation());
                    if (targetContribution != null) {
                    
                        // Find a matching contribution export
                        for (Export export: targetContribution.getExports()) {
                            if (export instanceof NamespaceExport) {
                                NamespaceExport namespaceExport = (NamespaceExport)export;
                                if (namespaceImport.getNamespace().equals(namespaceExport.getNamespace())) {
                                    namespaceImport.setModelResolver(namespaceExport.getModelResolver());
                                    initialized = true;
                                    break;
                                }
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

    public void contributionRemoved(ContributionRepository repository, Contribution contribution) {

    }

    public void contributionUpdated(ContributionRepository repository, Contribution oldContribution, Contribution contribution) {

    }

}
