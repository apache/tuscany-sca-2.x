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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomReader;

/**
 * DOM Node --> XStream transformer
 * 
 * @version $Rev$ $Date$
 */
public class Node2XObject extends BaseTransformer<Node, XObject> implements PullTransformer<Node, XObject> {

    public XObject transform(Node source, TransformationContext context) {
        try {
            DomReader in = null;
            if (source instanceof Document) {
                in = new DomReader((Document)source);
            } else if (source instanceof Element) {
                in = new DomReader((Element)source);
            }
            XStream xs = new XStream();
            return (XObject)xs.unmarshal(in);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public Class<Node> getSourceType() {
        return Node.class;
    }

    @Override
    public Class<XObject> getTargetType() {
        return XObject.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
