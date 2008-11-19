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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * An artifact processor that can read models from a StAX XMLStreamReader.
 * 
 * @version $Rev$ $Date$
 */
public interface StAXArtifactProcessor<M> extends ArtifactProcessor<M> {

    /**
     * Reads a model from an XMLStreamReader.
     * 
     * @param reader The XMLStreamReader
     * @return A model representation of the input.
     */
    M read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException;
    
    /**
     * Writes a model to an XMLStreamWriter.
     * 
     * @param model A model representing the source
     * @param writer The XML stream writer
     * @throws ContributionWriteException
     * @throws XMLStreamException
     */
    void write(M model, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException;
    
    /**
     * Returns the type of artifact handled by this artifact processor.
     *  
     * @return The type of artifact handled by this artifact processor
     */
    QName getArtifactType();
}
