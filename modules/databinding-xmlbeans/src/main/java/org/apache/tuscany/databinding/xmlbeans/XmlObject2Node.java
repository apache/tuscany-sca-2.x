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
package org.apache.tuscany.databinding.xmlbeans;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.Transformer;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.xmlbeans.XmlObject;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Node;

@Service(Transformer.class)
public class XmlObject2Node extends BaseTransformer<XmlObject, Node> implements
    PullTransformer<XmlObject, Node> {
    // private XmlOptions options;

    public Node transform(XmlObject source, TransformationContext context) {
        if (source == null)
            return null;
        return source.newDomNode();
    }

    public Class getSourceType() {
        return XmlObject.class;
    }

    public Class getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 30;
    }

}
