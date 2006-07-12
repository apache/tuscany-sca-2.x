/**
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
package org.apache.tuscany.core.implementation.system.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;

/**
 * Loads a system binding specified in an XML-based assembly
 *
 * @version $Rev: 416156 $ $Date: 2006-06-21 16:02:33 -0700 (Wed, 21 Jun 2006) $
 */
public class SystemBindingLoader extends LoaderExtension<SystemBinding> {
    public static final QName SYSTEM_BINDING =
        new QName("http://tuscany.apache.org/xmlns/system/0.9", "binding.system");

    public SystemBindingLoader() {
    }

    public SystemBindingLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return SYSTEM_BINDING;
    }

    public SystemBinding load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        LoaderUtil.skipToEndElement(reader);
        return new SystemBinding();
    }
}
