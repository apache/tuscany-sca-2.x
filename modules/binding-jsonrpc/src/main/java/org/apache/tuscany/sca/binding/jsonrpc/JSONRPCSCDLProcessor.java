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

package org.apache.tuscany.sca.binding.jsonrpc;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;


/**
 * A processor for <binding.jsonrpc> SCDL elements.
 */
public class JSONRPCSCDLProcessor implements StAXArtifactProcessor<JSONRPCBinding> {

    public static final QName JSONRPC_BINDING_QN = new QName(SCA_NS, "binding.jsonrpc");
    
    public QName getArtifactType() {
        return JSONRPC_BINDING_QN;
    }

    public Class<JSONRPCBinding> getModelType() {
        return JSONRPCBinding.class;
    }

    public JSONRPCBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        JSONRPCBinding JSONRPCBinding = new JSONRPCBinding();

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && JSONRPC_BINDING_QN.equals(reader.getName())) {
                break;
            }
        }

        return JSONRPCBinding;
    }

    public void write(JSONRPCBinding model, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        writer.writeStartElement(Constants.SCA10_NS, JSONRPC_BINDING_QN.getLocalPart());
        writer.writeEndElement();
    }

    public void resolve(JSONRPCBinding model, ModelResolver resolver) throws ContributionResolveException {
    }

}
