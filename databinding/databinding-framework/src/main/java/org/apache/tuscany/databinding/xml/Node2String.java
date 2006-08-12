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
package org.apache.tuscany.databinding.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.PullTransformer;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Transform DOM Node to XML String
 *
 */
public class Node2String implements PullTransformer<Node, String> {

    public String transform(Node source, TransformationContext context) {
        try {
            DOMImplementationLS impl = (DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
            LSSerializer serializer = impl.createLSSerializer();
            return serializer.writeToString(source);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<Node> getSourceType() {
        return Node.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

    public int getWeight() {
        return 40;
    }

}
