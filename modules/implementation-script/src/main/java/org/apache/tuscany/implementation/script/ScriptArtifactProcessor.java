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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;

public class ScriptArtifactProcessor implements StAXArtifactProcessorExtension<ScriptImplementation> {

    private static final String SCRIPT = "script";
    private static final String IMPLEMENTATION_SCRIPT = "implementation.script";
    private static final QName IMPLEMENTATION_SCRIPT_QNAME = new QName(Constants.SCA10_NS, IMPLEMENTATION_SCRIPT);

    public ScriptArtifactProcessor() {
    }

    public ScriptImplementation read(XMLStreamReader reader) throws ContributionReadException {

        try {

            String scriptName = reader.getAttributeValue(null, SCRIPT);
            ScriptImplementation scriptImplementation = new ScriptImplementation(scriptName);

            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_SCRIPT_QNAME.equals(reader.getName())) {
                    break;
                }
            }

            processComponentType(scriptImplementation);

            return scriptImplementation;

        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    private void processComponentType(ScriptImplementation scriptImplementation) {
        // Form the URI of the expected .componentType file;

        String ctName = scriptImplementation.getName();
        int lastDot = ctName.lastIndexOf('.');
        ctName = ctName.substring(0, lastDot) + ".componentType";
        
        String uri = ctName;

        // Create a ComponentType and mark it unresolved
        ComponentType componentType = new DefaultAssemblyFactory().createComponentType();
        componentType.setURI(uri);
        componentType.setUnresolved(true);
        scriptImplementation.setComponentType(componentType);
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

        scriptImplementation.setScriptSrc(readScript(scriptImplementation.getName()));

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String scriptURI = cl.getResource(scriptImplementation.getName()).toString();
        int lastDot = scriptURI.lastIndexOf('.');
        String ctURI = scriptURI.substring(0, lastDot) + ".componentType";
        ComponentType ct = scriptImplementation.getComponentType();
        ct.setURI(ctURI);
        ComponentType componentType = resolver.resolve(ComponentType.class, ct);
        if (componentType.isUnresolved()) {
            throw new ContributionResolveException("missing .componentType side file");
        }
        scriptImplementation.setComponentType(componentType);
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

    protected String readScript(String scriptName) throws ContributionResolveException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL scriptSrcUrl = cl.getResource(scriptName);
        if (scriptSrcUrl == null) {
            throw new ContributionResolveException("No script: " + scriptName);
        }

        InputStream is;
        try {
            is = scriptSrcUrl.openStream();
        } catch (IOException e) {
            throw new ContributionResolveException(e);
        }

        try {

            Reader reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[1024];
            StringBuilder source = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) > 0) {
                source.append(buffer, 0, count);
            }

            return source.toString();

        } catch (IOException e) {
            throw new ContributionResolveException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
