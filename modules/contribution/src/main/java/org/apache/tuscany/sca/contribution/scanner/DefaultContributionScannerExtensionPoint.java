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

package org.apache.tuscany.sca.contribution.scanner;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;

/**
 * Default implementation of a contribution scanner extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultContributionScannerExtensionPoint implements ContributionScannerExtensionPoint {

    private Map<String, ContributionScanner> scanners = new HashMap<String, ContributionScanner>();
    private boolean loaded;
    private ExtensionPointRegistry registry;

    public DefaultContributionScannerExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public void addContributionScanner(ContributionScanner scanner) {
        scanners.put(scanner.getContributionType(), scanner);
    }

    public void removeContributionScanner(ContributionScanner scanner) {
        scanners.remove(scanner.getContributionType());
    }

    public ContributionScanner getContributionScanner(String contentType) {
        loadScanners();
        return scanners.get(contentType);
    }

    private synchronized void loadScanners() {
        if (loaded)
            return;

        // Get the scanner service declarations
        Collection<ServiceDeclaration> scannerDeclarations;
        try {
            scannerDeclarations = registry.getServiceDiscovery().getServiceDeclarations(ContributionScanner.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration scannerDeclaration: scannerDeclarations) {
            Map<String, String> attributes = scannerDeclaration.getAttributes();

            // Load a URL artifact scanner
            String contributionType = attributes.get("type");

            // Create a scanner wrapper and register it
            ContributionScanner scanner = new LazyContributionScanner(contributionType, scannerDeclaration);
            addContributionScanner(scanner);
        }

        loaded = true;
    }

    /**
     * A facade for contribution scanners.
     */
    private static class LazyContributionScanner implements ContributionScanner {

        private ServiceDeclaration scannerDeclaration;
        private String contributionType;
        private ContributionScanner scanner;

        private LazyContributionScanner(String contributionType, ServiceDeclaration scannerDeclaration) {
            this.scannerDeclaration = scannerDeclaration;
            this.contributionType = contributionType;
        }

        public URL getArtifactURL(Contribution contributionSourceURL, String artifact) throws ContributionReadException {
            return getScanner().getArtifactURL(contributionSourceURL, artifact);
        }

        public List<String> scan(Contribution contributionSourceURL) throws ContributionReadException {
            return getScanner().scan(contributionSourceURL);
        }

        public String getContributionType() {
            return contributionType;
        }

        private ContributionScanner getScanner() {
            if (scanner == null) {
                try {
                    Class<ContributionScanner> scannerClass = (Class<ContributionScanner>)scannerDeclaration.loadClass();
                    Constructor<ContributionScanner> constructor = scannerClass.getConstructor();
                    scanner = constructor.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return scanner;
        }
    }
}
