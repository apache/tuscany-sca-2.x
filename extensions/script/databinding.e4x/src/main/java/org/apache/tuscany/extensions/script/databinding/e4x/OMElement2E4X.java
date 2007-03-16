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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.xmlimpl.XML;
import org.osoa.sca.annotations.Service;

@Service(Transformer.class)
public class OMElement2E4X extends TransformerExtension<OMElement, XML> implements PullTransformer<OMElement, XML> {

    private ScriptableObject scope;

    public OMElement2E4X() {
        Context cx = Context.enter();
        try {

            this.scope = cx.initStandardObjects();

        } finally {
            Context.exit();
        }
    }

    public Class getSourceType() {
        return OMElement.class;
    }

    public Class getTargetType() {
        return XML.class;
    }

    public int getWeight() {
        return 10;
    }

    public XML transform(OMElement source, TransformationContext context) {
        Context cx = Context.enter();
        try {

            return (XML)cx.newObject(scope, "XML", new Object[] {source});

        } finally {
            Context.exit();
        }
    }

}
