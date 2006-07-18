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

import java.io.Reader;

import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Transform XML string to SAX
 *
 */
public class Reader2SAX implements PushTransformer<Reader, ContentHandler> {
    public void transform(Reader source, ContentHandler target, TransformationContext context) {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(target);
            reader.parse(new InputSource(source));
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<Reader> getSourceType() {
        return Reader.class;
    }

    public Class<ContentHandler> getTargetType() {
        return ContentHandler.class;
    }

    public int getWeight() {
        return 40;
    }

}
