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
package org.apache.tuscany.scdl.stax.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.model.Base;
import org.apache.tuscany.scdl.stax.InvalidConfigurationException;
import org.apache.tuscany.scdl.stax.Loader;
import org.apache.tuscany.scdl.stax.LoaderException;
import org.apache.tuscany.scdl.stax.LoaderRegistry;
import org.apache.tuscany.scdl.stax.UnrecognizedElementException;

/**
 * The default implementation of a loader registry
 * 
 * @version $Rev$ $Date$
 */
public class LoaderRegistryImpl implements LoaderRegistry {
    private final Map<QName, Loader> loaders = new HashMap<QName, Loader>();

    public LoaderRegistryImpl() {
    }

    public Base load(Base object, XMLStreamReader reader) throws XMLStreamException {
        QName name = reader.getName();
        Loader loader = loaders.get(name);
        if (loader == null) {
            // FIXME:
            // throw new IllegalArgumentException(name.toString());
            return null;
        }
        return (Base) loader.load(object, reader);
    }

    public <MO extends Base> MO load(Base object, URL url, Class<MO> type) throws LoaderException {
        try {
            XMLStreamReader reader;
            InputStream is;
            is = url.openStream();
            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                reader = factory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    QName name = reader.getName();
                    Base mo = load(object, reader);
                    if (type.isInstance(mo)) {
                        return type.cast(mo);
                    } else {
                        UnrecognizedElementException e = new UnrecognizedElementException(name);
                        e.setResourceURI(url.toString());
                        throw e;
                    }
                } catch (LoaderException e) {
                    Location location = reader.getLocation();
                    e.setLine(location.getLineNumber());
                    e.setColumn(location.getColumnNumber());
                    throw e;
                } finally {
                    try {
                        reader.close();
                    } catch (XMLStreamException e) {
                        // ignore
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (IOException e) {
            LoaderException sfe = new LoaderException(e);
            sfe.setResourceURI(url.toString());
            throw sfe;
        } catch (XMLStreamException e) {
            throw new InvalidConfigurationException("Invalid or missing resource", url.toString(), e);
        }
    }

    public void addLoader(QName element, Loader loader) {
        loaders.put(element, loader);
    }

    public Loader getLoader(QName element) {
        return loaders.get(element);
    }

}
