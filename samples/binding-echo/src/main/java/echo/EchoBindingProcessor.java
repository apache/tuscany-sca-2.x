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

package echo;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;

/**
 * A processor for <binding.echo> elements.
 *
 * @version $Rev$ $Date$
 */
public class EchoBindingProcessor implements StAXArtifactProcessorExtension<EchoBinding> {
    private final EchoBindingFactory factory;

    public EchoBindingProcessor() {
        this.factory = new DefaultEchoBindingFactory();
    }

    public EchoBindingProcessor(EchoBindingFactory factory) {
        this.factory = factory;
    }

    public QName getArtifactType() {
        return EchoConstants.BINDING_ECHO;
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
        // TODO Auto-generated method stub
    }

    public void resolve(EchoBinding echoBinding, ArtifactResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }

}
