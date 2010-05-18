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

package org.apache.tuscany.sca.deployment;

import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A utility that provides system functions to handle Tuscany SCA application deployment
 */
public interface Deployer extends LifeCycleListener {
    /**
     * Check if Schema Validation for XML Documents is enabled
     * @return
     */
    boolean isSchemaValidationEnabled();
    
    /**
     * Sets Schema Validation for XML Documents flag
     * @param schemaValidationEnabled
     */
    void setSchemaValidationEnabled(boolean schemaValidationEnabled);
    
    /**
     * Attach a deployment composite to the given contribution 
     * @param contribution The target contribution
     * @param composite The deployment composite
     * @param appending A flag to indicate if existing deployable composites in the contribution should be appended or replaced
     */
    void attachDeploymentComposite(Contribution contribution, Composite composite, boolean appending);

    /**
     * Configure a list of contributions to create a composite representing a view of the domain
     * @param contributions
     * @param bindingBaseURIs
     * @param monitor
     * @return
     * @throws ContributionResolveException
     * @throws CompositeBuilderException
     */
    Composite build(List<Contribution> contributions, List<Contribution> allContributions, Map<QName, List<String>> bindingBaseURIs, Monitor monitor)
        throws ContributionResolveException, CompositeBuilderException;

    /**
     * Load an artifact from the given location
     * @param uri
     * @param location
     * @param monitor
     * @return
     * @throws ContributionReadException
     */
    Artifact loadArtifact(URI uri, URL location, Monitor monitor) throws ContributionReadException;

    /**
     * Load a contribution from the given location
     * @param uri
     * @param location
     * @param monitor
     * @return
     * @throws ContributionReadException
     */
    Contribution loadContribution(URI uri, URL location, Monitor monitor) throws ContributionReadException;

    /**
     * @param <T>
     * @param uri
     * @param location
     * @param monitor
     * @return
     * @throws ContributionReadException
     */
    <T> T loadDocument(URI uri, URL location, Monitor monitor) throws ContributionReadException;

    /**
     * @param <T>
     * @param reader
     * @param monitor
     * @return
     * @throws XMLStreamException
     * @throws ContributionReadException
     */
    <T> T loadXMLDocument(Reader reader, Monitor monitor) throws XMLStreamException, ContributionReadException;

    /**
     * @param <T>
     * @param location
     * @param monitor
     * @return
     * @throws XMLStreamException
     * @throws ContributionReadException
     */
    <T> T loadXMLDocument(URL location, Monitor monitor) throws XMLStreamException, ContributionReadException;

    /**
     * @param <T>
     * @param reader
     * @param monitor
     * @return
     * @throws ContributionReadException
     * @throws XMLStreamException
     */
    <T> T loadXMLElement(XMLStreamReader reader, Monitor monitor) throws ContributionReadException, XMLStreamException;

    /**
     * Save the model as XML
     * @param model
     * @param writer
     * @param monitor
     * @throws XMLStreamException
     * @throws ContributionWriteException
     */
    void saveXMLDocument(Object model, Writer writer, Monitor monitor) throws XMLStreamException,
        ContributionWriteException;

    /**
     * Save the model as XML
     * @param model
     * @param writer
     * @param monitor
     * @throws XMLStreamException
     * @throws ContributionWriteException
     */
    void saveXMLElement(Object model, XMLStreamWriter writer, Monitor monitor) throws XMLStreamException,
        ContributionWriteException;
    
    /**
     * 
     * @return
     */
    Monitor createMonitor();
    
    /**
     * Create an instance of {@link BuilderContext}
     * @return
     */
    BuilderContext createBuilderContext();

    /**
     * Create an instance of {@link ProcessorContext}
     * @return
     */
    ProcessorContext createProcessorContext();

    /**
     * Get the {@link ExtensionPointRegistry}
     * @return
     */
    ExtensionPointRegistry getExtensionPointRegistry();
    
    /**
     * Get the system definitions   
     */
    Definitions getSystemDefinitions();  
}
