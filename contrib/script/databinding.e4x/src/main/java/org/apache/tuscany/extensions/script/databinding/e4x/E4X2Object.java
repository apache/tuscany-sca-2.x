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

import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.SimpleType2JavaTransformer;
import org.mozilla.javascript.xmlimpl.XML;
import org.osoa.sca.annotations.Service;

/**
 * Transformer to convert data from a simple java bject to OMElement
 */
@Service(Transformer.class)
public class E4X2Object extends SimpleType2JavaTransformer<XML> {

    private E4X2OMElement e4x2om;

    public E4X2Object() {
        e4x2om = new E4X2OMElement();
    }

    @Override
    protected String getText(XML source) {
        return e4x2om.transform(source, null).getText();
    }

    public Class getSourceType() {
        return XML.class;
    }
}
