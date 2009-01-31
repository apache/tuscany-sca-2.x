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

package manager;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * Sample ListDeployables task.
 *
 * This sample shows how to use a subset of Tuscany to read contribution
 * metadata.
 * 
 * The sample reads the SCA metadata for two sample contributions then
 * prints the names of their deployable composites.
 *
 * @version $Rev$ $Date$
 */
public class ListDeployables {
    
    private static URLArtifactProcessor<Contribution> contributionProcessor;

    private static void init() {
        
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Create contribution info processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);
    }

    public static void main(String[] args) throws Exception {
        init();

        // Read the contribution info for the sample contribution
        URI uri = URI.create("store");
        URL url = new File("./target/sample-domain-management-store.jar").toURI().toURL();
        Contribution contribution = contributionProcessor.read(null, uri, url);
        
        // List the deployables in the contribution
        for (Composite deployable: contribution.getDeployables()) {
            System.out.println("Deployable: " + deployable.getName());
        }
        
    }
    
}
