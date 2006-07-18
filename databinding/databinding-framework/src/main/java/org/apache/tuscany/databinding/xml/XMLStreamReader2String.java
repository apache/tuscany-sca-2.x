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
package org.apache.tuscany.databinding.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.PullTransformer;

public class XMLStreamReader2String implements PullTransformer<XMLStreamReader, String> {

    public String transform(XMLStreamReader source, TransformationContext context) {
        try {
            return StAXHelper.save(source);
        } catch (XMLStreamException e) {
            throw new TransformationException(e);
        }
    }

    public Class<XMLStreamReader> getSourceType() {
        return XMLStreamReader.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

    public int getWeight() {
        return 40;
    }

}
