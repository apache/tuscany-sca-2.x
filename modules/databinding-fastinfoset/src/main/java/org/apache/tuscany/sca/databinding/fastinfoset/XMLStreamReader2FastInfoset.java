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

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.xml.XMLStreamSerializer;
import org.xml.sax.ContentHandler;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

/**
 * @version $Rev$ $Date$
 */
public class XMLStreamReader2FastInfoset extends BaseTransformer<XMLStreamReader, ContentHandler> implements
    PushTransformer<XMLStreamReader, OutputStream> {

    @Override
    protected Class getSourceType() {
        return XMLStreamReader.class;
    }

    @Override
    protected Class getTargetType() {
        return OutputStream.class;
    }

    
    /**
     * @see org.apache.tuscany.sca.databinding.PushTransformer#transform(java.lang.Object, java.lang.Object, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public void transform(XMLStreamReader source, OutputStream target, TransformationContext context) {
        try {
            StAXDocumentSerializer serializer = new StAXDocumentSerializer(target);
            XMLStreamSerializer streamSerializer = new XMLStreamSerializer();
            streamSerializer.serialize(source, serializer);
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
