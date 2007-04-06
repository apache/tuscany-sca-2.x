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
package org.apache.tuscany.services.spi.contribution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * The default implementation of a StAX artifact processor registry.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultStAXArtifactProcessorRegistry
    extends DefaultArtifactProcessorRegistry<StAXArtifactProcessor<Object>>
    implements StAXArtifactProcessorRegistry, StAXArtifactProcessor<Object> {

    private XMLInputFactory factory;

    /**
     * Constructs a new loader registry.
     * @param assemblyFactory
     * @param policyFactory
     * @param factory
     */
    public DefaultStAXArtifactProcessorRegistry(XMLInputFactory factory) {
        super();
        this.factory = factory;
    }

    public DefaultStAXArtifactProcessorRegistry() {
        this(XMLInputFactory.newInstance());
    }

    public Object read(XMLStreamReader source) throws ContributionReadException {
        
        // Delegate to the processor associated with the element qname
        QName name = source.getName();
        StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)this.getProcessor(name);
        if (processor == null) {
            return null;
        }
        return processor.read(source);
    }
    
    public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException {
        
        // Delegate to the processor associated with the model type
        StAXArtifactProcessor<Object> processor = (StAXArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
        if (processor != null) {
            processor.write(model, outputSource);
        }
    }
    
    public void resolve(Object model, ArtifactResolver resolver) throws ContributionResolveException {

        // Delegate to the processor associated with the model type
        StAXArtifactProcessor<Object> processor = (StAXArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
        if (processor != null) {
            processor.resolve(model, resolver);
        }
    }
    
    public void wire(Object model) throws ContributionWireException {

        // Delegate to the processor associated with the model type
        StAXArtifactProcessor<Object> processor = (StAXArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
        if (processor != null) {
            processor.wire(model);
        }
    }
    
    public <MO> MO read(URL url, Class<MO> type) throws ContributionReadException {
        try {
            XMLStreamReader reader;
            InputStream is;
            is = url.openStream();
            try {
                reader = factory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    QName name = reader.getName();
                    Object mo = read(reader);
                    if (type.isInstance(mo)) {
                        return type.cast(mo);
                    } else {
                        UnrecognizedElementException e = new UnrecognizedElementException(name);
                        e.setResourceURI(url.toString());
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
        } catch (IOException e) {
            ContributionReadException ce = new ContributionReadException(e);
            ce.setResourceURI(url.toString());
            throw ce;
        } catch (XMLStreamException e) {
            throw new InvalidConfigurationException("Invalid or missing resource: " + url.toString(), e);
        }
    }

    public QName getArtifactType() {
        return null;
    }
    
    public Class<Object> getModelType() {
        return null;
    }
}
