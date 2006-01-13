/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.deprecated.sdo.util.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import commonj.sdo.DataObject;

import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.model.util.ConfiguredResourceSetImpl;
import org.apache.tuscany.common.resource.loader.ResourceLoaderFactory;
import org.apache.tuscany.core.deprecated.sdo.util.XMLHelper;

/**
 * A serializable object that wraps a DataObject and is able to serialize/deserialize it without requiring it
 * to be in a DataGraph. This should be removed when we port to SDO 2.0.
 */
public class XMLDataObjectSerializer implements XMLHelper.DataObjectSerializer {

    private DataObject dataObject;

    /**
     * Constructor
     *
     * @param dataObject
     */
    public XMLDataObjectSerializer(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * Constructor
     *
     * @param dataObject
     */
    public XMLDataObjectSerializer() {
    }

    /**
     * @return Returns the dataObject.
     */
    public DataObject getDataObject() {
        return dataObject;
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String str = in.readUTF();
        ConfiguredResourceSet configuredResourceSet = new ConfiguredResourceSetImpl(ResourceLoaderFactory.getResourceLoader(Thread.currentThread().getContextClassLoader()));
        XMLHelper xmlHelper = new HelperProviderImpl(configuredResourceSet).getXMLHelper();
        dataObject = xmlHelper.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ConfiguredResourceSet configuredResourceSet = new ConfiguredResourceSetImpl(ResourceLoaderFactory.getResourceLoader(Thread.currentThread().getContextClassLoader()));
        XMLHelper xmlHelper = new HelperProviderImpl(configuredResourceSet).getXMLHelper();
        xmlHelper.save(dataObject, bos);
        out.writeUTF(bos.toString("UTF-8"));
    }
}

