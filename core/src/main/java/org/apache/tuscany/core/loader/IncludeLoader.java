/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.Include;

/**
 * Loader that handles &lt;include&gt; elements.
 *
 * @version $Rev$ $Date$
 */
public class IncludeLoader extends LoaderExtension<Include> {
    private static final QName INCLUDE = new QName(XML_NAMESPACE_1_0, "include");

    public QName getXMLType() {
        return INCLUDE;
    }

    public Include load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert INCLUDE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");

        Include include = new Include();
        include.setName(name);

        LoaderUtil.skipToEndElement(reader);
        return include;
    }
}
