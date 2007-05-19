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

package org.apache.tuscany.sca.contribution.service;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * Loader responsible to process the contribution metadata loader 
 * 
 * @version $Rev$ $Date$
 */
public interface ContributionMetadataLoader {
    /**
     * Create the model object for Contribution Service metadata information
     * 
     * @param contribution The contribution to populate with the proper values from the side file
     * @param reader The XML stream reader positioned on the applicable START_ELEMENT
     * @throws XMLStreamException
     * @throws ContributionMetadataReadException
     */
    void load(Contribution contribution, XMLStreamReader reader) throws XMLStreamException, ContributionMetadataLoaderException;
}