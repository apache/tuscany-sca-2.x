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

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.impl.DefaultAssemblyFactory;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.policy.model.impl.DefaultPolicyFactory;
import org.apache.tuscany.scdl.stax.Constants;
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

    private AssemblyFactory assemblyFactory;
    private PolicyFactory policyFactory;
    private XMLInputFactory factory;

    /**
     * @param assemblyFactory
     * @param policyFactory
     * @param factory
     */
    public LoaderRegistryImpl(AssemblyFactory assemblyFactory, PolicyFactory policyFactory, XMLInputFactory factory) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.policyFactory = policyFactory;
        this.factory = factory;
        init();
    }

    public LoaderRegistryImpl() {
        this(new DefaultAssemblyFactory(), new DefaultPolicyFactory(), XMLInputFactory.newInstance());
    }

    public final void init() {
        addLoader(Constants.COMPOSITE_QNAME, new CompositeLoader(assemblyFactory, policyFactory, this));
        addLoader(Constants.COMPONENT_TYPE_QNAME, new ComponentTypeLoader(assemblyFactory, policyFactory, this));
        addLoader(Constants.CONSTRAINING_TYPE_QNAME, new ConstrainingTypeLoader(assemblyFactory, policyFactory, this));
    }

    public Object load(XMLStreamReader reader) throws XMLStreamException {
        QName name = reader.getName();
        Loader loader = loaders.get(name);
        if (loader == null) {
            return null;
        }
        return loader.load(reader);
    }

    public <MO> MO load(URL url, Class<MO> type) throws LoaderException {
        try {
            XMLStreamReader reader;
            InputStream is;
            is = url.openStream();
            try {
                reader = factory.createXMLStreamReader(is);
                try {
                    reader.nextTag();
                    QName name = reader.getName();
                    Object mo = load(reader);
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
            throw new InvalidConfigurationException("Invalid or missing resource: " + url.toString(), e);
        }
    }

    public final void addLoader(QName element, Loader loader) {
        loaders.put(element, loader);
    }

    public Loader getLoader(QName element) {
        return loaders.get(element);
    }

    /**
     * @return the assemblyFactory
     */
    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }

    /**
     * @return the policyFactory
     */
    public PolicyFactory getPolicyFactory() {
        return policyFactory;
    }

}
