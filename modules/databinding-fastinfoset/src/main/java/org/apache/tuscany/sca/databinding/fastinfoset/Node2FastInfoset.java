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

package org.apache.tuscany.sca.databinding.fastinfoset;

import java.io.OutputStream;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.w3c.dom.Node;

import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;

/**
 * @version $Rev$ $Date$
 */
public class Node2FastInfoset extends BaseTransformer<Node, OutputStream> implements
    PushTransformer<Node, OutputStream> {

    @Override
    protected Class getSourceType() {
        return Node.class;
    }

    @Override
    protected Class getTargetType() {
        return OutputStream.class;
    }

    
    /**
     * @see org.apache.tuscany.sca.databinding.PushTransformer#transform(java.lang.Object, java.lang.Object, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public void transform(Node source, OutputStream target, TransformationContext context) {
        try {
            DOMDocumentSerializer serializer = new DOMDocumentSerializer();
            serializer.setOutputStream(target);
            serializer.serialize(source);
        } catch (Exception e) {
            throw new TransformationException(e);
        } 
    }

    public int getWeight() {
        return 20;
    }

    @Override
    public String getTargetDataBinding() {
        return "xml:fastinfoset:java.io.OutputStream";
    }
}
