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

package org.apache.tuscany.sca.definitions.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Default implementation of an extension point for XML definitionss.
 *
 * @version $Rev: 758911 $ $Date: 2009-03-26 15:52:27 -0700 (Thu, 26 Mar 2009) $
 */
public class DefaultDefinitionsExtensionPoint implements DefinitionsExtensionPoint {
    private static final Logger logger = Logger.getLogger(DefaultDefinitionsExtensionPoint.class.getName());
    private static final URI DEFINITIONS_URI = URI.create("META-INF/definitions.xml");
    private ExtensionPointRegistry registry;
    private List<URL> documents = new ArrayList<URL>();
    private List<Definitions> definitions = new ArrayList<Definitions>();
    private boolean documentsLoaded;
    private boolean loaded;

    public DefaultDefinitionsExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public void addDefinitionsDocument(URL url) {
        documents.add(url);
    }

    public void removeDefinitionsDocument(URL url) {
        documents.remove(url);
    }

    /**
     * Load definitions declarations from META-INF/services/
     * org.apache.tuscany.sca.contribution.processor.Definitions files
     */
    private synchronized void loadDefinitionsDocuments() {
        if (documentsLoaded)
            return;

        // Get the definitions declarations
        Collection<ServiceDeclaration> definitionsDeclarations;
        try {
            definitionsDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(DEFINITIONS_FILE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Find each definitions
        for (ServiceDeclaration definitionsDeclaration : definitionsDeclarations) {
            URL url = definitionsDeclaration.getResource(definitionsDeclaration.getClassName());
            if (url == null) {
                throw new IllegalArgumentException(new FileNotFoundException(definitionsDeclaration.getClassName()));
            }
            documents.add(url);
        }

        documentsLoaded = true;
    }

    public synchronized List<Definitions> getDefinitions() {
        if (!loaded) {
            loadDefinitionsDocuments();
            URLArtifactProcessorExtensionPoint processors =
                registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            URLArtifactProcessor<Definitions> processor = processors.getProcessor(Definitions.class);
            for (URL url : documents) {
                Definitions def;
                try {
                    def = processor.read(null, DEFINITIONS_URI, url);
                    definitions.add(def);
                } catch (ContributionReadException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            loaded = true;
        }
        return definitions;
    }

    public void addDefinitions(Definitions def) {
        this.definitions.add(def);
    }

    public void removeDefinitions(Definitions def) {
        this.definitions.remove(def);
    }
}
