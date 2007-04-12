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

package org.apache.tuscany.binding.echo;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class EchoBindingLoader implements StAXArtifactProcessor {
    private final EchoBindingFactory factory;

    public EchoBindingLoader(){
        this.factory = new DefaultEchoBindingFactory();
    }
    
    public EchoBindingLoader(EchoBindingFactory factory){
        this.factory = factory;
    }
    
    public QName getArtifactType() {
        return EchoConstants.BINDING_ECHO;
    }

    public Class getModelType() {
        return EchoBinding.class;
    }

    public Object read(XMLStreamReader arg0) throws ContributionReadException {
        return factory.createEchoBinding();
    }

    public void write(Object arg0, XMLStreamWriter arg1) throws ContributionWriteException {
        // TODO Auto-generated method stub
    }

    public void resolve(Object arg0, ArtifactResolver arg1) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }

    public void wire(Object arg0) throws ContributionWireException {
        // TODO Auto-generated method stub
    }

}
