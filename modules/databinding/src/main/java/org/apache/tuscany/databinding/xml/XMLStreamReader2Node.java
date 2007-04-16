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

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.w3c.dom.Node;

/**
 * Transform DOM Node to XML XMLStreamReader
 */
public class XMLStreamReader2Node extends TransformerExtension<XMLStreamReader, Node> implements
    PullTransformer<XMLStreamReader, Node> {
    private SAX2DOMPipe pipe = new SAX2DOMPipe();

    private XMLStreamReader2SAX stax2sax = new XMLStreamReader2SAX();

    public Node transform(XMLStreamReader source, TransformationContext context) {
        try {
            stax2sax.transform(source, pipe.getSink(), context);
            return pipe.getResult();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return XMLStreamReader.class;
    }

    public Class getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 40;
    }

}
