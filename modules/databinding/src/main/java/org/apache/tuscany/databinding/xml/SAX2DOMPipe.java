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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.spi.databinding.DataPipe;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class SAX2DOMPipe extends TransformerExtension<ContentHandler, Node> implements
    DataPipe<ContentHandler, Node> {
    private SAX2DOM pipe;

    /**
     * 
     */
    public SAX2DOMPipe() {
        super();
        try {
            this.pipe = new SAX2DOM();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Node getResult() {
        return pipe.getDOM();
    }

    public Class getTargetType() {
        return Node.class;
    }

    public ContentHandler getSink() {
        return pipe;
    }

    public Class getSourceType() {
        return ContentHandler.class;
    }

    public int getWeight() {
        return 30;
    }

}
