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

package org.apache.tuscany.sca.workspace.dependency.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.workspace.Workspace;

/**
 * A contribution dependency analyzer.
 *
 * @version $Rev$ $Date$
 */
public class ContributionDependencyAnalyzer {
    
    /**
     * Constructs a new WorkspaceDependencyAnalyzer.
     */
    public ContributionDependencyAnalyzer() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Calculate the set of contributions that a contribution depends on.
     * @param workspace
     * @param contribution
     * @return
     */
    public Set<Contribution> calculateContributionDependencies(Workspace workspace, Contribution contribution) {
        Set<Contribution> dependencies = new HashSet<Contribution>();

        addContributionDependencies(workspace, contribution, dependencies);
        dependencies.remove(contribution);
        
        return dependencies;
    }
    
    /**
     * Analyze a contribution and add its dependencies to the given dependency set.
     * @param workspace
     * @param contribution
     * @param dependencies
     */
    private void addContributionDependencies(Workspace workspace, Contribution contribution, Set<Contribution> dependencies) {
        
        // Go through the contribution imports
        for (Import import_: contribution.getImports()) {
            
            // Go through all contribution candidates and their exports
            for (Contribution dependency: workspace.getContributions()) {
                for (Export export: dependency.getExports()) {
                    
                    // If an export from a contribution matches the import in hand
                    // add that contribution to the dependency set
                    if (import_.match(export)) {
                        if (!dependencies.contains(dependency)) {
                            dependencies.add(dependency);
                            
                            // Now add the dependencies of that contribution
                            addContributionDependencies(workspace, dependency, dependencies);
                        }
                    }
                }
            }
        }
    }

}
