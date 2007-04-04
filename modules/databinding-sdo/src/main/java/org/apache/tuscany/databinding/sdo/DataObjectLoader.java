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

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;

/**
 * A SDO model-based Loader to load DataObject from the XML stream
 */
public class DataObjectLoader extends LoaderExtension<ModelDataObject> {
    private QName elementName;

    public DataObjectLoader(LoaderRegistry registry, 
                            String namespace, 
                            String name) {
        super(registry);
        this.elementName = new QName(namespace, name);
    }

    public ModelDataObject load(ModelObject object,
                                XMLStreamReader reader,
                                DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert elementName.equals(reader.getName());
        HelperContext helperContext = SDOContextHelper.getHelperContext(object);
        TypeHelper typeHelper = helperContext.getTypeHelper();
        XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(typeHelper);
        DataObject dataObject = streamHelper.loadObject(reader);
        // TODO: Is it required that the object always extends from ModelObject?
        return new ModelDataObject(dataObject);
    }

    @Override
    public QName getXMLType() {
        return elementName;
    }

}
