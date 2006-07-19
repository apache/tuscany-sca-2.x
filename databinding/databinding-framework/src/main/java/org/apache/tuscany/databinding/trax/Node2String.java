/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.databinding.trax;

import java.io.StringWriter;

import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.w3c.dom.Node;

/**
 * Transform DOM Node to XML String
 *
 */
public class Node2String implements PullTransformer<Node, String> {
    private static final Node2Writer transformer = new Node2Writer();

    public String transform(Node source, TransformationContext context) {
        try {
            StringWriter writer = new StringWriter();
            transformer.transform(source, writer, context);
            return writer.toString();
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
