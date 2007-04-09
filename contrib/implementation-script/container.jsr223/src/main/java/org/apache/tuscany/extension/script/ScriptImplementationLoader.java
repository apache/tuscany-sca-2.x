/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.extension.script;

import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

/**
 * Loader for handling implementation.script elements. <p/>
 * <code><implementation.script script="path/foo.py" [language="lang" class="myclass"]></code>
 */
public class ScriptImplementationLoader extends LoaderExtension<ScriptImplementation> {

    private static final QName IMPLEMENTATION_SCRIPT = new QName(SCA_NS, "implementation.script");

    @Constructor( {"registry"})
    public ScriptImplementationLoader(@Reference LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return IMPLEMENTATION_SCRIPT;
    }

    public ScriptImplementation load(ModelObject mo, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        String scriptName = reader.getAttributeValue(null, "script");
        if (scriptName != null && scriptName.length() < 1) {
            scriptName = null;
        }
        
        String scriptLanguage = reader.getAttributeValue(null, "language");
        if (scriptLanguage == null || scriptLanguage.length() < 1) {
            int i = scriptName.lastIndexOf('.');
            if (i > 0) {
                scriptLanguage = scriptName.substring(i + 1);
            }
        }
        if (scriptLanguage == null || scriptLanguage.length() < 1) {
            throw new LoaderException("unable to determine script language");
        }

        String scriptClassName = reader.getAttributeValue(null, "class");

        String scriptSrc = null;
//        String scriptSrc = reader.getElementText();
//        if (scriptSrc != null && scriptSrc.length() < 1) {
//            scriptSrc = null;
//        }
        if (scriptName == null && scriptSrc == null) {
            throw new MissingResourceException("no 'script' attribute or inline script source");
        }
        if (scriptName != null && scriptSrc != null) {
            throw new MissingResourceException("cannot use both 'script' attribute and inline script");
        }

        LoaderUtil.skipToEndElement(reader);

        ClassLoader cl = deploymentContext.getClassLoader();

        ScriptImplementation impl = new ScriptImplementation(scriptName, scriptSrc, scriptLanguage, scriptClassName, cl);

        registry.loadComponentType(impl, deploymentContext);

        return impl;
    }
}
