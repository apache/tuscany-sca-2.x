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
package org.apache.tuscany.container.crud;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;

public class CRUDImplementationLoader implements StAXArtifactProcessorExtension<CRUDImplementation> {
    public static final QName IMPLEMENTATION_CRUD = new QName(SCA_NS, "implementation.crud");

    public QName getArtifactType() {
        return IMPLEMENTATION_CRUD;
    }

    public Class<CRUDImplementation> getModelType() {
        return CRUDImplementation.class;
    }

    public void optimize(CRUDImplementation impl) throws ContributionException {
    }

    public CRUDImplementation read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPLEMENTATION_CRUD.equals(reader.getName());
        try {
            String dir = reader.getAttributeValue(null, "directory");

            CRUDImplementation implementation = new CRUDImplementation(dir);
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

    public void resolve(CRUDImplementation impl, ArtifactResolver resolver) throws ContributionResolveException {
    }

    public void wire(CRUDImplementation model) throws ContributionWireException {
    }

    public void write(CRUDImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
