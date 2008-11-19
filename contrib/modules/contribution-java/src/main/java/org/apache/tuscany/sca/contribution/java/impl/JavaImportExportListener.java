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

package org.apache.tuscany.sca.contribution.java.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportAllModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionListener;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;

/**
 * Java Import/Export contribution listener
 * The listener would process all import/export from a given contribution 
 * and initialize the model resolvers properly
 * 
 * @version $Rev$ $Date$
 */
public class JavaImportExportListener implements ContributionListener {
    
    private ContributionFactory contributionFactory;
    
    /**
     * Constructs a new JavaImportExportListener
     */
    public JavaImportExportListener(ModelFactoryExtensionPoint modelFactories) {
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
    }
    
    /**
     * Initialize the import/export model resolvers
     * Export model resolvers are same as Contribution model resolver
     * Import model resolvers are matched to a specific contribution if a location URI is specified, 
     *    otherwise it try to resolve against all the other contributions
     * Also set the exporting contributions used by contribution ClassLoaders to 
     * match import/export for class loading.
     */
    public void contributionAdded(ContributionRepository repository, Contribution contribution) {
        
        // If the contribution does not contain sca-contribution.xml metadata
        // (for example it's an existing JAR developed before SCA existed)
        // export all its Java packages
        ModelResolver modelResolver = contribution.getModelResolver();
        
        // Look for META-INF/sca-contribution.xml
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI(Contribution.SCA_CONTRIBUTION_META);
        artifact = modelResolver.resolveModel(Artifact.class, artifact);
        if (artifact.getLocation() == null) {

            // Look for META-INF/sca-contribution-generated.xml
            artifact.setURI(Contribution.SCA_CONTRIBUTION_GENERATED_META);
            artifact = modelResolver.resolveModel(Artifact.class, artifact);
            if (artifact.getLocation() == null) {
                
                // No contribution metadata file was found, default to export all the
                // Java packages found in the contribution
                Set<String> packages = new HashSet<String>();
                for (Artifact a: contribution.getArtifacts()) {
                    String uri = a.getURI();
                    if (uri.endsWith(".class")) {
                        uri = uri.substring(0, uri.length() - 6);
                        int d = uri.lastIndexOf('/');
                        if (d != -1) {
                            packages.add(uri.substring(0, d).replace('/', '.'));
                        }
                    }
                }
                
                // Add Java export model objects for all the packages we found
                for (String pkg: packages) {
                    JavaExport export = new JavaExportImpl();
                    export.setPackage(pkg);
                    contribution.getExports().add(export);
                }
            }
        }
        
        // Initialize the contribution exports
        for (Export export: contribution.getExports()) {
            export.setModelResolver(contribution.getModelResolver());
        }
        
        // Initialize the contribution imports
        for (Import import_: contribution.getImports()) {
            boolean initialized = false;
            
            if(import_ instanceof JavaImport) {
                JavaImport javaImport = (JavaImport) import_;
                
                //Find a matching contribution
                if(javaImport.getLocation() != null) {
                    Contribution targetContribution = repository.getContribution(javaImport.getLocation());
                    if (targetContribution != null) {
                    
                        // Find a matching contribution export
                        for (Export export: targetContribution.getExports()) {
                            if (export instanceof JavaExport) {
                                JavaExport javaExport = (JavaExport)export;
                                if (javaImport.getPackage().equals(javaExport.getPackage())) {
                                    List<Contribution> exportingContributions = new ArrayList<Contribution>();
                                    exportingContributions.add(targetContribution);
                                    javaImport.setModelResolver(new JavaImportModelResolver(exportingContributions, javaExport.getModelResolver()));
                                    initialized = true;
                                    break;
                                }
                            }
                        }
                    }                    
                }

                //if no location was specified, try to resolve with any contribution
                if (!initialized) {
                    //Use a resolver that will consider all contributions
                    import_.setModelResolver(new JavaImportModelResolver(repository.getContributions(), new DefaultImportAllModelResolver(import_, repository.getContributions())));
                }
            }
        }
    }

    public void contributionRemoved(ContributionRepository repository, Contribution contribution) {

    }

    public void contributionUpdated(ContributionRepository repository, Contribution oldContribution, Contribution contribution) {

    }

}
