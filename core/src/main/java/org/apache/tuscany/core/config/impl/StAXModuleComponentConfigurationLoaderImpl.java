/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.config.impl;

import java.io.IOException;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;

/**
 * @version $Rev$ $Date$
 */
public class StAXModuleComponentConfigurationLoaderImpl extends AbstractModuleComponentConfigurationLoader {
    private final XMLInputFactory xmlFactory;
    private final StAXLoaderRegistry registry;

    public StAXModuleComponentConfigurationLoaderImpl(AssemblyModelContext modelContext, XMLInputFactory xmlFactory, StAXLoaderRegistry registry) {
        super(modelContext);
        this.xmlFactory = xmlFactory;
        this.registry = registry;
    }

    public Module loadModule(URL url) throws ConfigurationLoadException {
        registry.setContext(modelContext);
        try {
            XMLStreamReader reader = xmlFactory.createXMLStreamReader(url.openStream());
            getDocumentRoot(reader);
            return (Module) registry.load(reader, resourceLoader);
        } catch (XMLStreamException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(url.toString()).initCause(e);
        } catch (IOException e) {
            throw new ConfigurationLoadException(url.toString(), e);
        } finally {
            registry.setContext(null);
        }
    }

    public ModuleFragment loadModuleFragment(URL url) throws ConfigurationLoadException {
        registry.setContext(modelContext);
        try {
            XMLStreamReader reader = xmlFactory.createXMLStreamReader(url.openStream());
            getDocumentRoot(reader);
            return (ModuleFragment) registry.load(reader, resourceLoader);
        } catch (XMLStreamException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(url.toString()).initCause(e);
        } catch (IOException e) {
            throw new ConfigurationLoadException(url.toString(), e);
        } finally {
            registry.setContext(null);
        }
    }

    private static void getDocumentRoot(XMLStreamReader reader) throws XMLStreamException {
        while (true) {
            if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                return;
            }
        }
    }
}
