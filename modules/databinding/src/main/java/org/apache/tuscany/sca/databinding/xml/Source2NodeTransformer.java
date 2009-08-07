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

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transform TrAX Source to Node
 *
 * @version $Rev$ $Date$
 */
public class Source2NodeTransformer extends BaseTransformer<Source, Node> implements
    PullTransformer<Source, Node> {
    private DOMHelper helper;
    
    public Source2NodeTransformer(ExtensionPointRegistry registry) {
        super();
        helper = DOMHelper.getInstance(registry);
    }
    
    public Node transform(Source source, TransformationContext context) {
        try {
            Document doc = helper.load(source);
            return DOMDataBinding.adjustElementName(context, doc.getDocumentElement());
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
