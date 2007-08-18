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

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;

/**
 * @version $Rev$ $Date$
 */
public class XMLInputStream2FastInfoset extends BaseTransformer<InputStream, OutputStream> implements
    PushTransformer<InputStream, OutputStream> {

    @Override
    protected Class getSourceType() {
        return InputStream.class;
    }

    @Override
    protected Class getTargetType() {
        return OutputStream.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.PushTransformer#transform(java.lang.Object, java.lang.Object, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public void transform(InputStream source, OutputStream target, TransformationContext context) {
        try {

            // Create Fast Infoset SAX serializer
            SAXDocumentSerializer saxDocumentSerializer = new SAXDocumentSerializer();
            // Set the output stream
            saxDocumentSerializer.setOutputStream(target);

            // Instantiate JAXP SAX parser factory
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            /* Set parser to be namespace aware
             * Very important to do otherwise invalid FI documents will be
             * created by the SAXDocumentSerializer
             */
            saxParserFactory.setNamespaceAware(true);
            // Instantiate the JAXP SAX parser
            SAXParser saxParser = saxParserFactory.newSAXParser();
            // Set the lexical handler
            saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", saxDocumentSerializer);
            // Parse the XML document and convert to a fast infoset document
            saxParser.parse(source, saxDocumentSerializer);
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
