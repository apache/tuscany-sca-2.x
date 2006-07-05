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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.component.CompositeComponent;

/**
 * Loads a reference from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ReferenceLoader extends LoaderExtension<ReferenceDefinition> {
    public ReferenceLoader() {
    }

    public ReferenceLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return AssemblyConstants.REFERENCE;
    }

    public ReferenceDefinition load(CompositeComponent parent,
                                    XMLStreamReader reader,
                                    DeploymentContext deploymentContext
    )
        throws XMLStreamException, LoaderException {
        assert AssemblyConstants.REFERENCE.equals(reader.getName());
        ReferenceDefinition referenceDefinition = new ReferenceDefinition();
        referenceDefinition.setName(reader.getAttributeValue(null, "name"));
        referenceDefinition.setMultiplicity(
            StAXUtil.multiplicity(reader.getAttributeValue(null, "multiplicity"), Multiplicity.ONE_ONE));

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    ModelObject o = registry.load(parent, reader, deploymentContext);
                    if (o instanceof ServiceContract) {
                        referenceDefinition.setServiceContract((ServiceContract) o);
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    return referenceDefinition;
            }
        }
    }
}
