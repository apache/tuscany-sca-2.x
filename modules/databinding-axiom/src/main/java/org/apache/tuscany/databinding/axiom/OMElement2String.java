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

import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.extension.TransformerExtension;

/**
 * Transformer to convert data from an OMElement to XML String
 */
public class OMElement2String extends TransformerExtension<OMElement, String> implements PullTransformer<OMElement, String> {
    // private XmlOptions options;
    
    public String transform(OMElement source, TransformationContext context) {
        try {
            StringWriter writer = new StringWriter();
            source.serialize(writer);
            return writer.toString();
        } catch (XMLStreamException e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return OMElement.class;
    }

    public Class getTargetType() {
        return String.class;
    }

    public int getWeight() {
        return 40;
    }

}
