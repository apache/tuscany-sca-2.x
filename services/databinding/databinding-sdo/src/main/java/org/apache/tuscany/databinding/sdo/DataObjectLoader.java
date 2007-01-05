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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sdo.helper.XMLStreamHelper;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.EagerInit;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

/**
 * A SDO model-based Loader to load DataObject from the XML stream
 *
 */
@EagerInit
public class DataObjectLoader implements StAXElementLoader<ModelObject> {
    protected LoaderRegistry registry;
    private QName propertyQName;

    public DataObjectLoader(Property property) {
        super();
        this.propertyQName = new QName(XSDHelper.INSTANCE.getNamespaceURI(property),
                                       XSDHelper.INSTANCE.getLocalName(property));
    }

    public DataObjectLoader(QName propertyQName) {
        super();
        this.propertyQName = propertyQName;
    }

    public ModelObject load(CompositeComponent parent, ModelObject object, XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert propertyQName.equals(reader.getName());
        // TODO: We need a way to get TypeHelper from deploymentContext
        TypeHelper typeHelper = TypeHelper.INSTANCE;
        XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(typeHelper);
        DataObject dataObject = streamHelper.loadObject(reader);
        // TODO: Is it required that the object always extends from ModelObject?
        return new ModelDataObject(dataObject);
    }

    @Autowire
    public void setRegistry(LoaderRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void start() {
        registry.registerLoader(propertyQName, this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(propertyQName, this);
    }


}
