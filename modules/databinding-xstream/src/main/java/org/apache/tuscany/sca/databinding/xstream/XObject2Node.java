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
package org.apache.tuscany.sca.databinding.xstream;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomWriter;

/**
 * XObject --> DOM Node transformer
 * 
 * @version $Rev$ $Date$
 */
public class XObject2Node extends BaseTransformer<XObject, Node> implements PullTransformer<XObject, Node> {

    public Node transform(XObject source, TransformationContext context) {
        try {
            MetaObject mj = new MetaObjectImpl(source);
            Document root = DOMHelper.newDocument();
            DomWriter out = new DomWriter(DOMHelper.newDocument());
            XStream xs = new XStream();
            xs.alias("xobject", mj.getClass());
            xs.marshal(source, out);
            DOMHelper.adjustElementName(context, root.getDocumentElement());
            return root;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public Class<XObject> getSourceType() {
        return XObject.class;
    }

    @Override
    public Class<Node> getTargetType() {
        return Node.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
