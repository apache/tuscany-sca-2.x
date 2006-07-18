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
package org.apache.tuscany.databinding.axiom;

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.PullTransformer;

public class String2OMElement implements PullTransformer<String, OMElement> {

    public OMElement transform(String source, TransformationContext context) {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(source.getBytes()));
            return builder.getDocumentElement();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<OMElement> getTargetType() {
        return OMElement.class;
    }

    public Class<String> getSourceType() {
        return String.class;
    }

    public int getWeight() {
        return 40;
    }

}
