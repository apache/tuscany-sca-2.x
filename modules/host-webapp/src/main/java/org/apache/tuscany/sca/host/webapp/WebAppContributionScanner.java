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

package org.apache.tuscany.sca.host.webapp;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;

/**
 * 
 */
public class WebAppContributionScanner implements ContributionScanner {
    private ServletContext servletContext;
    private ContributionFactory contributionFactory;

    public WebAppContributionScanner(ExtensionPointRegistry registry) {
        super();
        this.servletContext = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(ServletContext.class);
        this.contributionFactory =
            registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(ContributionFactory.class);
    }

    public String getContributionType() {
        return PackageType.WAR;
    }

    public List<Artifact> scan(Contribution contribution) throws ContributionReadException {
        try {
            List<Artifact> artifacts = new ArrayList<Artifact>();
            URL location = new URL(contribution.getLocation());
            URL root = servletContext.getResource("/");
            URI relative = root.toURI().relativize(location.toURI());
            String path = relative.getPath();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            for (Object file : servletContext.getResourcePaths(path)) {
                Artifact artifact = contributionFactory.createArtifact();
                String name = (String)file;
                // Remove leading /
                name = name.substring(1);
                artifact.setURI(name);
                URL artifactURL = new URL(location, name);
                artifact.setLocation(artifactURL.toString());
                artifacts.add(artifact);
            }
            return artifacts;
        } catch (Exception e) {
            throw new ContributionReadException(e);
        }
    }

}
