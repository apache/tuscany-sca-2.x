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

import javax.xml.transform.dom.DOMResult;

import net.sf.saxon.om.NodeInfo;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * Transforms SDO DataObject-s to NodeInfo objects needed by Saxon parser.
 *
 * For root element when Serializing the DataObject the name of its 
 * implemented interface is used with its first letter made lowercase.
 * Also any namespaces that are defined are deleted, because otherwise
 * the SaxonB parser does not work
 *
 * @version $Rev$ $Date$
 */
public class DataObject2NodeInfoTransformer extends BaseTransformer<DataObject, NodeInfo> implements
    PullTransformer<DataObject, NodeInfo> {

    private Node2NodeInfoTransformer node2NodeInfoTransformer;

    public DataObject2NodeInfoTransformer(Node2NodeInfoTransformer node2NodeInfoTransformer) {
        this.node2NodeInfoTransformer = node2NodeInfoTransformer;
    }

    public DataObject2NodeInfoTransformer() {
        this.node2NodeInfoTransformer = new Node2NodeInfoTransformer();
    }

    public NodeInfo transform(DataObject source, TransformationContext context) {
        // FIXME: Need to create the HelperContext from the transformation context
        // FIXME: This is a big hack to create a document using the class name
        XMLHelper helper = XMLHelper.INSTANCE;
        String name = null;
        if (source.getClass().getInterfaces().length > 0) {
            name = source.getClass().getInterfaces()[0].getSimpleName();
        } else {
            name = source.getClass().getName();
        }

        if (name.length() > 0) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());
        }

        try {
            DOMResult domResult = new DOMResult(DOMHelper.newDocument());
            XMLDocument xmlDoc = helper.createDocument(source, null, name);
            helper.save(xmlDoc, domResult, null);
            return node2NodeInfoTransformer.transform(domResult.getNode(), context);
        } catch (Exception e) {
            throw new TransformationException(e);
        }

    }

    @Override
    protected Class<DataObject> getSourceType() {
        return DataObject.class;
    }

    @Override
    protected Class<NodeInfo> getTargetType() {
        return NodeInfo.class;
    }

    @Override
    public int getWeight() {
        return 30 + node2NodeInfoTransformer.getWeight();
    }

}
