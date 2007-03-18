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
package org.apache.tuscany.databinding.axiom;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.osoa.sca.annotations.Service;

@Service(Transformer.class)
public class XMLStreamReader2OMElement extends TransformerExtension<XMLStreamReader, OMElement> implements
    PullTransformer<XMLStreamReader, OMElement> {

    public XMLStreamReader2OMElement() {
        super();
    }

    public OMElement transform(XMLStreamReader source, TransformationContext context) {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(source);
            OMElement element = builder.getDocumentElement();
            AxiomHelper.adjustElementName(context, element);
            return element;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getTargetType() {
        return OMElement.class;
    }

    public Class getSourceType() {
        return XMLStreamReader.class;
    }

    public int getWeight() {
        return 10;
    }

}
