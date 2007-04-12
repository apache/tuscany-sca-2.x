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
package org.apache.tuscany.databinding.sdo;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static org.apache.tuscany.databinding.sdo.ImportSDO.IMPORT_SDO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * Loader that handles &lt;import.sdo&gt; elements.
 * 
 * @version $Rev$ $Date$
 */
public class ImportSDOProcessor implements StAXArtifactProcessor<ImportSDO> {
    private HelperContextRegistry helperContextRegistry;

    public ImportSDOProcessor(HelperContextRegistry helperContextRegistry) {
        super();
        this.helperContextRegistry = helperContextRegistry;
    }

    public QName getXMLType() {
        return IMPORT_SDO;
    }

    public ImportSDO read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPORT_SDO.equals(reader.getName());

        HelperContext helperContext = null;
        
        // FIXME: [rfeng] How to get the enclosing composite?
        int id = System.identityHashCode(reader);
        // FIXME: [rfeng] How to associate the TypeHelper with deployment
        // context?
        synchronized (helperContextRegistry) {
            helperContext = helperContextRegistry.getHelperContext(id);
            if (helperContext == null) {
                helperContext = SDOUtil.createHelperContext();
                helperContextRegistry.register(id, helperContext);
            }
        }

        ImportSDO importSDO = new ImportSDO(helperContext);
        String factoryName = reader.getAttributeValue(null, "factory");
        if (factoryName != null) {
            importSDO.setFactoryClassName(factoryName);
        }
        String location = reader.getAttributeValue(null, "location");
        if (location == null) {
            importSDO.setSchemaLocation(location);
        }

        // Skip to end element
        try {
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && ImportSDO.IMPORT_SDO.equals(reader.getName())) {
                    break;
                }
            }
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
        return importSDO;
    }

    private void importFactory(ImportSDO importSDO) throws ContributionResolveException {
        String factoryName = importSDO.getFactoryClassName();
        if (factoryName != null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Class<?> factoryClass = cl.loadClass(factoryName);
                register(factoryClass, importSDO.getHelperContext());
            } catch (Exception e) {
                throw new ContributionResolveException(e);
            }
        }
    }

    private static void register(Class factoryClass, HelperContext helperContext) throws Exception {
        Field field = factoryClass.getField("INSTANCE");
        Object factory = field.get(null);
        Method method = factory.getClass().getMethod("register", new Class[] {HelperContext.class});
        method.invoke(factory, new Object[] {helperContext});

        // FIXME: How do we associate the application HelperContext with the one
        // imported by the composite
        HelperContext defaultContext = HelperProvider.getDefaultContext();
        method.invoke(factory, new Object[] {defaultContext});
    }

    private void importWSDL(ImportSDO importSDO) throws ContributionResolveException {
        String location = importSDO.getSchemaLocation();
        if (location != null) {
            try {
                URL wsdlURL = null;
                URI uri = URI.create(location);
                if (uri.isAbsolute()) {
                    wsdlURL = uri.toURL();
                }
                wsdlURL = Thread.currentThread().getContextClassLoader().getResource(location);
                if (null == wsdlURL) {
                    ContributionResolveException loaderException = new ContributionResolveException(
                                                                                                    "WSDL location error");
                    throw loaderException;
                }
                InputStream xsdInputStream = wsdlURL.openStream();
                try {
                    XSDHelper xsdHelper = importSDO.getHelperContext().getXSDHelper();
                    xsdHelper.define(xsdInputStream, wsdlURL.toExternalForm());
                } finally {
                    xsdInputStream.close();
                }
                // FIXME: How do we associate the application HelperContext with
                // the one
                // imported by the composite
                HelperContext defaultContext = HelperProvider.getDefaultContext();
                xsdInputStream = wsdlURL.openStream();
                try {
                    XSDHelper xsdHelper = defaultContext.getXSDHelper();
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    xsdHelper.define(xsdInputStream, wsdlURL.toExternalForm());
                } finally {
                    xsdInputStream.close();
                }
            } catch (IOException e) {
                throw new ContributionResolveException(e);
            }
        }
    }

    public QName getArtifactType() {
        return ImportSDO.IMPORT_SDO;
    }

    public void write(ImportSDO model, XMLStreamWriter outputSource) throws ContributionWriteException {
        // TODO Auto-generated method stub

    }

    public Class<ImportSDO> getModelType() {
        return ImportSDO.class;
    }

    public void resolve(ImportSDO importSDO, ArtifactResolver resolver) throws ContributionResolveException {
        importFactory(importSDO);
        importWSDL(importSDO);
    }

    public void wire(ImportSDO model) throws ContributionWireException {
    }
}
