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

package org.apache.tuscany.sca.workspace.builder.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.resolver.DefaultImportAllModelResolver;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;

/**
 * A contribution dependency builder.
 *
 * @version $Rev$ $Date$
 */
public class ContributionDependencyBuilderImpl implements ContributionDependencyBuilder {
    private final static Logger logger = Logger.getLogger(ContributionDependencyBuilderImpl.class.getName());
    
    private Monitor monitor;
    
    /**
     * Constructs a new ContributionDependencyBuilder.
     */
    public ContributionDependencyBuilderImpl(Monitor monitor) {
               
        this.monitor = monitor;
    }
    
    /**
     * Calculate the set of contributions that a contribution depends on.
     * @param contribution
     * @param workspace
     * @return
     */
    public List<Contribution> buildContributionDependencies(Contribution contribution, Workspace workspace) {
        List<Contribution> dependencies = new ArrayList<Contribution>();
        Set<Contribution> set = new HashSet<Contribution>();

        dependencies.add(contribution);
        set.add(contribution);
        addContributionDependencies(contribution, workspace, dependencies, set);
        
        Collections.reverse(dependencies);
        return dependencies;
    }
    
    /**
     * Analyze a contribution and add its dependencies to the given dependency set.
     * @param contribution
     * @param workspace
     * @param dependencies
     * @param set
     */
    private void addContributionDependencies(Contribution contribution, Workspace workspace, List<Contribution> dependencies, Set<Contribution> set) {
        
        // Go through the contribution imports
        for (Import import_: contribution.getImports()) {
            boolean resolved = false;
            
            // Go through all contribution candidates and their exports
            List<Contribution> matched = new ArrayList<Contribution>();
            Set<Contribution> mset = new HashSet<Contribution>();
            for (Contribution dependency: workspace.getContributions()) {
                for (Export export: dependency.getExports()) {
                    
                    // If an export from a contribution matches the import in hand
                    // add that contribution to the dependency set
                    if (import_.match(export)) {
                        resolved = true;
                        
                        if (!mset.contains(dependency)) {
                            mset.add(dependency);
                            matched.add(dependency);
                        }

                        if (!set.contains(dependency)) {
                            set.add(dependency);
                            dependencies.add(dependency);
                            
                            // Now add the dependencies of that contribution 
                            addContributionDependencies(dependency, workspace, dependencies, set);
                        }
                    }
                }
            }
            
            if (resolved) {
                
                // Initialize the import's model resolver
                import_.setModelResolver(new DefaultImportAllModelResolver(import_, matched));
                
            } else {
                // Record import resolution issue
                Problem problem = new ProblemImpl(this.getClass().getName(), "workspace-validation-messages", Severity.WARNING, import_, "Unresolved import");
                monitor.problem(problem);
            }
        }
    }

}
