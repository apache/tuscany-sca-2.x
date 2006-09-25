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

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.annotations.Service;

@Service(Transformer.class)
public class String2OMElement extends TransformerExtension<String, OMElement> implements
        PullTransformer<String, OMElement> {

    @SuppressWarnings("unchecked")
    public OMElement transform(String source, TransformationContext context) {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(source.getBytes()));
            OMElement element = builder.getDocumentElement();
            adjustElementName(context, element);
            return element;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    /**
     * @param context
     * @param element
     */
    private void adjustElementName(TransformationContext context, OMElement element) {
        if (context != null) {
            DataType<QName> dataType = context.getTargetDataType();
            QName targetQName = dataType == null ? null : dataType.getLogical();
            if (targetQName != null && !element.getQName().equals(targetQName)) {
                // TODO: Throw expection or switch to the new Element
                OMFactory factory = OMAbstractFactory.getOMFactory();
                OMNamespace namespace =
                        factory.createOMNamespace(targetQName.getNamespaceURI(), targetQName.getPrefix());
                element.setNamespace(namespace);
                element.setLocalName(targetQName.getLocalPart());
            }
        }
    }

    public Class getTargetType() {
        return OMElement.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public int getWeight() {
        return 40;
    }

}
