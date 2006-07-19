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
package org.apache.tuscany.databinding.trax;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.w3c.dom.Node;

/**
 * Push DOM InputStream to Node
 * 
 */
public class InputSource2Node implements PullTransformer<InputStream, Node> {
    private static final Source2ResultTransformer transformer = new Source2ResultTransformer();

    public Node transform(InputStream source, TransformationContext context) {
        try {
            Source streamSource = new StreamSource(source);
            DOMResult result = new DOMResult();
            transformer.transform(streamSource, result, context);
            return result.getNode();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<InputStream> getSourceType() {
        return InputStream.class;
    }

    public Class<Node> getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 40;
    }

}
