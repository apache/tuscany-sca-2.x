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

package echo.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

import echo.EchoBinding;
import echo.EchoBindingFactory;

/**
 * A processor for <binding.echo> elements.
 */
public class EchoBindingProcessor implements StAXArtifactProcessor<EchoBinding> {

    private QName BINDING_ECHO = new QName("http://echo", "binding.echo");
    
    private final EchoBindingFactory factory;

    public EchoBindingProcessor(EchoBindingFactory factory) {
        this.factory = factory;
    }

    public QName getArtifactType() {
        return BINDING_ECHO;
    }

    public Class<EchoBinding> getModelType() {
        return EchoBinding.class;
    }

    public EchoBinding read(XMLStreamReader reader) throws ContributionReadException {
        String uri = reader.getAttributeValue(null, "uri");
        EchoBinding echoBinding = factory.createEchoBinding();
        if (uri != null) {
            echoBinding.setURI(uri.trim());
        }
        return echoBinding;
    }

    public void write(EchoBinding echoBinding, XMLStreamWriter writer) throws ContributionWriteException {
    }

    public void resolve(EchoBinding echoBinding, ModelResolver resolver) throws ContributionResolveException {
    }

}
