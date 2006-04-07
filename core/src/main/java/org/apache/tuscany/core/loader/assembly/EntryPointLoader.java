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
package org.apache.tuscany.core.loader.assembly;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXUtil;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.ENTRY_POINT;
import org.apache.tuscany.model.assembly.*;
import org.osoa.sca.annotations.Scope;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class EntryPointLoader extends AbstractLoader {
    public QName getXMLType() {
        return ENTRY_POINT;
    }

    public Class<EntryPoint> getModelType() {
        return EntryPoint.class;
    }

    public EntryPoint load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert ENTRY_POINT.equals(reader.getName());
        EntryPoint entryPoint = factory.createEntryPoint();
        String name = reader.getAttributeValue(null, "name");
        entryPoint.setName(name);

        Service service = factory.createService();
        service.setName(name);
        ConfiguredService configuredService = factory.createConfiguredService();
        configuredService.setService(service);
        entryPoint.setConfiguredService(configuredService);

        Reference reference = factory.createReference();
        reference.setMultiplicity(StAXUtil.multiplicity(reader.getAttributeValue(null, "multiplicity"), Multiplicity.ONE_ONE));
        ConfiguredReference configuredReference = factory.createConfiguredReference();
        configuredReference.setReference(reference);
        entryPoint.setConfiguredReference(configuredReference);

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                QName qname = reader.getName();
                if (AssemblyConstants.REFERENCE.equals(qname)) {
                    String uri = reader.getElementText();
                    configuredReference.getTargets().add(uri);
                } else {
                    AssemblyModelObject o = registry.load(reader, resourceLoader);
                    if (o instanceof Binding) {
                        entryPoint.getBindings().add((Binding) o);
                    } else if (o instanceof ServiceContract) {
                        service.setServiceContract((ServiceContract) o);
                    }
                }
                reader.next();
                break;
            case END_ELEMENT:
                return entryPoint;
            }
        }
    }
}
