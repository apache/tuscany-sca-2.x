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
package org.apache.tuscany.databinding.sdo;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.sdo.helper.XMLStreamHelper;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;

public class XMLDocument2XMLStreamReader implements Transformer<XMLDocument, XMLStreamReader> {
    private XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(TypeHelper.INSTANCE);

    /**
     * @param source
     * @param context
     * @return
     */
    public XMLStreamReader transform(XMLDocument source, TransformationContext context) {
        try {
            return streamHelper.createXMLStreamReader(source);
        } catch (XMLStreamException e) {
            throw new TransformationException(e);
        }
    }

    public Class<commonj.sdo.helper.XMLDocument> getSourceType() {
        return XMLDocument.class;
    }

    public Class<XMLStreamReader> getResultType() {
        return XMLStreamReader.class;
    }

    public int getWeight() {
        return 10;
    }

}
