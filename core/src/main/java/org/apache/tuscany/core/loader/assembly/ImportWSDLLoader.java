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
import java.io.InputStream;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import commonj.sdo.helper.XSDHelper;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.MissingResourceException;
import org.apache.tuscany.core.config.SidefileLoadException;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.IMPORT_WSDL;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.ImportWSDL;
import org.apache.tuscany.sdo.util.SDOUtil;

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

    public ImportWSDL load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert AssemblyConstants.IMPORT_WSDL.equals(reader.getName());
        String namespace = reader.getAttributeValue(null, "namespace");
        String location = reader.getAttributeValue(null, "location");
        ImportWSDL importWSDL = factory.createImportWSDL(location, namespace);

        Definition definition = loadDefinition(namespace, location, resourceLoader);
//        importWSDL.setDefinition(definition);

        StAXUtil.skipToEndElement(reader);
        return importWSDL;
    }

    protected Definition loadDefinition(String namespace, String location, ResourceLoader resourceLoader) throws MissingResourceException, SidefileLoadException {
        Definition definition;
        URL wsdlURL = resourceLoader.getResource(location);
        if (wsdlURL == null) {
            throw new MissingResourceException(location);
        }

        try {
            definition = wsdlRegistry.loadDefinition(namespace, wsdlURL);
        } catch (IOException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(location);
            throw sfe;
        } catch (WSDLException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(location);
            throw sfe;
        }

        try {
            InputStream xsdInputStream = wsdlURL.openStream();
            try {
                AssemblyContext context = registry.getContext();
                XSDHelper xsdHelper = SDOUtil.createXSDHelper(context.getTypeHelper());
                xsdHelper.define(xsdInputStream, null);
            } finally {
                xsdInputStream.close();
            }
        } catch (IOException e) {
            SidefileLoadException sfe = new SidefileLoadException(e.getMessage());
            sfe.setResourceURI(location);
            throw sfe;
        }

        return definition;
    }
}
