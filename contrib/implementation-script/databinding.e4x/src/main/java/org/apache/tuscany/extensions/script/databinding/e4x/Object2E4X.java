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
package org.apache.tuscany.extensions.script.databinding.e4x;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.Java2SimpleTypeTransformer;
import org.apache.tuscany.spi.model.ElementInfo;
import org.mozilla.javascript.xmlimpl.XML;
import org.osoa.sca.annotations.Service;

/**
 * Transformer to convert data from an simple OMElement to Java Object
 */
@Service(Transformer.class)
public class Object2E4X extends Java2SimpleTypeTransformer<XML> {

    private OMFactory factory;
    private OMElement2E4X om2e4x;

    public Object2E4X() {
        factory = OMAbstractFactory.getOMFactory();
        om2e4x = new OMElement2E4X();
    }

    @Override
    public Class getTargetType() {
        return XML.class;
    }

    @Override
    protected XML createElement(ElementInfo element, String literal, TransformationContext context) {
        OMElement omElement = factory.createOMElement(element.getQName(), null);
        factory.createOMText(omElement, literal);
        return om2e4x.transform(omElement, context);
    }

}
