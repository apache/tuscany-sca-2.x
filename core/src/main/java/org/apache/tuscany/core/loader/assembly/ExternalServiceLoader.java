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

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.core.loader.LoaderContext;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.EXTERNAL_SERVICE;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
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
public class ExternalServiceLoader extends AbstractLoader {
    public QName getXMLType() {
        return EXTERNAL_SERVICE;
    }

    public ExternalService load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        assert EXTERNAL_SERVICE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        ExternalService externalService = factory.createExternalService();
        externalService.setName(name);
        externalService.setOverrideOption(StAXUtil.overrideOption(reader.getAttributeValue(null, "overridable"), OverrideOption.NO));

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                AssemblyObject o = registry.load(reader, loaderContext);
                if (o instanceof ServiceContract) {
                    Service service = factory.createService();
                    service.setName(name);
                    service.setServiceContract((ServiceContract) o);
                    ConfiguredService configuredService = factory.createConfiguredService();
                    configuredService.setPort(service);
                    externalService.setConfiguredService(configuredService);
                } else if (o instanceof Binding) {
                    externalService.getBindings().add((Binding) o);
                }
                reader.next();
                break;
            case END_ELEMENT:
                return externalService;
            }
        }
    }
}
