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

package org.apache.tuscany.databinding.json.axiom;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.databinding.json.JSONDataBinding;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.XMLType;
import org.json.JSONObject;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(Transformer.class)
public class JSON2OMElement extends TransformerExtension<JSONObject, OMElement> implements
    PullTransformer<JSONObject, OMElement> {

    private OMFactory factory = OMAbstractFactory.getOMFactory();

    @Override
    protected Class getSourceType() {
        return JSONObject.class;
    }

    @Override
    protected Class getTargetType() {
        return OMElement.class;
    }

    public OMElement transform(JSONObject source, TransformationContext context) {
        try {
            String ns = JSONDataBinding.ROOT_ELEMENT.getNamespaceURI();
            String name = JSONDataBinding.ROOT_ELEMENT.getLocalPart();
            if (context != null) {
                DataType<?> dataType = context.getTargetDataType();
                Object logical = dataType.getLogical();
                if (logical instanceof XMLType) {
                    XMLType xmlType = (XMLType)logical;
                    if (xmlType.isElement()) {
                        ns = xmlType.getElementName().getNamespaceURI();
                        name = xmlType.getElementName().getLocalPart();
                    }
                }
            }
            JSONBadgerfishDataSource ds = new JSONBadgerfishDataSource(source);
            OMNamespace namespace = factory.createOMNamespace(ns, "");
            return factory.createOMElement(ds, name, namespace);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public int getWeight() {
        return 10;
    }

}
