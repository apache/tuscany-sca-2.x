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

package org.apache.tuscany.binding.jsonrpc.impl;

import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

import org.apache.tuscany.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.binding.jsonrpc.JSONRPCBindingFactory;

/**
 * A processor for <binding.jsonrpc> elements.
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingProcessor implements StAXArtifactProcessor<JSONRPCBinding> {

    private QName BINDING_JSONRPC = new QName(SCA_NS, "binding.jsonrpc");
    
    private final JSONRPCBindingFactory factory;

    public JSONRPCBindingProcessor(JSONRPCBindingFactory factory) {
        this.factory = factory;
    }

    public QName getArtifactType() {
        return BINDING_JSONRPC;
    }

    public Class<JSONRPCBinding> getModelType() {
        return JSONRPCBinding.class;
    }

    public JSONRPCBinding read(XMLStreamReader reader) throws ContributionReadException {
        String uri = reader.getAttributeValue(null, "uri");
        JSONRPCBinding JSONRPCBinding = factory.createJSONRPCBinding();
        if (uri != null) {
            JSONRPCBinding.setURI(uri.trim());
        }
        return JSONRPCBinding;
    }

    public void write(JSONRPCBinding JSONRPCBinding, XMLStreamWriter writer) throws ContributionWriteException {
    }

    public void resolve(JSONRPCBinding JSONRPCBinding, ModelResolver resolver) throws ContributionResolveException {
    }

}
