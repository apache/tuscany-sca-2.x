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

import java.io.IOException;

import javax.xml.transform.dom.DOMSource;

import net.sf.saxon.om.NodeInfo;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.w3c.dom.Document;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;

/**
 * Transforms NodeInfo objects to SDO DataObjects.
 *
 * Before constructing the data object, resulting XML is populated
 * with correct namespaces (which are taken from the logical target data type)
 * These namespaces are crucial for constructing the right instance of
 * the DataObject (i.e. if there is a SDO factory, an instance from this factory will
 * be used, not the generic any data object)
 *
 * @version $Rev$ $Date$
 */
public class NodeInfo2DataObjectTransformer extends BaseTransformer<NodeInfo, DataObject> implements
    PullTransformer<NodeInfo, DataObject> {

    private NodeInfo2NodeTransformer nodeInfo2NodeTransformer;

    public NodeInfo2DataObjectTransformer(NodeInfo2NodeTransformer nodeInfo2NodeTransformer) {
        this.nodeInfo2NodeTransformer = nodeInfo2NodeTransformer;
    }

    public NodeInfo2DataObjectTransformer() {
        this.nodeInfo2NodeTransformer = new NodeInfo2NodeTransformer();
    }

    @Override
    protected Class<NodeInfo> getSourceType() {
        return NodeInfo.class;
    }

    @Override
    protected Class<DataObject> getTargetType() {
        return DataObject.class;
    }

    @Override
    public int getWeight() {
        return 30 + nodeInfo2NodeTransformer.getWeight();
    }

    public DataObject transform(NodeInfo source, TransformationContext context) {
        Document doc = (Document)nodeInfo2NodeTransformer.transform(source, context);

        return produceResult(doc);
    }

    private DataObject produceResult(Document doc) {
        try {
            // FIXME: [rfeng] We should use the HelperContext from the context
            DataObject resultingObject =
                XMLHelper.INSTANCE.load(new DOMSource(doc), doc.getDocumentURI(), null).getRootObject();
            return resultingObject;
        } catch (IOException e) {
            throw new TransformationException(e);
        }
    }
}
