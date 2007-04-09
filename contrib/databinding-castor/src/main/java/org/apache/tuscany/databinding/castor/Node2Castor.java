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
package org.apache.tuscany.databinding.castor;

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.exolab.castor.xml.Unmarshaller;
import org.w3c.dom.Node;

public class Node2Castor<T> extends TransformerExtension<Node, T> implements PullTransformer<Node, T> {
    private Class<T> type;

    /**
     * @param type
     */
    public Node2Castor(Class<T> type) {
        super();
        this.type = type;
    }

    public Class getTargetType() {
        return type;
    }

    public Class getSourceType() {
        return Node.class;
    }

    public int getWeight() {
        return 40;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.PullTransformer#transform(java.lang.Object, org.apache.tuscany.spi.databinding.TransformationContext)
     */
    public T transform(Node source, TransformationContext context) {
        try {
            Object object = Unmarshaller.unmarshal(type, source);
            return type.cast(object);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
