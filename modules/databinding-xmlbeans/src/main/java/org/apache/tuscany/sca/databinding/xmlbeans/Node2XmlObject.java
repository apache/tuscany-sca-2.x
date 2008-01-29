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
package org.apache.tuscany.sca.databinding.xmlbeans;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

public class Node2XmlObject extends BaseTransformer<Node, XmlObject> implements
    PullTransformer<Node, XmlObject> {
    // private XmlOptions options;

    public XmlObject transform(Node source, TransformationContext context) {
        try {
            return XmlObject.Factory.parse(source);
        } catch (XmlException e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public Class getTargetType() {
        return XmlObject.class;
    }

    @Override
    public Class getSourceType() {
        return Node.class;
    }

    @Override
    public int getWeight() {
        return 30;
    }

}
