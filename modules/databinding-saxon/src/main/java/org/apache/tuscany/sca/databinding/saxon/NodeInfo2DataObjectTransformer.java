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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.om.NodeInfo;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sdo.helper.TypeHelperImpl;
import org.apache.tuscany.sdo.impl.FactoryBase;
import org.w3c.dom.Document;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * Transforms NodeInfo objects to SDO DataObject-s
 * @version $Rev$ $Date$
 * Before constructing the data object, resulting XML is populated
 * with correct namespaces (which are taken from the logical target data type)
 * These namespaces are crucial for constructing the right instance of
 * the DataObject (i.e. if there is a SDO factory, an instance from this factory will
 * be used, not the generic any data object)
 */
public class NodeInfo2DataObjectTransformer extends BaseTransformer<NodeInfo, DataObject> implements
    PullTransformer<NodeInfo, DataObject> {

    private NodeInfo2NodeTransformer nodeInfo2NodeTransformer;

    public NodeInfo2DataObjectTransformer(NodeInfo2NodeTransformer nodeInfo2NodeTransformer) {
        this.nodeInfo2NodeTransformer = nodeInfo2NodeTransformer;
    }

    @Override
    protected Class getSourceType() {
        return NodeInfo.class;
    }

    @Override
    protected Class getTargetType() {
        return DataObject.class;
    }

    @Override
    public int getWeight() {
        return 10 + nodeInfo2NodeTransformer.getWeight();
    }

    public DataObject transform(NodeInfo source, TransformationContext context) {
        Document doc = (Document)nodeInfo2NodeTransformer.transform(source, context);

        Document cloneDoc = cloneDocumentWithRightNamespaces(doc, context);

        return produceResult(cloneDoc);
    }

    private Document cloneDocumentWithRightNamespaces(Document doc, TransformationContext context) {

        DataType targetDataType = context.getTargetDataType();
        String namespace = null;
        Object logical = targetDataType.getLogical();
        if (logical instanceof XMLType) {
            namespace = ((XMLType)logical).getTypeName().getNamespaceURI();
        } else {
            Type type = HelperProvider.INSTANCE.typeHelper().getType(targetDataType.getClass());

            if (type == null) {
                return doc;
            }
            namespace = type.getURI();
        }

        if (namespace == null) {
            return doc;
        }

        FactoryBase factory =
            (FactoryBase)((TypeHelperImpl)HelperProvider.INSTANCE.typeHelper()).getExtendedMetaData()
                .getPackage(namespace);
        if (factory == null) {
            return doc;
        }
        String prefix = factory.getNsPrefix();

        Document cloneDoc = (Document)doc.cloneNode(false);

        SaxonDataBindingHelper.setNamespacesAndPrefixesReq(doc, cloneDoc, cloneDoc, namespace, prefix);

        return cloneDoc;
    }

    private DataObject produceResult(Document doc) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(baos);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), streamResult);
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DataObject resultingObject = XMLHelper.INSTANCE.load(new String(baos.toByteArray())).getRootObject();

        return resultingObject;
    }
}
