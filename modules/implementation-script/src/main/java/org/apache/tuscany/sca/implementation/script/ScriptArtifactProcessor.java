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

package org.apache.tuscany.sca.implementation.script;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.implementation.spi.AbstractStAXArtifactProcessor;
import org.apache.tuscany.implementation.spi.PropertyValueObjectFactory;
import org.apache.tuscany.implementation.spi.ResourceHelper;

/**
 * ArtifactProcessor to read the SCDL XML for script implementations
 * 
 * <code><implementation.script script="pathToScriptFile" [language="scriptLanguage"] /></code>
 */
public class ScriptArtifactProcessor extends AbstractStAXArtifactProcessor<ScriptImplementation> {

    private static final QName IMPLEMENTATION_SCRIPT_QNAME = new QName(Constants.SCA10_NS, "implementation.script");

    // TODO: runtime needs to provide a better way to get the PropertyValueObjectFactory
    private PropertyValueObjectFactory propertyFactory;

    public ScriptArtifactProcessor(AssemblyFactory assemblyFactory, PropertyValueObjectFactory propertyFactory) {
        super(assemblyFactory);
        this.propertyFactory = propertyFactory;
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_SCRIPT_QNAME;
    }

    public Class<ScriptImplementation> getModelType() {
        return ScriptImplementation.class;
    }

    public ScriptImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        String scriptName = reader.getAttributeValue(null, "script");

        String scriptLanguage = reader.getAttributeValue(null, "language");
        if (scriptLanguage == null || scriptLanguage.length() < 1) {
            int i = scriptName.lastIndexOf('.');
            scriptLanguage = scriptName.substring(i+1);
        }

        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_SCRIPT_QNAME.equals(reader.getName())) {
                break;
            }
        }

        String scriptSrc = ResourceHelper.readResource(scriptName);
        ScriptImplementation scriptImpl = new ScriptImplementation(scriptName, scriptLanguage, scriptSrc, propertyFactory);

        // TODO: How to get the script URI? Should use the contrabution service
        //   the uri is used in the resolve method (perhaps incorrectly?) to get the .componentType sidefile
        scriptImpl.setURI(Thread.currentThread().getContextClassLoader().getResource(scriptName).toString());

        return scriptImpl;
    }

    public void write(ScriptImplementation scriptImplementation, XMLStreamWriter writer) throws XMLStreamException {

        writer.writeStartElement(Constants.SCA10_NS, "implementation.script");

        if (scriptImplementation.getScriptName() != null) {
            writer.writeAttribute("script", scriptImplementation.getScriptName());
        }

        if (scriptImplementation.getScriptLanguage() != null) {
            writer.writeAttribute("language", scriptImplementation.getScriptLanguage());
        }

        writer.writeEndElement();
    }
}
