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
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.contribution.service.UnrecognizedElementException;

/**
 * Implementation of an extensible StAX artifact processor.
 * 
 * Takes a StAXArtifactProcessorExtensionPoint and delegates to the proper
 * StAXArtifactProcessor by element QName
 * 
 * @version $Rev$ $Date$
 */
public class ExtensibleStAXArtifactProcessor
    implements StAXArtifactProcessor<Object> {

    private static final Logger logger = Logger.getLogger(ExtensibleStAXArtifactProcessor.class.getName()); 
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessorExtensionPoint processors;

    /**
     * Constructs a new ExtensibleStAXArtifactProcessor.
     * @param processors
     * @param inputFactory
     * @param outputFactory
     */
    public ExtensibleStAXArtifactProcessor(StAXArtifactProcessorExtensionPoint processors, XMLInputFactory inputFactory, XMLOutputFactory outputFactory) {
        super();
        this.processors = processors;
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        if (this.outputFactory != null) {
            this.outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        }
    }

    public Object read(XMLStreamReader source) throws ContributionReadException, XMLStreamException {
        
        // Delegate to the processor associated with the element QName
        int event = source.getEventType();
        if (event == XMLStreamConstants.START_DOCUMENT) {
            source.nextTag();
        }
        QName name = source.getName();
        StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)processors.getProcessor(name);
        if (processor == null) {
            if (logger.isLoggable(Level.WARNING)) {
                Location location = source.getLocation();
                logger.warning("Element " + name + " cannot be processed. (" + location + ")");
            }
            return null;
        }
        return processor.read(source);
    }
    
    @SuppressWarnings("unchecked")
    public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
        
        // Delegate to the processor associated with the model type
        if (model != null) {
            StAXArtifactProcessor processor = processors.getProcessor(model.getClass());
            if (processor != null) {
                processor.write(model, outputSource);
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("No StAX processor is configured to handle " + model.getClass());
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void resolve(Object model, ModelResolver resolver) throws ContributionResolveException {

        // Delegate to the processor associated with the model type
        if (model != null) {
            StAXArtifactProcessor processor = processors.getProcessor(model.getClass());
            if (processor != null) {
                processor.resolve(model, resolver);
            }
        }
    }
    
    /**
     * Read a model from an InputStream.
     * @param is The artifact InputStream
     * @param type Model type
     * @return The model
     * @throws ContributionReadException
     */
    public <M> M read(InputStream is, Class<M> type) throws ContributionReadException {
        try {
            XMLStreamReader reader;
            try {
                reader = inputFactory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    QName name = reader.getName();
                    Object mo = read(reader);
                    if (type.isInstance(mo)) {
                        return type.cast(mo);
                    } else {
                        UnrecognizedElementException e = new UnrecognizedElementException(name);
                        throw e;
                    }
                } catch (ContributionReadException e) {
                    Location location = reader.getLocation();
                    e.setLine(location.getLineNumber());
                    e.setColumn(location.getColumnNumber());
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
            throw ce;
        }
    }

    /**
     * Write a model to an OutputStream.
     * @param model
     * @param os
     * @throws ContributionWriteException
     */
    public void write(Object model, OutputStream os) throws ContributionWriteException {
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(os);
            write(model, writer);
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public QName getArtifactType() {
        return null;
    }
    
    public Class<Object> getModelType() {
        return null;
    }
}
