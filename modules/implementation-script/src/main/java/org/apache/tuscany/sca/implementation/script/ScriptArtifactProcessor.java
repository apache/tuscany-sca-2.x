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
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.implementation.spi.PropertyValueObjectFactory;
import org.apache.tuscany.implementation.spi.ResourceHelper;

// TODO: I hate the way this has to mess about with the .componentType side file, 
//       the runtime should do that for me

public class ScriptArtifactProcessor implements StAXArtifactProcessor<ScriptImplementation> {

    private static final QName IMPLEMENTATION_SCRIPT_QNAME = new QName(Constants.SCA10_NS, "implementation.script");
    
    private AssemblyFactory assemblyFactory;
    private PropertyValueObjectFactory propertyFactory;

    public ScriptArtifactProcessor(AssemblyFactory assemblyFactory, PropertyValueObjectFactory propertyFactory) {
        this.assemblyFactory = assemblyFactory;
        this.propertyFactory = propertyFactory;
    }

    public ScriptImplementation read(XMLStreamReader reader) throws ContributionReadException {

        try {

            String scriptName = reader.getAttributeValue(null, "script");
            String scriptLanguage = reader.getAttributeValue(null, "language");
            if (scriptLanguage == null || scriptLanguage.length() < 1) {
                int i = scriptName.lastIndexOf('.');
                scriptLanguage = scriptName.substring(i+1);
            }
            ScriptImplementation scriptImplementation = new ScriptImplementation(scriptName, scriptLanguage);

            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_SCRIPT_QNAME.equals(reader.getName())) {
                    break;
                }
            }

            String scriptSrc = ResourceHelper.readResource(scriptImplementation.getScriptName());
            scriptImplementation.setScriptSrc(scriptSrc);

            processComponentType(scriptImplementation);

            return scriptImplementation;

        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    private void processComponentType(ScriptImplementation scriptImplementation) {
        // Form the URI of the expected .componentType file;

        String ctName = scriptImplementation.getScriptName();
        int lastDot = ctName.lastIndexOf('.');
        ctName = ctName.substring(0, lastDot) + ".componentType";
        
        String uri = ctName;

        // Create a ComponentType and mark it unresolved
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setURI(uri);
        componentType.setUnresolved(true);
        scriptImplementation.setComponentType(componentType);
    }

    public void write(ScriptImplementation scriptImplementation, XMLStreamWriter writer) throws ContributionWriteException {
        try {

            writer.writeStartElement(Constants.SCA10_NS, "implementation.script");

            if (scriptImplementation.getScriptName() != null) {
                writer.writeAttribute("script", scriptImplementation.getScriptName());
            }

            if (scriptImplementation.getScriptLanguage() != null) {
                writer.writeAttribute("language", scriptImplementation.getScriptLanguage());
            }

            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(ScriptImplementation scriptImplementation, ArtifactResolver resolver) throws ContributionResolveException {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String scriptURI = cl.getResource(scriptImplementation.getScriptName()).toString();
        int lastDot = scriptURI.lastIndexOf('.');
        String ctURI = scriptURI.substring(0, lastDot) + ".componentType";
        ComponentType ct = scriptImplementation.getComponentType();
        ct.setURI(ctURI);
        ComponentType componentType = resolver.resolve(ComponentType.class, ct);
        if (componentType.isUnresolved()) {
            throw new ContributionResolveException("missing .componentType side file");
        }
        for (Reference reference : componentType.getReferences()) {
            scriptImplementation.getReferences().add(reference);
        }
        for (Service service : componentType.getServices()) {
            scriptImplementation.getServices().add(service);
        }
        for (Property property : componentType.getProperties()) {
            scriptImplementation.getProperties().add(property);
        }
        scriptImplementation.setComponentType(componentType);
        
        scriptImplementation.setUnresolved(false);
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_SCRIPT_QNAME;
    }

    public Class<ScriptImplementation> getModelType() {
        return ScriptImplementation.class;
    }

}
