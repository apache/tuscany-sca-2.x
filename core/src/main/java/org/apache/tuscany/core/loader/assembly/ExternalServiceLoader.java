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

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.core.loader.StAXUtil;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.EXTERNAL_SERVICE;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ExternalServiceLoader extends AbstractLoader {
    public QName getXMLType() {
        return EXTERNAL_SERVICE;
    }

    public Class<ExternalService> getModelType() {
        return ExternalService.class;
    }

    public ExternalService load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert EXTERNAL_SERVICE.equals(reader.getName());
        ExternalService externalService = factory.createExternalService();
        externalService.setName(reader.getAttributeValue(null, "name"));
        externalService.setOverrideOption(StAXUtil.overrideOption(reader.getAttributeValue(null, "overridable"), OverrideOption.NO));

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                AssemblyModelObject o = registry.load(reader, resourceLoader);
                if (o instanceof ServiceContract) {
                    Service service = factory.createService();
                    service.setServiceContract((ServiceContract) o);
                    ConfiguredService configuredService = factory.createConfiguredService();
                    configuredService.setService(service);
                    externalService.setConfiguredService(configuredService);
                }
                reader.next();
                break;
            case END_ELEMENT:
                return externalService;
            }
        }
    }
}
