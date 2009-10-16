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
package org.apache.tuscany.sca.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Implementation of an extensible StAX artifact processor.
 *
 * Takes a StAXArtifactProcessorExtensionPoint and delegates to the proper
 * StAXArtifactProcessor by element QName
 *
 * @version $Rev$ $Date$
 */
public class ExtensibleStAXArtifactProcessor implements StAXArtifactProcessor<Object> {
    private static final Logger logger = Logger.getLogger(ExtensibleStAXArtifactProcessor.class.getName());
    private static final String XMLSCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    public static final QName ANY_ELEMENT = new QName(XMLSCHEMA_NS, "any");

    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessorExtensionPoint processors;
    

    /**
     * Constructs a new ExtensibleStAXArtifactProcessor.
     * @param processors
     * @param inputFactory
     * @param outputFactory
     */
    public ExtensibleStAXArtifactProcessor(StAXArtifactProcessorExtensionPoint processors,
                                           XMLInputFactory inputFactory,
                                           XMLOutputFactory outputFactory) {
        super();
        this.processors = processors;
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        if (this.outputFactory != null) {
            this.outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        }
    }
    
    public ExtensibleStAXArtifactProcessor(ExtensionPointRegistry registry) {
        super();
        this.processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.inputFactory = factories.getFactory(XMLInputFactory.class);
        this.outputFactory = factories.getFactory(XMLOutputFactory.class);
        if (this.outputFactory != null) {
            this.outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        }
    }

    /**
     * Report a warning.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void warning(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "contribution-validation-messages",
                                      Severity.WARNING,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
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
                                      "contribution-validation-messages",
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
                monitor.createProblem(this.getClass().getName(),
                                      "contribution-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }

    public Object read(XMLStreamReader source, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        Monitor monitor = context.getMonitor();
        // Delegate to the processor associated with the element QName
        int event = source.getEventType();
        if (event == XMLStreamConstants.START_DOCUMENT) {
            source.nextTag();
        }
        QName name = source.getName();
        StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)processors.getProcessor(name);
        if (processor == null) {
            Location location = source.getLocation();
            error(monitor, "ElementCannotBeProcessed", processors, name, location);

            StAXArtifactProcessor anyElementProcessor = processors.getProcessor(ANY_ELEMENT);
            if (anyElementProcessor != null) {
                return anyElementProcessor.read(source, context);
            } else {
                return null;
            }
        }
        return processor.read(source, context);
    }

    @SuppressWarnings("unchecked")
    public void write(Object model, XMLStreamWriter outputSource, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        Monitor monitor = context.getMonitor();
        // Delegate to the processor associated with the model type
        if (model != null) {
            StAXArtifactProcessor processor = processors.getProcessor(model.getClass());
            if (processor != null) {
                processor.write(model, outputSource, context);
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("No StAX processor is configured to handle " + model.getClass());
                }
                if (!XMLStreamReader.class.isInstance(model)) {
                    warning(monitor, "NoStaxProcessor", processors, model.getClass());
                }
                StAXArtifactProcessor anyElementProcessor = processors.getProcessor(ANY_ELEMENT);
                if (anyElementProcessor != null) {
                    anyElementProcessor.write(model, outputSource, context);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void resolve(Object model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        // Delegate to the processor associated with the model type
        if (model != null) {
            StAXArtifactProcessor processor = processors.getProcessor(model.getClass());
            if (processor != null) {
                processor.resolve(model, resolver, context);
            }
        }
    }

    /**
     * Read a model from an InputStream.
     * @param is The artifact InputStream
     * @param type Model type
     * @param context TODO
     * @return The model
     * @throws ContributionReadException
     */
    public <M> M read(InputStream is, Class<M> type, ProcessorContext context) throws ContributionReadException {
        Monitor monitor = context.getMonitor();
        try {
            XMLStreamReader reader;
            try {
                reader = inputFactory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    QName name = reader.getName();
                    Object mo = read(reader, context);
                    if (type.isInstance(mo)) {
                        return type.cast(mo);
                    } else {
                        error(monitor, "UnrecognizedElementException", reader, name);
                        UnrecognizedElementException e = new UnrecognizedElementException(name);
                        throw e;
                    }
                } catch (ContributionReadException e) {
                    Location location = reader.getLocation();
                    e.setLine(location.getLineNumber());
                    e.setColumn(location.getColumnNumber());
                    error(monitor, "ContributionReadException", reader, e);
                    throw e;
                } finally {
                    try {
                        reader.close();
                    } catch (XMLStreamException e) {
                        // ignore
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (XMLStreamException e) {
            ContributionReadException ce = new ContributionReadException(e);
            error(monitor, "ContributionReadException", inputFactory, ce);
            throw ce;
        }
    }

    /**
     * Write a model to an OutputStream.
     * @param model
     * @param os
     * @param context 
     * @throws ContributionWriteException
     */
    public void write(Object model, OutputStream os, ProcessorContext context) throws ContributionWriteException {
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(os);
            write(model, writer, context);
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            ContributionWriteException cw = new ContributionWriteException(e);
            error(context.getMonitor(), "ContributionWriteException", outputFactory, cw);
            throw cw;
        }
    }

    public QName getArtifactType() {
        return null;
    }

    public Class<Object> getModelType() {
        return null;
    }
}
