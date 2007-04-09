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

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.mozilla.javascript.xmlimpl.XML;
import org.osoa.sca.annotations.Service;

@Service(Transformer.class)
public class E4X2OMElement extends TransformerExtension<XML, OMElement> implements PullTransformer<XML, OMElement> {

    public OMElement transform(XML source, TransformationContext context) {
        return (OMElement)source.getAxiomFromXML();
    }

    public Class getSourceType() {
        return XML.class;
    }

    public Class getTargetType() {
        return OMElement.class;
    }

    public int getWeight() {
        return 10;
    }


}
