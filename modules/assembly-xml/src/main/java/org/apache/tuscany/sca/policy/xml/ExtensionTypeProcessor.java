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

package org.apache.tuscany.sca.policy.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Processor for handling XML models of ExtensionType meta data definitions
 *
 * @version $Rev$ $Date$
 */
abstract class ExtensionTypeProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<ExtensionType>, PolicyConstants {

    private PolicyFactory policyFactory;
    private Monitor monitor;

    protected abstract ExtensionType resolveExtensionType(ExtensionType extnType, ModelResolver resolver)
        throws ContributionResolveException;

    public ExtensionTypeProcessor(PolicyFactory policyFactory,
                                  StAXArtifactProcessor<Object> extensionProcessor,
                                  Monitor monitor) {
        this.policyFactory = policyFactory;
        this.monitor = monitor;
    }

    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      Messages.RESOURCE_BUNDLE,
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    public ExtensionType read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        QName extType = getArtifactType();
        QName type = getQName(reader, "type");

        if (type != null) {
            ExtensionType extensionType = null;
            if (BINDING_TYPE_QNAME.equals(extType)) {
                extensionType = policyFactory.createBindingType();
            } else if (IMPLEMENTATION_TYPE_QNAME.equals(extType)) {
                extensionType = policyFactory.createImplementationType();
            } else {
                error("UnrecognizedExtensionType", reader, type);
                return null;
                //throw new ContributionReadException("Unrecognized ExtensionType - " + type);
            }
            extensionType.setType(type);
            extensionType.setUnresolved(true);

            readAlwaysProvidedIntents(extensionType, reader);
            readMayProvideIntents(extensionType, reader);
            return extensionType;

        } else {
            error("RequiredAttributeMissing", reader, extType);
            //throw new ContributionReadException("Required attribute '" + TYPE + 
            //"' missing from BindingType Definition");
        }
        return null;
    }

    private void readAlwaysProvidedIntents(ExtensionType extnType, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, ALWAYS_PROVIDES);
        if (value != null) {
            List<Intent> alwaysProvided = extnType.getAlwaysProvidedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                alwaysProvided.add(intent);
            }
        }
    }

    private void readMayProvideIntents(ExtensionType extnType, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, MAY_PROVIDE);
        if (value != null) {
            List<Intent> mayProvide = extnType.getMayProvidedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                mayProvide.add(intent);
            }
        }
    }

    public void write(ExtensionType extnType, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {

        // Write an <sca:bindingType or sca:implementationType>
        if (extnType instanceof BindingType) {
            writer.writeStartElement(SCA11_NS, BINDING_TYPE);
        } else if (extnType instanceof ImplementationType) {
            writer.writeStartElement(SCA11_NS, IMPLEMENTATION_TYPE);
        }

        writeAlwaysProvidesIntentsAttribute(extnType, writer);
        writeMayProvideIntentsAttribute(extnType, writer);

        writer.writeEndElement();
    }

    private void writeMayProvideIntentsAttribute(ExtensionType extnType, XMLStreamWriter writer)
        throws XMLStreamException {
        StringBuffer sb = new StringBuffer();
        for (Intent intent : extnType.getMayProvidedIntents()) {
            writer.writeNamespace(intent.getName().getPrefix(), intent.getName().getNamespaceURI());
            sb.append(intent.getName().getPrefix() + COLON + intent.getName().getLocalPart());
            sb.append(WHITE_SPACE);
        }

        if (sb.length() > 0) {
            writer.writeAttribute(MAY_PROVIDE, sb.toString());
        }
    }

    private void writeAlwaysProvidesIntentsAttribute(ExtensionType extnType, XMLStreamWriter writer)
        throws XMLStreamException {
        StringBuffer sb = new StringBuffer();
        for (Intent intent : extnType.getAlwaysProvidedIntents()) {
            writer.writeNamespace(intent.getName().getPrefix(), intent.getName().getNamespaceURI());
            sb.append(intent.getName().getPrefix() + COLON + intent.getName().getLocalPart());
            sb.append(WHITE_SPACE);
        }

        if (sb.length() > 0) {
            writer.writeAttribute(ALWAYS_PROVIDES, sb.toString());

        }
    }

    public void resolve(ExtensionType extnType, ModelResolver resolver) throws ContributionResolveException {

        if (extnType != null && extnType.isUnresolved()) {
            resolveAlwaysProvidedIntents(extnType, resolver);
            resolveMayProvideIntents(extnType, resolver);
            extnType.setUnresolved(false);
            //resolveExtensionType(extnType, resolver);
        }
    }

    private void resolveAlwaysProvidedIntents(ExtensionType extensionType, ModelResolver resolver)
        throws ContributionResolveException {
        if (extensionType != null) {
            // resolve all provided intents
            List<Intent> alwaysProvided = new ArrayList<Intent>();
            for (Intent providedIntent : extensionType.getAlwaysProvidedIntents()) {
                if (providedIntent.isUnresolved()) {
                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
                    if (!providedIntent.isUnresolved()) {
                        alwaysProvided.add(providedIntent);
                    } else {
                        error("AlwaysProvidedIntentNotFound", resolver, providedIntent, extensionType);
                        //throw new ContributionResolveException("Always Provided Intent - " + providedIntent
                        //+ " not found for ExtensionType "
                        //+ extensionType);
                    }
                } else {
                    alwaysProvided.add(providedIntent);
                }
            }
            extensionType.getAlwaysProvidedIntents().clear();
            extensionType.getAlwaysProvidedIntents().addAll(alwaysProvided);
        }
    }

    private void resolveMayProvideIntents(ExtensionType extensionType, ModelResolver resolver)
        throws ContributionResolveException {
        if (extensionType != null) {
            // resolve all provided intents
            List<Intent> mayProvide = new ArrayList<Intent>();
            for (Intent providedIntent : extensionType.getMayProvidedIntents()) {
                if (providedIntent.isUnresolved()) {
                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
                    if (!providedIntent.isUnresolved()) {
                        mayProvide.add(providedIntent);
                    } else {
                        error("MayProvideIntentNotFound", resolver, providedIntent, extensionType);
                        //throw new ContributionResolveException("May Provide Intent - " + providedIntent
                        //+ " not found for ExtensionType "
                        //+ extensionType);
                    }
                } else {
                    mayProvide.add(providedIntent);
                }
            }
            extensionType.getMayProvidedIntents().clear();
            extensionType.getMayProvidedIntents().addAll(mayProvide);
        }
    }

    public Class<ExtensionType> getModelType() {
        return ExtensionType.class;
    }
}
