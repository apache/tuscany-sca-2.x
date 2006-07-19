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
package org.apache.tuscany.databinding.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.PullTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class JAXB2Node implements PullTransformer<Object, Node> {

    private String contextPath;

    public Node transform(Object source, TransformationContext transformationContext) {
        if (source == null)
            return null;
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Marshaller marshaller = context.createMarshaller();
            // FIXME: The default Marshaller doesn't support marshaller.getNode()
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            marshaller.marshal(source, document);
            return document;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<Object> getSourceType() {
        return Object.class;
    }

    public Class<Node> getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 30;
    }

    public JAXB2Node(String contextPath) {
        super();
        this.contextPath = contextPath;
    }

}
