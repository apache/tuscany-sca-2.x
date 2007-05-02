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

import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.impl.BaseTransformer;
import org.xml.sax.ContentHandler;

/**
 * XMLStreamReader to SAX events
 */
public class XMLStreamReader2SAX extends BaseTransformer<XMLStreamReader, ContentHandler> implements
    PushTransformer<XMLStreamReader, ContentHandler> {

    /**
     * @see org.apache.tuscany.databinding.PushTransformer#getSourceType()
     */
    public Class getTargetType() {
        return ContentHandler.class;
    }

    /**
     * @see org.apache.tuscany.databinding.PushTransformer#getSourceType()
     */
    public Class getSourceType() {
        return XMLStreamReader.class;
    }

    /**
     * @see org.apache.tuscany.databinding.PushTransformer#getWeight()
     */
    public int getWeight() {
        return 20;
    }

    /**
     * @see org.apache.tuscany.databinding.PushTransformer#transform(java.lang.Object,
     *      java.lang.Object,
     *      org.apache.tuscany.databinding.TransformationContext)
     */
    public void transform(XMLStreamReader source, ContentHandler sink, TransformationContext context) {
        StAX2SAXAdapter adapter = new StAX2SAXAdapter(false);
        try {
            adapter.parse(source, sink);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
