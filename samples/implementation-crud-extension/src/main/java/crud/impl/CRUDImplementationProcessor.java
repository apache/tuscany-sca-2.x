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
package crud.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

import crud.model.CRUDImplementation;
import crud.model.CRUDImplementationFactory;

/**
 * Implements a STAX artifact processor for CRUD implementations.
 * 
 * The artifact processor is responsible for processing <implementation.crud>
 * elements in SCA assembly XML composite files and populating the CRUD
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML. 
 */
public class CRUDImplementationProcessor implements StAXArtifactProcessor<CRUDImplementation> {
    private static final QName IMPLEMENTATION_CRUD = new QName("http://crud", "implementation.crud");
    
    private CRUDImplementationFactory crudFactory;
    
    public CRUDImplementationProcessor(CRUDImplementationFactory crudFactory) {
        this.crudFactory = crudFactory;
    }

    public QName getArtifactType() {
        // Returns the qname of the XML element processed by this processor
        return IMPLEMENTATION_CRUD;
    }

    public Class<CRUDImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return CRUDImplementation.class;
    }

    public CRUDImplementation read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPLEMENTATION_CRUD.equals(reader.getName());
        
        // Read an <implementation.crud> element
        try {
            // Read the directory attribute. This is where the sample
            // CRUD implementation will persist resources.
            String directory = reader.getAttributeValue(null, "directory");

            // Create an initialize the CRUD implementation model
            CRUDImplementation implementation = crudFactory.createCRUDImplementation();
            implementation.setDirectory(directory);
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_CRUD.equals(reader.getName())) {
                    break;
                }
            }
            
            return implementation;
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(CRUDImplementation impl, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(CRUDImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
