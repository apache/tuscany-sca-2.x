/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.loader.assembly;

import java.io.IOException;
import java.net.URL;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.MissingResourceException;
import org.apache.tuscany.core.config.SidefileLoadException;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.IMPORT_WSDL;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.ImportWSDL;

/**
 * Loader that handles &lt;import.wsdl&gt; elements.
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ImportWSDLLoader extends AbstractLoader {
    private WSDLDefinitionRegistry wsdlRegistry;

    @Autowire
    public void setWsdlRegistry(WSDLDefinitionRegistry wsdlRegistry) {
        this.wsdlRegistry = wsdlRegistry;
    }

    public QName getXMLType() {
        return IMPORT_WSDL;
    }

    public Class<ImportWSDL> getModelType() {
        return ImportWSDL.class;
    }

    public ImportWSDL load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert AssemblyConstants.IMPORT_WSDL.equals(reader.getName());
        String namespace = reader.getAttributeValue(null, "namespace");
        String location = reader.getAttributeValue(null, "location");

        loadDefinition(namespace, location, resourceLoader);

        StAXUtil.skipToEndElement(reader);
        return factory.createImportWSDL(location, namespace);
    }

    protected void loadDefinition(String namespace, String location, ResourceLoader resourceLoader) throws MissingResourceException, SidefileLoadException {
        URL wsdlURL = resourceLoader.getResource(location);
        if (wsdlURL == null) {
            throw new MissingResourceException(location);
        }

        try {
            wsdlRegistry.loadDefinition(namespace, wsdlURL);
        } catch (IOException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(location);
            throw sfe;
        } catch (WSDLException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(location);
            throw sfe;
        }
    }
}
