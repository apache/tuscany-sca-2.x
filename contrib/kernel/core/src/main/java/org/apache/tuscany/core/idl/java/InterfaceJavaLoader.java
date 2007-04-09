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
package org.apache.tuscany.core.idl.java;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Loads a Java interface definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class InterfaceJavaLoader extends LoaderExtension<JavaServiceContract> {
    public static final QName INTERFACE_JAVA = new QName(SCA_NS, "interface.java");

    private final JavaInterfaceProcessorRegistry interfaceRegsitry;

    @Constructor({"registry", "interfaceRegsitry"})
    public InterfaceJavaLoader(@Reference LoaderRegistry registry,
                               @Reference JavaInterfaceProcessorRegistry interfaceRegistry) {
        super(registry);
        this.interfaceRegsitry = interfaceRegistry;
    }

    public QName getXMLType() {
        return INTERFACE_JAVA;
    }

    public JavaServiceContract load(
        ModelObject object, XMLStreamReader reader,
        DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert INTERFACE_JAVA.equals(reader.getName());
        String conversationalAttr = reader.getAttributeValue(null, "conversational");
        boolean conversational = Boolean.parseBoolean(conversationalAttr);
        String name = reader.getAttributeValue(null, "interface");
        if (name == null) {
            // allow "class" as well as seems to be a common mistake
            name = reader.getAttributeValue(null, "class");
        }
        if (name == null) {
            throw new InvalidValueException("interface name not supplied");
        }
        Class<?> interfaceClass = LoaderUtil.loadClass(name, deploymentContext.getClassLoader());

        name = reader.getAttributeValue(null, "callbackInterface");
        Class<?> callbackClass = (name != null) ? LoaderUtil.loadClass(name, deploymentContext.getClassLoader()) : null;

        Map<Class<?>, ModelObject> extensions = new HashMap<Class<?>, ModelObject>();
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                ModelObject mo = registry.load(null, reader, deploymentContext);
                if (mo != null) {
                    extensions.put(mo.getClass(), mo);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getName().equals(INTERFACE_JAVA)) {
                break;
            }
        }
        JavaServiceContract serviceContract;
        try {
            serviceContract = interfaceRegsitry.introspect(interfaceClass, callbackClass);
        } catch (InvalidServiceContractException e) {
            throw new LoaderException(interfaceClass.getName(), e);
        }

        // Set databinding from the SCDL extension <databinding>
        DataType<?> dataType = (DataType<?>) extensions.get(DataType.class);
        if (dataType != null) {
            serviceContract.setDataBinding(dataType.getDataBinding());
        }
        serviceContract.getExtensions().putAll(extensions);
        serviceContract.setConversational(conversational);
        return serviceContract;
    }
}
