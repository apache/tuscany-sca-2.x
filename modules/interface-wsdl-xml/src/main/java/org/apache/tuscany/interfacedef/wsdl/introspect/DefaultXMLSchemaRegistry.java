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

package org.apache.tuscany.interfacedef.wsdl.introspect;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Element;

/**
 * Default implementation of XMLSchemaRegistry
 */
public class DefaultXMLSchemaRegistry implements XMLSchemaRegistry {
    private final XmlSchemaCollection collection;

    /**
     * @param collection
     */
    public DefaultXMLSchemaRegistry(XmlSchemaCollection collection) {
        super();
        this.collection = collection;
    }

    public DefaultXMLSchemaRegistry() {
        super();
        this.collection = new XmlSchemaCollection();
    }

    public XmlSchemaElement getElement(QName name) {
        return collection.getElementByQName(name);
    }

    public XmlSchemaType getType(QName name) {
        return collection.getTypeByQName(name);
    }

    public List<XmlSchema> loadSchemas(Definition definition) {
        Types types = definition.getTypes();
        if (types == null) {
            return Collections.emptyList();
        }
        List<XmlSchema> schemas = new ArrayList<XmlSchema>();
        for (Object ext : types.getExtensibilityElements()) {
            if (ext instanceof Schema) {
                Element element = ((Schema) ext).getElement();
                XmlSchema s = collection.read(element, element.getBaseURI());
                schemas.add(s);
            }
        }
        return schemas;
    }

    public XmlSchema loadSchema(String namespace, URL location) throws IOException, XmlSchemaException {
        XmlSchema schema;
        XmlSchema[] schemaList = collection.getXmlSchema(location.toExternalForm());
        if (schemaList != null && schemaList.length > 0) {
            schema = schemaList[0];
        } else {
            InputStream is = location.openStream();
            schema = collection.read(new InputStreamReader(is), null);
            is.close();
        }
        if (namespace != null && schema != null && !namespace.equals(schema.getTargetNamespace())) {
            throw new XmlSchemaException(namespace + " != " + schema.getTargetNamespace());
        }
        return schema;
    }

    public XmlSchema loadSchema(String schemaLocation, ClassLoader classLoader) throws IOException, XmlSchemaException {
        int index = schemaLocation.indexOf(' ');
        if (index == -1) {
            throw new XmlSchemaException("Invalid schemaLocation: " + schemaLocation);
        }
        String namespace = schemaLocation.substring(0, index).trim();
        URL url;
        URI uri;
        try {
            uri = new URI(schemaLocation.substring(index + 1).trim());
        } catch (URISyntaxException e) {
            throw new XmlSchemaException("Invalid schemaLocation: " + schemaLocation);
        }
        if (uri.isAbsolute()) {
            url = uri.toURL();
        } else {
            url = classLoader.getResource(uri.toString());
            if (url == null) {
                throw new XmlSchemaException("Resource cannot be resolved: schemaLocation: " + schemaLocation);
            }
        }
        return loadSchema(namespace, url);
    }

}
