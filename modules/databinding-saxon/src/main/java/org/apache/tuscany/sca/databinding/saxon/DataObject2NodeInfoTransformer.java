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
package org.apache.tuscany.sca.databinding.saxon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.om.NodeInfo;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * Transforms SDO DataObject-s to NodeInfo objects needed by Saxon parser
 * @version $Rev$ $Date$
 * For root element when serializing the DataObject the name of its 
 * implemented interface is used with its first letter made lowercase.
 * Also any namespaces that are defined are deleted, because otherwise
 * the SaxonB parser does not work
 */
public class DataObject2NodeInfoTransformer extends BaseTransformer<DataObject, NodeInfo> implements
    PullTransformer<DataObject, NodeInfo> {

    private Node2NodeInfoTransformer node2NodeInfoTransformer;

    public DataObject2NodeInfoTransformer(Node2NodeInfoTransformer node2NodeInfoTransformer) {
        this.node2NodeInfoTransformer = node2NodeInfoTransformer;
    }

    public NodeInfo transform(DataObject source, TransformationContext context) {
        XMLHelper helper = HelperProvider.INSTANCE.xmlHelper();
        String name = null;
        if (source.getClass().getInterfaces().length > 0) {
            name = source.getClass().getInterfaces()[0].getSimpleName();
        } else {
            name = source.getClass().getName();
        }

        if (name.length() > 0) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());
        }

        DOMResult domResult = new DOMResult();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            helper.save(source, null, name, baos);
            baos.flush();
            baos.close();
        } catch (IOException e) {
            throw new TransformationException(e);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Source src = new StreamSource(bais);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(src, domResult);
        } catch (TransformerConfigurationException e) {
            throw new TransformationException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new TransformationException(e);
        } catch (TransformerException e) {
            throw new TransformationException(e);
        }

        return node2NodeInfoTransformer.transform(domResult.getNode(), context);
    }

    @Override
    protected Class getSourceType() {
        return DataObject.class;
    }

    @Override
    protected Class getTargetType() {
        return NodeInfo.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
