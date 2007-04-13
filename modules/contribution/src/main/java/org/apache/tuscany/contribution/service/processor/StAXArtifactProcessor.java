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

package org.apache.tuscany.contribution.service.processor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionWriteException;

/**
 * An artifact processor that can read models from a StAX XMLStreamReader.
 * 
 * @version $Rev: 522653 $ $Date: 2007-03-26 15:30:21 -0700 (Mon, 26 Mar 2007) $
 */
public interface StAXArtifactProcessor<M> extends ArtifactProcessor<M> {

    /**
     * Reads a model from an input source. Examples of input sources are: a URI, a
     * DOM node, an XML reader.
     * @param source
     * @return a model representation of the input.
     */
    M read(XMLStreamReader inputSource) throws ContributionReadException;
    
    /**
     * Writes a model to an ouput source. Examples of output sources are: a URI, a
     * DOM node, an XML writer.
     * @param source
     * @return a model representation of the source.
     */
    void write(M model, XMLStreamWriter outputSource) throws ContributionWriteException;
    
    /**
     * Returns the type of artifact handled by this artifact processor. 
     * @return the type of artifact handled by this artifact processor
     */
    QName getArtifactType();

}
