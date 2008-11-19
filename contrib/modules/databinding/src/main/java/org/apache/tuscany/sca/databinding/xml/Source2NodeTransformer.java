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
package org.apache.tuscany.sca.databinding.xml;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transform TrAX Source to Node
 *
 * @version $Rev$ $Date$
 */
public class Source2NodeTransformer extends BaseTransformer<Source, Node> implements
    PullTransformer<Source, Node> {
    private static final TransformerFactory FACTORY = TransformerFactory.newInstance();

    public Node transform(Source source, TransformationContext context) {
        try {
            javax.xml.transform.Transformer transformer = FACTORY.newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(source, result);
            Document doc = (Document) result.getNode();
            return DOMHelper.adjustElementName(context, doc.getDocumentElement());
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    protected Class<Source> getSourceType() {
        return Source.class;
    }

    @Override
    protected Class<Node> getTargetType() {
        return Node.class;
    }

    @Override
    public int getWeight() {
        return 40;
    }

}
