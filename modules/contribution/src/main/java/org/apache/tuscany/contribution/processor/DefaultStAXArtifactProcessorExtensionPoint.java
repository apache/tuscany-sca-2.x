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
package org.apache.tuscany.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.contribution.service.UnrecognizedElementException;

/**
 * The default implementation of a StAX artifact processor registry.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultStAXArtifactProcessorExtensionPoint
    extends DefaultArtifactProcessorExtensionPoint
    implements StAXArtifactProcessorExtensionPoint, StAXArtifactProcessor<Object> {

    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;

    /**
     * Constructs a new loader registry.
     * @param assemblyFactory
     * @param policyFactory
     * @param inputFactory
     */
    public DefaultStAXArtifactProcessorExtensionPoint(XMLInputFactory inputFactory, XMLOutputFactory outputFactory) {
        super();
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
    }

    public DefaultStAXArtifactProcessorExtensionPoint() {
        this(XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
    }

    public Object read(XMLStreamReader source) throws ContributionReadException {
        
        // Delegate to the processor associated with the element qname
        QName name = source.getName();
        StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)this.getProcessor(name);
        if (processor == null) {
            return null;
        }
        try {
            return processor.read(source);
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException {
        
        // Delegate to the processor associated with the model type
        if (model != null) {
            StAXArtifactProcessor<Object> processor = 
                (StAXArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
            if (processor != null) {
                try {
                    processor.write(model, outputSource);
                } catch (XMLStreamException e) {
                    throw new ContributionWriteException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void resolve(Object model, ArtifactResolver resolver) throws ContributionResolveException {

        // Delegate to the processor associated with the model type
        if (model != null) {
            StAXArtifactProcessor<Object> processor = 
                (StAXArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
            if (processor != null) {
                processor.resolve(model, resolver);
            }
        }
    }
    
    /**
     * Read a model from an input stream.
     * @param is
     * @param type
     * @return
     * @throws ContributionReadException
     */
    public <MO> MO read(InputStream is, Class<MO> type) throws ContributionReadException {
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
     * Write a model to an ouput stream.
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

    public void addArtifactProcessor(StAXArtifactProcessor artifactProcessor) {
        processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
    }
    
    public void removeArtifactProcessor(StAXArtifactProcessor artifactProcessor) {
        processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        processorsByModelType.remove(artifactProcessor.getModelType());        
    }

    public QName getArtifactType() {
        return null;
    }
    
    public Class<Object> getModelType() {
        return null;
    }
}
