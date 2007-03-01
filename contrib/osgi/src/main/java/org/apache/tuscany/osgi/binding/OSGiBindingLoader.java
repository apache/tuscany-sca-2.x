/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.osgi.binding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Loader for handling <code>binding.osgi</code> elements.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class OSGiBindingLoader extends LoaderExtension<OSGiBindingDefinition> {
    public static final QName BINDING_OSGI = new QName("http://tuscany.apache.org/xmlns/osgi/1.0", "binding.osgi");

    @Constructor
    public OSGiBindingLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return BINDING_OSGI;
    }

    public OSGiBindingDefinition load(CompositeComponent parent, ModelObject object, XMLStreamReader reader,
                            DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        String uri = reader.getAttributeValue(null, "uri");
        String service = reader.getAttributeValue(null, "service");
        LoaderUtil.skipToEndElement(reader);

        OSGiBindingDefinition binding = new OSGiBindingDefinition();
        binding.setURI(uri);
        binding.setService(service);
        return binding;
    }

}
