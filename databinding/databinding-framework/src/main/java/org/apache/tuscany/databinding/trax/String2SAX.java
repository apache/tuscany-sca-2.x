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

import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.xml.sax.ContentHandler;

/**
 * Transform XML string to SAX
 *
 */
public class String2SAX implements PushTransformer<String, ContentHandler> {
    private static final TransformerFactory factory = TransformerFactory.newInstance();

    public void transform(String source, ContentHandler target, TransformationContext context) {
        try {
            javax.xml.transform.Transformer transformer = factory.newTransformer();
            Source domSource = new StreamSource(new StringReader(source));
           
            Result result = new SAXResult(target);
            transformer.transform(domSource, result);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<String> getSourceType() {
        return String.class;
    }

    public Class<ContentHandler> getTargetType() {
        return ContentHandler.class;
    }

    public int getWeight() {
        return 40;
    }

}
