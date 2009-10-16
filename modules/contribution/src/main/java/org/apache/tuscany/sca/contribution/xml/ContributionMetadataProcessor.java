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
package org.apache.tuscany.sca.contribution.xml;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Processor for contribution metadata
 *
 * @version $Rev$ $Date$
 */
public class ContributionMetadataProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<ContributionMetadata> {
    private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200903";

    private static final QName CONTRIBUTION_QNAME = new QName(SCA11_NS, "contribution");
    private static final QName DEPLOYABLE_QNAME = new QName(SCA11_NS, "deployable");

    private final AssemblyFactory assemblyFactory;
    private final ContributionFactory contributionFactory;
    private final StAXArtifactProcessor<Object> extensionProcessor;
    private final StAXAttributeProcessor<Object> attributeProcessor;
    

    public ContributionMetadataProcessor(FactoryExtensionPoint modelFactories,
                                         StAXArtifactProcessor<Object> extensionProcessor,
                                         StAXAttributeProcessor<Object> attributeProcessor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        this.extensionProcessor = extensionProcessor;
        this.attributeProcessor = attributeProcessor;
    }

    /**
     * Report a error.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "contribution-xml-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(), "contribution-xml-validation-messages", Severity.ERROR,
                                model, message, ex);
            monitor.problem(problem);
        }
    }

    public QName getArtifactType() {
        return CONTRIBUTION_QNAME;
    }

    public Class<ContributionMetadata> getModelType() {
        return ContributionMetadata.class;
    }

    public ContributionMetadata read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException {
        ContributionMetadata contribution = null;
        QName name = null;

        try {
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        name = reader.getName();

                        if (CONTRIBUTION_QNAME.equals(name)) {

                            // Read <contribution>
                            contribution = this.contributionFactory.createContributionMetadata();
                            contribution.setUnresolved(true);
                            readExtendedAttributes(reader, contribution, attributeProcessor, assemblyFactory, context);

                        } else if (DEPLOYABLE_QNAME.equals(name)) {

                            // Read <deployable>
                            QName compositeName = getQName(reader, "composite");
                            if (compositeName == null) {
                                error(context.getMonitor(), "AttributeCompositeMissing", reader);
                                //throw new ContributionReadException("Attribute 'composite' is missing");
                            } else {
                                if (contribution != null) {
                                    Composite composite = assemblyFactory.createComposite();
                                    composite.setName(compositeName);
                                    composite.setUnresolved(true);
                                    contribution.getDeployables().add(composite);
                                }
                            }
                        } else {

                            // Read an extension element
                            Object extension = extensionProcessor.read(reader, context);
                            if (extension != null && contribution != null) {
                                if (extension instanceof Import) {
                                    contribution.getImports().add((Import)extension);
                                } else if (extension instanceof Export) {
                                    contribution.getExports().add((Export)extension);
                                } else {
                                    contribution.getExtensions().add(extension);
                                }
                            }
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        if (CONTRIBUTION_QNAME.equals(reader.getName())) {
                            return contribution;
                        }
                        break;
                }

                //Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
        } catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            error(context.getMonitor(), "XMLStreamException", reader, ex);
        }

        return contribution;
    }

    public void write(ContributionMetadata contribution, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {

        // Write <contribution>
        writeStartDocument(writer, CONTRIBUTION_QNAME.getNamespaceURI(), CONTRIBUTION_QNAME.getLocalPart());
        writeExtendedAttributes(writer, contribution, attributeProcessor, context);

        // Write <import>
        for (Import imp : contribution.getImports()) {
            extensionProcessor.write(imp, writer, context);
        }

        // Write <export>
        for (Export export : contribution.getExports()) {
            extensionProcessor.write(export, writer, context);
        }

        // Write <deployable>
        for (Composite deployable : contribution.getDeployables()) {
            writeStart(writer,
                       DEPLOYABLE_QNAME.getNamespaceURI(),
                       DEPLOYABLE_QNAME.getLocalPart(),
                       new XAttr("composite", deployable.getName()));
            writeEnd(writer);
        }

        writeEndDocument(writer);
    }

    public void resolve(ContributionMetadata contribution, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        // Resolve imports and exports
        for (Export export : contribution.getExports()) {
            extensionProcessor.resolve(export, resolver, context);
        }
        for (Import import_ : contribution.getImports()) {
            extensionProcessor.resolve(import_, resolver, context);
        }

        // Resolve deployable composites
        List<Composite> deployables = contribution.getDeployables();
        for (int i = 0, n = deployables.size(); i < n; i++) {
            Composite deployable = deployables.get(i);
            Composite resolved = (Composite)resolver.resolveModel(Composite.class, deployable, context);
            if (resolved != deployable) {
                deployables.set(i, resolved);
            }
        }

        for (Object ext : contribution.getExtensions()) {
            extensionProcessor.resolve(ext, resolver, context);
        }

        contribution.setUnresolved(false);
    }
}
