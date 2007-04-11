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

package org.apache.tuscany.implementation.script;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class ScriptArtifactProcessor implements StAXArtifactProcessor<ScriptImplementation> {

    private static final String SCRIPT = "script";
    private static final String IMPLEMENTATION_SCRIPT = "implementation.script";
    private static final QName IMPLEMENTATION_SCRIPT_QNAME = new QName(Constants.SCA10_NS, IMPLEMENTATION_SCRIPT);

    public ScriptArtifactProcessor() {
    }

    public ScriptImplementation read(XMLStreamReader reader) throws ContributionReadException {

        try {

            ScriptImplementation scriptImplementation = new ScriptImplementation();
            scriptImplementation.setUnresolved(true);
            scriptImplementation.setName(reader.getAttributeValue(null, SCRIPT));

            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_SCRIPT_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return scriptImplementation;

        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void write(ScriptImplementation scriptImplementation, XMLStreamWriter writer) throws ContributionWriteException {
        try {

            writer.writeStartElement(Constants.SCA10_NS, IMPLEMENTATION_SCRIPT);
            if (scriptImplementation.getName() != null) {
                writer.writeAttribute(SCRIPT, scriptImplementation.getName());
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(ScriptImplementation scriptImplementation, ArtifactResolver resolver) throws ContributionResolveException {
        try {

            // TODO: implement
            URL scriptSrcUrl = Thread.currentThread().getContextClassLoader().getResource(scriptImplementation.getName());
//            Class javaClass = Class.forName(scriptImplementation.getName(), true, Thread.currentThread().getContextClassLoader());
//            scriptImplementation.setJavaClass(javaClass);
//            
//            //FIXME JavaImplementationDefinition should not be mandatory 
//            if (scriptImplementation instanceof ScriptImplementationDefinition) {
//                introspectionRegistry.introspect(scriptImplementation.getJavaClass(), (ScriptImplementationDefinition)scriptImplementation);
//                
//                //FIXME the introspector should always create at least one service
//                if (scriptImplementation.getServices().isEmpty()) {
//                    scriptImplementation.getServices().add(new ServiceImpl());
//                }
//            }

        } catch (Exception e) {
            throw new ContributionResolveException(e);
        }
    }

    public void wire(ScriptImplementation model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_SCRIPT_QNAME;
    }

    public Class<ScriptImplementation> getModelType() {
        return ScriptImplementation.class;
    }

}
