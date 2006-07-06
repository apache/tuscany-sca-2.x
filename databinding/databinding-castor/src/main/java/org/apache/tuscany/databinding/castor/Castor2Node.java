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

package org.apache.tuscany.databinding.castor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.Transformer;
import org.exolab.castor.xml.Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Castor2Node implements Transformer<Object, Node> {

    public Class<Node> getResultType() {
        return Node.class;
    }

    public Class<Object> getSourceType() {
        return Object.class;
    }

    public int getWeight() {
        return 40;
    }

    /**
     * @see org.apache.tuscany.databinding.Transformer#transform(java.lang.Object, org.apache.tuscany.databinding.TransformationContext)
     */
    public Node transform(Object source, TransformationContext arg1) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Marshaller.marshal(source, document);
            return document;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
