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
package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class JAXB2Node extends BaseTransformer<Object, Node> implements PullTransformer<Object, Node> {

    public Node transform(Object source, TransformationContext tContext) {
        if (source == null) {
            return null;
        }
        try {
            JAXBContext context = JAXBContextHelper.createJAXBContext(tContext, true);
            Marshaller marshaller = context.createMarshaller();
            // FIXME: The default Marshaller doesn't support
            // marshaller.getNode()
            Document document = DOMHelper.newDocument();
            JAXBElement<?> jaxbElement = JAXBContextHelper.createJAXBElement(tContext.getSourceDataType(), source);
            marshaller.marshal(jaxbElement, document);
            return document;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return Object.class;
    }

    public Class getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 30;
    }

    @Override
    public String getSourceDataBinding() {
        return JAXBDataBinding.NAME;
    }
}
